package com.indopay.qrissapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.indopay.qrissapp.domain.usecase.QrisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val qrisUseCase: QrisUseCase,
) : ViewModel() {
    fun loginRequest(email: String) = qrisUseCase.requestLogin(email).asLiveData()
}