package com.speedwagon.cato.helper.api.otp

data class OtpData(
    val id: String,
    val otp: String,
    val otp_length: Int,
    val channel: String,
    val provider: String,
    val purpose: String
)