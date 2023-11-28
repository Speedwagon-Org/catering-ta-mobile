package com.speedwagon.cato.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.speedwagon.cato.R

class Otp : AppCompatActivity() {
    private lateinit var otpOne : TextInputEditText
    private lateinit var otpTwo : TextInputEditText
    private lateinit var otpThree : TextInputEditText
    private lateinit var otpFour : TextInputEditText
    private lateinit var otpFive : TextInputEditText
    private lateinit var otpSix : TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        otpOne = findViewById(R.id.et_otp_1)
        otpTwo = findViewById(R.id.et_otp_2)
        otpThree = findViewById(R.id.et_otp_3)
        otpFour = findViewById(R.id.et_otp_4)
        otpFive = findViewById(R.id.et_otp_5)
        otpSix = findViewById(R.id.et_otp_6)

        otpOne.requestFocus()
        
    }
}