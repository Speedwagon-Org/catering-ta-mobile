package com.speedwagon.cato.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.speedwagon.cato.R
import com.speedwagon.cato.helper.RegistrationManager
import com.speedwagon.cato.helper.api.ApiService
import com.speedwagon.cato.helper.api.otp.AuthInterceptor
import com.speedwagon.cato.helper.api.otp.OtpRequest
import com.speedwagon.cato.helper.api.otp.OtpResponse
import com.speedwagon.cato.helper.api.otp.OtpVerifyRequest
import com.speedwagon.cato.helper.api.otp.OtpVerifyResponse
import com.speedwagon.cato.home.menu.profile.location.DetailLocation
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OtpAuthentication : AppCompatActivity() {
    private var otpId = ""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_authentication)

        val otpButton = findViewById<Button>(R.id.button_check_otp)
        val emailText = findViewById<TextView>(R.id.text_email)

        val email = intent.getStringExtra("email")
        val username = intent.getStringExtra("username")
        val phone = intent.getStringExtra("phone")
        val password = intent.getStringExtra("password")

        emailText.text = email?.substring(0,4) + "xxxx"
        val retrofit = createRetrofit()
        val service = retrofit.create(ApiService::class.java)
        if (email != null && username != null && phone != null && password != null){
            val otpRequest = OtpRequest(
                email = email,
                phone = "",
                gateway_key = "e86e1a7e-149f-47de-b64f-3bfc0c57a838"
            )

            service.requestOtp(otpRequest).enqueue(object : Callback<OtpResponse> {
                override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                    if (response.isSuccessful) {
                        val otpResponse = response.body()
                        println("Status: ${otpResponse?.status}")
                        println("Message: ${otpResponse?.message}")
                        println("OTP: ${otpResponse?.data?.otp}")
                        println("Otp Id: ${otpResponse?.data?.id}")

                        if (otpResponse?.data?.id != null){
                            otpId = otpResponse.data.id
                        }
                    } else {
                        println("Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                    println("Failure: ${t.message}")
                }
            })

            otpButton.setOnClickListener {
                val otpDigit1 = findViewById<TextView>(R.id.otp_digit1).text
                val otpDigit2 = findViewById<TextView>(R.id.otp_digit2).text
                val otpDigit3 = findViewById<TextView>(R.id.otp_digit3).text
                val otpDigit4 = findViewById<TextView>(R.id.otp_digit4).text

                val otp = "$otpDigit1$otpDigit2$otpDigit3$otpDigit4"
                val otpVerifyRequest = OtpVerifyRequest(
                    otp_id = otpId,
                    otp = otp
                )
                service.requestVerifyOtp(otpVerifyRequest).enqueue(object : Callback<OtpVerifyResponse>{
                    override fun onResponse(
                        call: Call<OtpVerifyResponse>,
                        response: Response<OtpVerifyResponse>
                    ) {
                        if(response.isSuccessful){
                            val otpVerifyResponse = response.body()
                            if (otpVerifyResponse?.status == true){
                                println("OTP VERIFIED")

                                val registrationManager = RegistrationManager()
                                registrationManager.registerWithEmailAndPassword(email, username, phone, password) { isSuccess, eMessage ->
                                    if (isSuccess){
                                        redirectAddLocation()
                                    } else {
                                        println("Registration Error: $eMessage")
                                    }
                                }
                            } else {
                                println("OTP NOT VERIFIED ${otpVerifyResponse?.message}")
                            }
                        } else {
                            println("Error: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<OtpVerifyResponse>, t: Throwable) {
                        println("Failure: ${t.message}")
                    }

                })
            }
        }


    }

    private fun createRetrofit(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api.fazpass.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun redirectAddLocation(){
        val intent = Intent(this, DetailLocation::class.java)
        intent.putExtra("first_time", true)
        startActivity(intent)
        finish()
    }
}