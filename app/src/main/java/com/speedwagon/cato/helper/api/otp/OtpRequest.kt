package com.speedwagon.cato.helper.api.otp

data class OtpRequest(
    val email: String,
    val phone: String,
    val gateway_key: String
)
