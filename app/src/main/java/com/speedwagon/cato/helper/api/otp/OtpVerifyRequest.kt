package com.speedwagon.cato.helper.api.otp

data class OtpVerifyRequest(
    val otp_id: String,
    val otp: String
)
