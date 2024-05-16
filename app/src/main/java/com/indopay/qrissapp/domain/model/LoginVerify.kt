package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginVerify(
    val status: String?,
    val email: String?,
    val token: String?,
    val tokenExpired: String?,
    val message: String?,
) : Parcelable
