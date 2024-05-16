package com.indopay.qrissapp.core.data.entity

import android.os.Parcelable
import com.indopay.qrissapp.domain.model.LoginVerify
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginVerifyEntity(
    val email: String?,
    val token: String?,
    val tokenExpired: String?,
) : Parcelable {
    fun toLoginVerify(statusResponse: String, message: String) : LoginVerify {
        return LoginVerify(
            status = statusResponse,
            email = email.toString(),
            token = token.toString(),
            tokenExpired = tokenExpired.toString(),
            message = message,
        )
    }
}
