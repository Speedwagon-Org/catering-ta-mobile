package com.speedwagon.cato.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.speedwagon.cato.R

class Otp : AppCompatActivity() {
    private lateinit var otpOne: TextInputEditText
    private lateinit var otpTwo: TextInputEditText
    private lateinit var otpThree: TextInputEditText
    private lateinit var otpFour: TextInputEditText
    private lateinit var otpFive: TextInputEditText
    private lateinit var otpSix: TextInputEditText
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
        otpOne.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpOne.editableText.clear()
            }
        }
        otpTwo.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpTwo.editableText.clear()
            }
        }
        otpThree.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpThree.editableText.clear()
            }
        }
        otpFour.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpFour.editableText.clear()
            }
        }
        otpFive.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpFive.editableText.clear()
            }
        }
        otpSix.onFocusChangeListener = View.OnFocusChangeListener{v, hasFocus ->
            if (hasFocus){
                otpSix.editableText.clear()
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
                    } else if (otpFive.editableText.isEmpty()) {
                        otpFive.requestFocus()
                    }else if (otpSix.editableText.isEmpty()) {
                        otpSix.requestFocus()
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

        otpTwo.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpThree.editableText.isEmpty()) {
                        otpThree.requestFocus()
                    } else if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    } else if (otpFive.editableText.isEmpty()) {
                        otpFive.requestFocus()
                    }else if (otpSix.editableText.isEmpty()) {
                        otpSix.requestFocus()
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

        otpThree.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    } else if (otpFive.editableText.isEmpty()) {
                        otpFive.requestFocus()
                    }else if (otpSix.editableText.isEmpty()) {
                        otpSix.requestFocus()
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
                    if (otpFive.editableText.isEmpty()) {
                        otpFive.requestFocus()
                    } else if (otpSix.editableText.isEmpty()) {
                        otpSix.requestFocus()
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

        otpFive.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpSix.editableText.isEmpty()) {
                        otpSix.requestFocus()
                    } else {
                        verification()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int){
            }
        })

        otpSix.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    if (otpOne.editableText.isEmpty()) {
                        otpOne.requestFocus()
                    } else if (otpTwo.editableText.isEmpty()) {
                        otpTwo.requestFocus()
                    } else if (otpThree.editableText.isEmpty()) {
                        otpThree.requestFocus()
                    } else if (otpFour.editableText.isEmpty()) {
                        otpFour.requestFocus()
                    } else if (otpFive.editableText.isEmpty()) {
                        otpFive.requestFocus()
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
    }

    private fun verification(){
        Toast.makeText(this, "Verifying... 🔎", Toast.LENGTH_SHORT).show()
    }
}