package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataLastTransactionItem(
    val statusResponse: String?,
    val messageResponse: String?,
    val date: String?,
    val amount: String?,
    val netAmount: String?,
    val idTrx: String?,
    val status: String?,
    val id: Int?,
) : Parcelable
