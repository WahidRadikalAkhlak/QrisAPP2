package com.indopay.qrissapp.core.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.TransactionDetailEntity

@Parcelize
data class TransactionDetailResponse(

	@field:SerializedName("rc")
	val status: String? = null,

	@field:SerializedName("data")
	val dataTrxDetailItem: DataTrxDetailItem? = null,

	@field:SerializedName("rcMessage")
	val message: String? = null
) : Parcelable

@Parcelize
data class DataTrxDetailItem(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("netAmount")
	val netAmount: String? = null,

	@field:SerializedName("idTrx")
	val idTrx: String? = null,

	@field:SerializedName("MID")
	val mID: String? = null,

	@field:SerializedName("dateTrx")
	val dateTrx: String? = null,

	@field:SerializedName("mdrAmount")
	val mdrAmount: String? = null,

	@field:SerializedName("status")
	val status: String? = null
) : Parcelable{
	fun toTransactionDetailEntity():TransactionDetailEntity{
		return TransactionDetailEntity(
			amount = amount,
			netAmount = netAmount,
			idTrx = idTrx,
			mID = mID,
			dateTrx = dateTrx,
			mdrAmount = mdrAmount,
			status = status
		)
	}
}
