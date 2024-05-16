package com.indopay.qrissapp.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.PagingData
import com.indopay.qrissapp.domain.model.DataTrxItemByDate
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrxListByDateViewModel @Inject constructor(
    private val useCase: QrisUseCase,
) : ViewModel() {
    fun getTrxListByDate(
        authToken: String,
        email: String,
        mId: String,
        firstDate: String,
        lastDate: String
    ) : LiveData<PagingData<DataTrxItemByDate>> {
        val mappingData = mutableMapOf<String, String>().apply {
            put("username", email)
            put("MID", mId)
            put("firstDate", firstDate)
            put("lastDate", lastDate)
        }

        return useCase.getTransactionByDate(authToken, mappingData).asLiveData()
    }
}