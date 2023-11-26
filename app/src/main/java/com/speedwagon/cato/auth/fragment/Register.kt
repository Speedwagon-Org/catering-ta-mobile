package com.speedwagon.cato.auth.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.speedwagon.cato.R

class Register : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val userUsername = view.findViewById<TextInputEditText>(R.id.et_register_username_value).editableText.toString()
        val userPhone = view.findViewById<TextInputEditText>(R.id.et_register_phone_value).editableText.toString()
        val userPassword = view.findViewById<TextInputEditText>(R.id.et_register_password_value).editableText.toString()

        val registerButton = view.findViewById<Button>(R.id.btn_register)
        val loginRedirect = view.findViewById<TextView>(R.id.tv_login_redirectToLogin)

        loginRedirect.setOnClickListener{
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val loginFragment = Login()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fcv_auth, loginFragment)
            fragmentTransaction.commit()
        }

        registerButton.setOnClickListener { registerUser(userUsername, userPhone, userPassword) }

        return view
    }

    private fun registerUser(username: String, phoneNumber: String, password: String){
        if (username.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()){
            Toast.makeText(context, "Seperti nya ada yang belum kamu isih deh, coba cek lagi ya!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Telah terdaftar", Toast.LENGTH_SHORT).show()
        }
    }
}