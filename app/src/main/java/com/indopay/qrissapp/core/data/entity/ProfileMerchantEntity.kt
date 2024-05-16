package com.indopay.qrissapp.core.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.indopay.qrissapp.domain.model.ProfileMerchant
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "profile_merchant_entity")
data class ProfileMerchantEntity(
    val amount: String?,
    val address1: String?,
    val address2: String?,
    val jmlTrx: String?,
    val netAmount: String?,
    val mID: String?,
    val mdrAmount: String?,
    val kelurahan: String?,
    val merchantName: String?,
    val province: String?,
    val subdistrict: String?,
    val district: String?,
    val posCode: String?,
    val username: String?,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
) : Parcelable {
    fun toProfileMerchantDomain(status: String?, message: String?) : ProfileMerchant {
        return ProfileMerchant(
            status,
            message,
            amount,
            address1,
            address2,
            jmlTrx,
            netAmount,
            mID,
            mdrAmount,
            kelurahan,
            merchantName,
            province,
            subdistrict,
            district,
            posCode,
            username
        )
    }
}