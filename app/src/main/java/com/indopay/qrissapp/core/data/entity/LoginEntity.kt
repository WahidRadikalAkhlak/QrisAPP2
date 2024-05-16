package com.indopay.qrissapp.core.data.entity

import com.indopay.qrissapp.domain.model.Login

data class LoginEntity(
    val status: String? = null,
    val message: String? = null,
) {
    fun toLogin() : Login {
        return Login(
            status,
            message
        )
    }
}
