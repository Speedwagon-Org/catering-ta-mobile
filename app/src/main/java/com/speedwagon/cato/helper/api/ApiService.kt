package com.speedwagon.cato.helper.api

import com.speedwagon.cato.helper.api.otp.OtpRequest
import com.speedwagon.cato.helper.api.otp.OtpResponse
import com.speedwagon.cato.helper.api.otp.OtpVerifyRequest
import com.speedwagon.cato.helper.api.otp.OtpVerifyResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("v1/otp/request")
    fun requestOtp(@Body otpRequest: OtpRequest): Call<OtpResponse>

    @POST("v1/otp/verify")
    fun requestVerifyOtp(@Body otpVerifyRequest: OtpVerifyRequest): Call<OtpVerifyResponse>
}