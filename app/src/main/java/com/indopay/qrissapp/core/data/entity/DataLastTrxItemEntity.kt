package com.indopay.qrissapp.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.indopay.qrissapp.domain.model.DataLastTransactionItem

@Entity(tableName = "last_trx_entity")
data class DataLastTrxItemEntity(
    val statusResponse: String?,
    val messageResponse: String?,
    val date: String?,
    val amount: String?,
    val netAmount: String?,
    val idTrx: String?,
    val status: String?,

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
) {
    fun toDataLastTrxItemDomain() : DataLastTransactionItem {
        return DataLastTransactionItem(
            statusResponse = statusResponse,
            messageResponse = messageResponse,
            date = date,
            amount = amount,
            netAmount = netAmount,
            idTrx = idTrx,
            status = status,
            id = id,
        )
    }
}

