package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionDetail(
    val statusResponse: String?,
    val message: String?,
    val amount: String?,
    val netAmount: String?,
    val idTrx: String?,
    val mID: String?,
    val dateTrx: String?,
    val mdrAmount: String?,
    val status: String?,
) : Parcelable
