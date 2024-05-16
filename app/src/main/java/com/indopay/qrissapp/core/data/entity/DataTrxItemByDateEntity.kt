package com.indopay.qrissapp.core.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.indopay.qrissapp.domain.model.DataTrxItemByDate

@Entity(tableName = "trx_item_byDate_entity")
data class DataTrxItemByDateEntity(
    val date: String?,
    val statusResponse: String?,
    val messageResponse: String?,
    val amount: String?,
    val idTrx: String?,
    val netAmount: String?,
    val status: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
) {
    fun toDataTrxItemByDateDomain() : DataTrxItemByDate {
        return DataTrxItemByDate(
            amount = amount,
            idTrx = idTrx,
            netAmount = netAmount,
            status = status,
            id = id,
            date = date,
            statusResponse = statusResponse,
            messageResponse = messageResponse
        )
    }
}
