package com.indopay.qrissapp.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataTrxItemByDate(
    val date: String?,
    val statusResponse: String?,
    val messageResponse: String?,
    val amount: String?,
    val idTrx: String?,
    val netAmount: String?,
    val status: String?,
    val id: Int?,
) : Parcelable
