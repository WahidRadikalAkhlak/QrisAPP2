package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileMerchant(
    val status: String?,
    val message: String?,
    val amount: String?,
    val address1: String?,
    val address2: String?,
    val jmlTrx: String?,
    val netAmount: String?,
    val mID: String?,
    val mdrAmount: String?,
    val kelurahan: String?,
    val merchantName: String?,
    val province: String?,
    val subdistrict: String?,
    val district: String?,
    val posCode: String?,
    val username: String?,
) : Parcelable
