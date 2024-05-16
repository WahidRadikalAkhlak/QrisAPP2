package com.indopay.qrissapp.core.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.LoginEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginResponse(

	@field:SerializedName("rc")
	val status: String,

	@field:	SerializedName("rcMessage")
	val message: String,
) : Parcelable {

	fun toLoginEntity(): LoginEntity {
		return LoginEntity(
			status = status,
			message = message
		)
	}
}

