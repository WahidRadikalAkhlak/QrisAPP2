package com.indopay.qrissapp.utils

object ConstrainKey {
    const val END_API_LOGIN = "qris/user/login"
    const val END_API_OTP_LOGIN = "qris/user/verify"
    const val END_API_PROFILE = "qris/user/profile"
    const val END_API_LAST_TRX = "qris/trx/latest"
    const val END_API_TRX_BY_DATE = "qris/trx/date"
    const val END_API_TRX_DETAIL = "qris/trx/detail"
    const val END_API_LOGOUT = "merchant-logout/b2b"
    const val END_API_GENERATE_QR = "merchant-generate-qr/b2b"
    const val END_API_REGENERATE_OTP = "merchant-regenerate-otp"
    const val END_API_FORGOT_PASSWORD = "forgot-password/b2b"
    const val END_API_MERCHANT_TRX = "merchant-trx/b2b"
}