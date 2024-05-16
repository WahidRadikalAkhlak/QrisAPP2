package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Login(
    val status: String?,
    val message: String?,
) : Parcelable
