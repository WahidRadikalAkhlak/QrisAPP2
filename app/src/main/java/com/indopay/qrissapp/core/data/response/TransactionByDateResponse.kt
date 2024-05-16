package com.indopay.qrissapp.core.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.DataTrxItemByDateEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionByDateResponse(

	@field:SerializedName("result")
	val result: Int? = null,

	@field:SerializedName("rc")
	val status: String? = null,

	@field:SerializedName("data")
	val data: List<DataTrxItemByDateResponse?>? = null,

	@field:SerializedName("rcMessage")
	val message: String? = null,

	@field:SerializedName("firstDate")
	val firstDate: String? = null,

	@field:SerializedName("Page")
	val page: Int? = null,

	@field:SerializedName("lastDate")
	val lastDate: String? = null
) :Parcelable

@Parcelize
data class DataTrxItemByDateResponse(

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
	fun toDataTrxItemByDateEntity(statusResponse: String?, messageResponse: String?) : DataTrxItemByDateEntity {
		return DataTrxItemByDateEntity(
			amount = amount,
			idTrx = idTrx,
			netAmount = netAmount,
			status = status,
			date = date,
			statusResponse = statusResponse,
			messageResponse = messageResponse
		)
	}
}