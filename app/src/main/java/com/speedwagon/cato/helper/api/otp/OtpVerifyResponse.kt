package com.speedwagon.cato.helper.api.otp

data class OtpVerifyResponse(
    val status: Boolean,
    val message: String,
    val code : String
)