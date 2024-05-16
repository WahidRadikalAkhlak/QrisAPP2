package com.indopay.qrissapp.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.paging.map
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LastTrxViewModel @Inject constructor(
    private val qrisUseCase: QrisUseCase,
    dataStorePreference: DataStorePreference
) : ViewModel() {

    fun getLastTrxWithPagingThree(
        authToken: String,
        email: String,
        mId: String
    ) = qrisUseCase.getlastTransactionWithPagingThree(authToken, email, mId)
        .asLiveData().map { pagingData ->
            pagingData.map { it.toDataLastTrxItemDomain() }
        }

    val readTokenFromDataStore = dataStorePreference.readTokenFromDataStore().asLiveData()
    val readEmailFromDataStore = dataStorePreference.readEmailFromDataStore().asLiveData()
    val readMerchantIdFromDataStore = dataStorePreference.readMidFromDataStore().asLiveData()
}