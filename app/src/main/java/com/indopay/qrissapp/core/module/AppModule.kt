package com.indopay.qrissapp.core.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.indopay.qrissapp.BuildConfig
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import com.indopay.qrissapp.core.data.db.QrisDb
import com.indopay.qrissapp.core.network.utils.SslUtils
import com.indopay.qrissapp.core.network.api.ApiService
import com.indopay.qrissapp.core.repository.QrisRepository
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import com.indopay.qrissapp.domain.repository.AQrisRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.util.Arrays
import javax.inject.Singleton
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUseCase(repository: AQrisRepository) : QrisUseCase {
        return QrisUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context) : DataStorePreference {
        return DataStorePreference(context)
    }

    @Provides
    @Singleton
    fun provideRepository(apiService: ApiService, db: QrisDb) : AQrisRepository {
        return QrisRepository(db, apiService)
    }

    @Provides
    @Singleton
    fun provideApiService(context: Application) : ApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(null as KeyStore?)
        val trustManagers = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(
                trustManagers
            )
        }

        val trustManager = trustManagers[0] as X509TrustManager

        val client = OkHttpClient.Builder()
            .addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(ChuckerCollector(context))
                    .maxContentLength(250000L)
                    .redactHeaders(emptySet())
                    .alwaysReadResponseBody(false)
                    .build()
            )
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(
                sslSocketFactory = SslUtils.getSslContextForCertificateFile(context, "my_service_certifcate.pem").socketFactory,
                trustManager = trustManager
            )
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Application) : QrisDb {
        return Room.databaseBuilder(
            context.applicationContext,
            QrisDb::class.java,
            "qris.db",
        )
            .fallbackToDestructiveMigration()
            .build()
    }


}