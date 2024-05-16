package com.indopay.qrissapp.ui.transaction.trx_detail

import androidx.lifecycle.ViewModel
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.domain.model.TransactionDetail
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val qrisUseCase: QrisUseCase,
    dataStorePreference: DataStorePreference
) : ViewModel() {

    fun getTransactionDetailById(
        token: String,
        mId: String,
        email: String,
        idTrx: String
    ) : Flow<Resource<TransactionDetail>> {
        val params = mutableMapOf<String, String>().apply {
            put("username", email)
            put("MID", mId)
            put("idTrx", idTrx)
        }
        return qrisUseCase.getTransactionDetail(token, params).flowOn(Dispatchers.IO)
    }

    val getEmailFromDataStore = dataStorePreference.readEmailFromDataStore()
    val getTokenFromDataStore = dataStorePreference.readTokenFromDataStore()
    val getMerchantIdFromDataStore = dataStorePreference.readMidFromDataStore()
}