package com.speedwagon.cato.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.speedwagon.cato.R
import com.speedwagon.cato.home.HomeNavigation

class Otp : AppCompatActivity() {
    private lateinit var otpOne: TextInputEditText
    private lateinit var otpTwo: TextInputEditText
    private lateinit var otpThree: TextInputEditText
    private lateinit var otpFour: TextInputEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        otpOne = findViewById(R.id.et_otp_1)
        var oldOtpOne = ""
        otpTwo = findViewById(R.id.et_otp_2)
        var oldOtpTwo = ""
        otpThree = findViewById(R.id.et_otp_3)
        var oldOtpThree = ""
        otpFour = findViewById(R.id.et_otp_4)
        var oldOtpFour = ""

        otpOne.requestFocus()
        otpOne.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                oldOtpOne = otpOne.editableText.toString()
                otpOne.editableText.clear()
            } else {
                if (otpOne.editableText.isEmpty()){
                    otpOne.setText(oldOtpOne)
                }
            }
        }
        otpTwo.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                oldOtpTwo = otpOne.editableText.toString()
                otpTwo.editableText.clear()
            } else {
                if (otpTwo.editableText.isEmpty()){
                    otpTwo.setText(oldOtpTwo)
                }
            }
        }
        otpThree.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                oldOtpThree = otpOne.editableText.toString()
                otpOne.editableText.clear()
            } else {
                if (otpThree.editableText.isEmpty()){
                    otpThree.setText(oldOtpThree)
                }
            }
        }
        otpFour.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                oldOtpFour = otpOne.editableText.toString()
                otpOne.editableText.clear()
            } else {
                if (otpThree.editableText.isEmpty()){
                    otpThree.setText(oldOtpFour)
                }
            }
        }
        otpOne.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpTwo.editableText.isEmpty()) {
                        otpTwo.requestFocus()
                    } else if (otpThree.editableText.isEmpty()) {
                        otpThree.requestFocus()
                    } else if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    }  else {
                        verification()
                    }
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        otpTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpThree.editableText.isEmpty()) {
                        otpThree.requestFocus()
                    } else if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    }  else {
                        verification()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        otpThree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    } else {
                        verification()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        otpFour.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpOne.editableText.isEmpty()) {
                        otpOne.requestFocus()
                    } else if (otpTwo.editableText.isEmpty()) {
                        otpTwo.requestFocus()
                    } else if (otpThree.editableText.isEmpty()) {
                        otpThree.requestFocus()
                    }  else {
                        verification()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun verification(){
        val intent = Intent(this, HomeNavigation::class.java)
        intent.putExtra(
            "username",
            getIntent().getStringExtra("username").toString()
        )
        startActivity(intent)
        finish()
    }
}