package com.indopay.qrissapp.domain.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.indopay.qrissapp.core.data.entity.DataLastTrxItemEntity
import com.indopay.qrissapp.core.network.utils.Resource
import com.indopay.qrissapp.domain.model.DataLastTransactionItem
import com.indopay.qrissapp.domain.model.DataTrxItemByDate
import com.indopay.qrissapp.domain.model.Login
import com.indopay.qrissapp.domain.model.LoginVerify
import com.indopay.qrissapp.domain.model.Logout
import com.indopay.qrissapp.domain.model.ProfileMerchant
import com.indopay.qrissapp.domain.model.TransactionDetail
import kotlinx.coroutines.flow.Flow

interface AQrisRepository {
    fun requestLogin(email: String): Flow<Resource<Login>>

    fun requestVerifyLoginWithOtp(email: String, otpNumber: String): Flow<Resource<LoginVerify>>

    fun requestLogout(): LiveData<Resource<Logout>>

    fun getProfileMerchant(authToken: String, username: String): Flow<Resource<ProfileMerchant>>

    fun getLastTransaction(authToken: String, username: String, mId: String):
            Flow<Resource<List<DataLastTransactionItem>>>

    fun getTransactionDetail(token: String, params: Map<String, String>):
            Flow<Resource<TransactionDetail>>

    fun getTransactionByDateWithPagingThree(authToken: String, map: Map<String, String>):
            Flow<PagingData<DataTrxItemByDate>>

    fun getlastTransactionWithPagingThree(authToken: String, username: String, mId: String):
            Flow<PagingData<DataLastTrxItemEntity>>

}