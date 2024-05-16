package com.indopay.qrissapp.domain.usecase

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
import com.indopay.qrissapp.domain.repository.AQrisRepository
import kotlinx.coroutines.flow.Flow

class   QrisUseCase (private val qrisRepository: AQrisRepository) {
    fun requestLogin(email: String): Flow<Resource<Login>> =
        qrisRepository.requestLogin(email)

    fun requestVerifyLoginWithOtp(email: String, otpNumber: String): Flow<Resource<LoginVerify>> =
        qrisRepository.requestVerifyLoginWithOtp(email, otpNumber)

    fun requestLogout(): LiveData<Resource<Logout>> =
        qrisRepository.requestLogout()
    fun getProfileMerchant(authToken: String, username: String): Flow<Resource<ProfileMerchant>> =
        qrisRepository.getProfileMerchant(authToken, username)

    fun getLastTransaction(authToken: String, username: String, mId: String)
            : Flow<Resource<List<DataLastTransactionItem>>> =
        qrisRepository.getLastTransaction(
            authToken,
            username,
            mId
        )

    fun getTransactionDetail(token: String, params: Map<String, String>)
            : Flow<Resource<TransactionDetail>> =
        qrisRepository.getTransactionDetail(token, params)

    fun getTransactionByDate(authToken: String, map: Map<String, String>): Flow<PagingData<DataTrxItemByDate>> =
        qrisRepository.getTransactionByDateWithPagingThree(authToken, map)

    fun getlastTransactionWithPagingThree(
        authToken: String,
        username: String,
        mId: String
    ): Flow<PagingData<DataLastTrxItemEntity>> {
        return qrisRepository.getlastTransactionWithPagingThree(authToken, username, mId)
    }
}