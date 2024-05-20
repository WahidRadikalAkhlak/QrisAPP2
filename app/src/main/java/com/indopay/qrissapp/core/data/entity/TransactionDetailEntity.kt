package com.indopay.qrissapp.core.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.indopay.qrissapp.domain.model.TransactionDetail
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "trx_detail_entity")
data class TransactionDetailEntity(
    val amount: String?,
    val netAmount: String?,
    val idTrx: String?,
    val mID: String?,
    val mdrAmount: String?,
    val status: String?,
    val dateTrx: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
) : Parcelable {
    fun toTransactionDetailDomain(statusResponse: String?, message: String?) : TransactionDetail {
        return TransactionDetail(
            statusResponse = statusResponse,
            message = message,
            amount,
            netAmount,
            idTrx,
            mID,
            mdrAmount,
            status,
            dateTrx,
        )
    }
}
