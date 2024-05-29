package com.speedwagon.cato.helper.api.otp

data class OtpResponse(
    val status: Boolean,
    val message: String,
    val code: String,
    val data: OtpData
)

