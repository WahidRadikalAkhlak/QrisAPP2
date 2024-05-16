package com.indopay.qrissapp.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserMerchant(
    val merchantId: String,
    val emailMerchant: String,
    val token: String,
) : Parcelable
