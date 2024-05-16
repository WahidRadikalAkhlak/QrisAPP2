package com.indopay.qrissapp.ui.profile

import androidx.lifecycle.ViewModel
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val qrisUseCase: QrisUseCase) : ViewModel() {
    fun getProfileMerchant(authToken: String, username: String) =
        qrisUseCase.getProfileMerchant(authToken, username)
}