package com.speedwagon.cato.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.speedwagon.cato.R

class Register : Fragment() {
    private lateinit var userUsername : TextInputEditText
    private lateinit var userPhone : TextInputEditText
    private lateinit var userPassword : TextInputEditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        userUsername = view.findViewById(R.id.et_register_username_value)
        userPhone = view.findViewById(R.id.et_register_phone_value)
        userPassword = view.findViewById(R.id.et_register_password_value)

        val registerButton = view.findViewById<Button>(R.id.btn_register)
        val loginRedirect = view.findViewById<TextView>(R.id.tv_login_redirectToLogin)

        loginRedirect.setOnClickListener{
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val loginFragment = Login()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fcv_auth, loginFragment)
            fragmentTransaction.commit()
        }

        registerButton.setOnClickListener { registerUser() }

        return view
    }

    private fun registerUser(){
        val username = userUsername.editableText.toString()
        val phoneNumber = userPhone.editableText.toString()
        val password = userPassword.editableText.toString()

        if (username.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "Seperti nya ada yang belum kamu isih deh, coba cek lagi ya!", Toast.LENGTH_SHORT).show()
            if (username.isEmpty()){
                userUsername.requestFocus()
            } else if (phoneNumber.isEmpty()){
                userPhone.requestFocus()
            } else {
                userPassword.requestFocus()
            }
        } else {
            Toast.makeText(context, "Telah terdaftar", Toast.LENGTH_SHORT).show()
        }
    }
}