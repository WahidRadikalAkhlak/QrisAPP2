package com.indopay.qrissapp.ui.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.indopay.qrissapp.core.data.datastore.DataStorePreference
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
private val qrisUseCase: QrisUseCase,
private val dataStorePreference: DataStorePreference

): ViewModel(){
    fun requestVerifyLoginWithOtp(email: String, otpNumber: String) =
        qrisUseCase.requestVerifyLoginWithOtp(email, otpNumber).asLiveData()

    fun saveTokenToDataStore(token: String) {
        viewModelScope.launch {
            dataStorePreference.saveTokenToDataStore(token)
        }
    }

    fun saveEmailToDataStore(email: String) {
        viewModelScope.launch {
            dataStorePreference.saveEmailToDataStore(email)
        }
    }

    fun saveLoginState() = viewModelScope.launch {
        dataStorePreference.saveLoginStateDataStore()
    }
}