package com.indopay.qrissapp.core.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.indopay.qrissapp.core.data.db.QrisDb
import com.indopay.qrissapp.core.data.entity.DataLastTrxItemEntity
import com.indopay.qrissapp.core.data.paging.LastTransactionRemoteMediator
import com.indopay.qrissapp.core.data.paging.ListTrxByDateRemoteMediator
import com.indopay.qrissapp.core.network.api.ApiService
import com.indopay.qrissapp.core.network.utils.ErrorCode.ERR_EXCEPTION_CODE
import com.indopay.qrissapp.core.network.utils.ErrorCode.ERR_INTERNET_CONNECTION
import com.indopay.qrissapp.core.network.utils.ErrorCode.NOT_FOUND_ERR
import com.indopay.qrissapp.core.network.utils.ErrorCode.REQUEST_TIME_OUT
import com.indopay.qrissapp.core.network.utils.ErrorCode.SERVER_ERR
import com.indopay.qrissapp.core.network.utils.ErrorCode.UNAUTHORIZATION_ERR
import com.indopay.qrissapp.core.network.utils.ErrorCode.UNKNOWN_HOST_ERROR
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.domain.model.DataLastTransactionItem
import com.indopay.qrissapp.domain.model.DataTrxItemByDate
import com.indopay.qrissapp.domain.model.Login
import com.indopay.qrissapp.domain.model.LoginVerify
import com.indopay.qrissapp.domain.model.Logout
import com.indopay.qrissapp.domain.model.ProfileMerchant
import com.indopay.qrissapp.domain.model.TransactionDetail
import com.indopay.qrissapp.domain.repository.AQrisRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class QrisRepository(
    private val db: QrisDb,
    private val apiService: ApiService,
) : AQrisRepository {

    override fun requestLogin(email: String): Flow<Resource<Login>> {
        return flow {
            emit(Resource.Loading(null))
            try {
                val response = apiService.loginUser(email)
                val loginEntity = response.toLoginEntity()
                val loginData = loginEntity.toLogin()

                emit(Resource.Success(loginData))
            } catch (e: IOException) {
                e.stackTraceToString()
                emit(Resource.Error(ERR_INTERNET_CONNECTION, e.message.toString(), null))
            } catch (e: SocketTimeoutException) {
                e.stackTraceToString()
                emit(Resource.Error(REQUEST_TIME_OUT, e.message.toString(), null))
            } catch (e: UnknownHostException) {
                e.stackTraceToString()
                emit(Resource.Error(UNKNOWN_HOST_ERROR, e.message.toString(), null))
            } catch (e: Exception) {
                if (e is HttpException) {
                    when (e.code()) {
                        SERVER_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        NOT_FOUND_ERR -> {
                            e.printStackTrace()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        UNAUTHORIZATION_ERR -> {
                            e.printStackTrace()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                    }
                } else {
                    e.stackTraceToString()
                    emit(Resource.Error(ERR_EXCEPTION_CODE, e.message.toString(), null))
                }
            }
        }
    }

    override fun requestVerifyLoginWithOtp(email: String, otpNumber: String): Flow<Resource<LoginVerify>> {
        return flow {
            emit(Resource.Loading(null))

            try {
                val response = apiService.loginVerifyWithOtp(email, otpNumber)

                if (response.status != "00") {
                    emit(
                        Resource.Error(
                            statusCode = response.status.toInt(),
                            message = response.message,
                            data = null
                        )
                    )
                }

                val verifyEntity = response.dataVerifyResponse.toLoginVerifyEntity()
                val dataVerify = verifyEntity.toLoginVerify(response.status, response.message)

                emit(Resource.Success(dataVerify))
            } catch (e: IOException) {
                e.stackTraceToString()
                emit(Resource.Error(ERR_INTERNET_CONNECTION, e.message.toString(), null))
            } catch (e: SocketTimeoutException) {
                e.stackTraceToString()
                emit(Resource.Error(REQUEST_TIME_OUT, e.message.toString(), null))
            } catch (e: UnknownHostException) {
                e.stackTraceToString()
                emit(Resource.Error(UNKNOWN_HOST_ERROR, e.message.toString(), null))
            } catch (e: Exception) {
                if (e is HttpException) {
                    when (e.code()) {
                        SERVER_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        UNAUTHORIZATION_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        NOT_FOUND_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                    }
                } else {
                    e.stackTraceToString()
                    emit(Resource.Error(ERR_EXCEPTION_CODE, e.message.toString(), null))
                }
            }
        }
    }

    override fun requestLogout(): LiveData<Resource<Logout>> {
        return liveData { }
    }

    override fun getProfileMerchant(
        authToken: String,
        username: String
    ): Flow<Resource<ProfileMerchant>> {
        return flow {

            emit(Resource.Loading(null))

            var status: String? = "none"
            var message: String? = "none"

            try {

                val response = apiService.getProfileByUsername(authToken, username)

                status = response.status
                message = response.message

                val cached = db.qrisDao().getProfileMerchantFromEntity()
                    ?.toProfileMerchantDomain(status, message)

//            emit(Resource.Loading(cached))
                if (response.status != "00") {
                    emit(Resource.Error(response.status?.toIntOrNull() ?: 0, response.message ?: "none", null))
                } else {
                    val profileEntity = response.dataProfile?.toProfileMerchantEntity()

                    db.withTransaction {
                        if (profileEntity != null) {
                            db.qrisDao().deleteProfileMerchantEntity()
                            db.qrisDao().insertProfileMerchantToEntity(profileEntity)
                        }
                    }
                }

//                val profileDomain = db.qrisDao().getProfileMerchantFromEntity()?.toProfileMerchantDomain(
//                    response.status, response.message
//                )
//                profileDomain?.let {
//                    emit(Resource.Success(it))
//                }
            } catch (e: IOException) {
                e.stackTraceToString()
                emit(Resource.Error(ERR_INTERNET_CONNECTION, e.message.toString(), null))
            } catch (e: SocketTimeoutException) {
                e.stackTraceToString()
                emit(Resource.Error(REQUEST_TIME_OUT, e.message.toString(), null))
            } catch (e: UnknownHostException) {
                e.stackTraceToString()
                emit(Resource.Error(UNKNOWN_HOST_ERROR, e.message.toString(), null))
            } catch (e: Exception) {
                if (e is HttpException) {
                    when (e.code()) {
                        SERVER_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        NOT_FOUND_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        UNAUTHORIZATION_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                    }
                }
                e.stackTraceToString()
                emit(Resource.Error(ERR_EXCEPTION_CODE, e.message.toString(), null))
            }

            val dataLastProfileDomain = db.qrisDao().getProfileMerchantFromEntity()?.toProfileMerchantDomain(
                status,
                message
            )

            if (dataLastProfileDomain != null) {
                emit(Resource.Success(dataLastProfileDomain))
            }
        }
    }

    override fun getLastTransaction(
        authToken: String,
        username: String,
        mID: String
    ): Flow<Resource<List<DataLastTransactionItem>>> {
        return flow {
            emit(Resource.Loading(null))

            var status: String? = "none"
            var message: String? = "none"

            try {
                val response = apiService.getLastTransaction(
                    authToken = authToken,
                    page = 1,
                    pageSize = 6,
                    username = username,
                    mID = mID
                )

                status = response.status
                message = response.message

//                val cached = db.qrisDao().getAllLastTransactionFromEntity()
//                val cachedToDomain = cached.map { it.toDataLastTrxItemDomain(status, message) }

                if (response.status != "00") {
                    emit(
                        Resource.Error(
                            response.status?.toIntOrNull() ?: 0,
                            response.message.toString(),
                            null
                        )
                    )
                } else {
                    val listEntity = response.data?.map {
                        it!!.toDataLastTrxItemEntity(
                            statusResponse = status,
                            messageResponse = message
                        )
                    }

                    db.withTransaction {
                        if (listEntity != null) {
                            db.qrisDao().deleteAllLastTrxFromEntity()
                            db.qrisDao().insertAllLastTransactionToEntity(listEntity)
                        }
                    }
                }


//                val lastTrxDomain = db.qrisDao().getAllLastTransactionFromEntity().map { it.toDataLastTrxItemDomain() }
//                emit(Resource.Success(lastTrxDomain))

            } catch (e: IOException) {
                e.stackTraceToString()
                emit(Resource.Error(ERR_INTERNET_CONNECTION, e.message.toString(), null))
            } catch (e: SocketTimeoutException) {
                e.stackTraceToString()
                emit(Resource.Error(REQUEST_TIME_OUT, e.message.toString(), null))
            } catch (e: UnknownHostException) {
                e.stackTraceToString()
                emit(Resource.Error(UNKNOWN_HOST_ERROR, e.message.toString(), null))
            } catch (e: Exception) {
                if (e is HttpException) {
                    when (e.code()) {
                        SERVER_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        NOT_FOUND_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        UNAUTHORIZATION_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                    }
                } else {
                    e.stackTraceToString()
                    emit(Resource.Error(ERR_EXCEPTION_CODE, e.message.toString(), null))
                }
            }

            val dataLastTrx = db.qrisDao().getAllLastTransactionFromEntity().map { it.toDataLastTrxItemDomain() }
            emit(Resource.Success(dataLastTrx))
        }
    }

    override fun getTransactionDetail(
        token: String,
        params: Map<String, String>
    ): Flow<Resource<TransactionDetail>> {
        return flow {
            val idTrx = params["idTrx"]

            emit(Resource.Loading(null))

            var status: String? = "none"
            var message: String? = "none"

            try {

                val response = apiService.getTransactionDetail(
                    token,
                    params
                )

                status = response.status
                message = response.message

                val cached = db.qrisDao().getTrxDetailFromEntity(idTrx)

                val cachedToDomain =
                    cached?.toTransactionDetailDomain(status, message)

//            emit(Resource.Loading(cachedToDomain))
                if (response.status != "00") {
                    emit(
                        Resource.Error(
                            response.status?.toIntOrNull() ?: 0,
                            response.message ?: "none",
                            cachedToDomain
                        )
                    )
                } else {
                    val trxDetailEntity = response.dataTransactionDetail?.toTransactionDetailEntity()

                    if (trxDetailEntity != null) {
                        idTrx?.let { id -> db.qrisDao().deleteTrxDetailFromEntity(id) }
                        db.qrisDao().insertTransactionDetailToEntity(trxDetailEntity)
                    }
                }


            } catch (e: IOException) {
                e.stackTraceToString()
                emit(Resource.Error(ERR_INTERNET_CONNECTION, e.message.toString(), null))
            } catch (e: SocketTimeoutException) {
                e.stackTraceToString()
                emit(Resource.Error(REQUEST_TIME_OUT, e.message.toString(), null))
            } catch (e: UnknownHostException) {
                e.stackTraceToString()
                emit(Resource.Error(UNKNOWN_HOST_ERROR, e.message.toString(), null))
            } catch (e: Exception) {
                if (e is HttpException) {
                    when (e.code()) {
                        SERVER_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        NOT_FOUND_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                        UNAUTHORIZATION_ERR -> {
                            e.stackTraceToString()
                            emit(Resource.Error(e.code(), e.message.toString(), null))
                        }
                    }
                } else {
                    e.stackTraceToString()
                    emit(Resource.Error(ERR_EXCEPTION_CODE, e.message.toString(), null))
                }
            }

            val dataLastTrxDetail = db.qrisDao().getTrxDetailFromEntity(idTrx)?.toTransactionDetailDomain(
                status,
                message
            )

            if (dataLastTrxDetail != null) {
                emit(Resource.Success(dataLastTrxDetail))
            }
        }
    }

    override fun getTransactionByDateWithPagingThree(
        authToken: String,
        map: Map<String, String>
    ): Flow<PagingData<DataTrxItemByDate>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = ListTrxByDateRemoteMediator(
                authToken,
                map,
                apiService,
                db
            ),
            pagingSourceFactory = {
                db.qrisDao().getAllTransactionByDateFromEntity()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDataTrxItemByDateDomain() }
        }
    }

    override fun getlastTransactionWithPagingThree(
        authToken: String,
        username: String,
        mId: String,
    ): Flow<PagingData<DataLastTrxItemEntity>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = LastTransactionRemoteMediator(
                authToken, username, mId, apiService, db
            ),
            pagingSourceFactory = {
                db.qrisDao().getAllLastTransactionFromEntityUsingPaging()
            },
        ).flow
    }
}