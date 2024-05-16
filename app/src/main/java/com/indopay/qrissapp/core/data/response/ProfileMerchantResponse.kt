package com.indopay.qrissapp.core.data.response

import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.ProfileMerchantEntity

data class ProfileMerchantResponse(

	@field:SerializedName("rc")
	val status: String? = null,

	@field:SerializedName("data")
	val dataProfile: DataProfile? = null,

	@field:SerializedName("rcMessage")
	val message: String? = null
)

data class DataProfile(

	@field:SerializedName("amount")
	val amount: String? = null,

	@field:SerializedName("address2")
	val address2: String? = null,

	@field:SerializedName("jmlTrx")
	val jmlTrx: String? = null,

	@field:SerializedName("netAmount")
	val netAmount: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("MID")
	val mID: String? = null,

	@field:SerializedName("mdrAmount")
	val mdrAmount: String? = null,

	@field:SerializedName("kelurahan")
	val kelurahan: String? = null,

	@field:SerializedName("merchantName")
	val merchantName: String? = null,

	@field:SerializedName("province")
	val province: String? = null,

	@field:SerializedName("subdistrict")
	val subdistrict: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("posCode")
	val posCode: String? = null,

	@field:SerializedName("username")
	val username: String? = null
) {
	fun toProfileMerchantEntity(): ProfileMerchantEntity{
		return ProfileMerchantEntity(
			amount = amount,
			address1 = address1,
			address2 = address2,
			jmlTrx = jmlTrx,
			netAmount = netAmount,
			mID = mID,
			mdrAmount = mdrAmount,
			kelurahan = kelurahan,
			merchantName = merchantName,
			province = province,
			subdistrict = subdistrict,
			district = district,
			posCode = posCode,
			username = username
		)
	}
}
