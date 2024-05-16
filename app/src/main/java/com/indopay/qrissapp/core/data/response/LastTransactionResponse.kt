package com.indopay.qrissapp.core.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.DataLastTrxItemEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class LastTransactionResponse(

	@field:SerializedName("result")
	val result: Int? = null,

	@field:SerializedName("rc")
	val status: String? = null,

	@field:SerializedName("data")
	val data: List<DataLastTrxItemResponse?>? = null,

	@field:SerializedName("rcMessage")
	val message: String? = null,

	@field:SerializedName("page")
	val page: Int? = null
) : Parcelable

@Parcelize
data class DataLastTrxItemResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("netAmount")
	val netAmount: String? = null,

	@field:SerializedName("idTrx")
	val idTrx: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable {
	fun toDataLastTrxItemEntity(statusResponse: String?, messageResponse: String?) : DataLastTrxItemEntity {
		return DataLastTrxItemEntity(
			statusResponse = statusResponse,
			messageResponse = messageResponse,
			date = date,
			amount = amount,
			netAmount = netAmount,
			idTrx = idTrx,
			status = status,
		)
	}
}
