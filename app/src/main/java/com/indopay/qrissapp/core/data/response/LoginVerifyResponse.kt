package com.indopay.qrissapp.core.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.indopay.qrissapp.core.data.entity.LoginVerifyEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class LoginVerifyResponse(
    @field:SerializedName("rc")
    val status: String,

    @field:SerializedName("rcMessage")
    val message: String,

    @field:SerializedName("data")
    val dataVerifyResponse: DataVerifyResponse,
) : Parcelable

@Parcelize
data class DataVerifyResponse(
    @field:SerializedName("authFor")
    val email: String? = null,

    @field:SerializedName("authToken")
    val token: String? = null,

    @field:SerializedName("authTokenExpiredAt")
    val tokenExpired: String? = null,
) : Parcelable {
    fun toLoginVerifyEntity() : LoginVerifyEntity {
        return LoginVerifyEntity(
            email = email,
            token = token,
            tokenExpired = tokenExpired,
        )
    }
}
