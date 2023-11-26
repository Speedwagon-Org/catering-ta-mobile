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

class Login : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val userUsername = view.findViewById<TextInputEditText>(R.id.et_login_username_value).editableText.toString()
        val userPassword = view.findViewById<TextInputEditText>(R.id.et_login_password_value).editableText.toString()
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val redirectRegister = view.findViewById<TextView>(R.id.tv_login_redirectToRegister)

        redirectRegister.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val registerFragment = Register()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fcv_auth, registerFragment)
            fragmentTransaction.commit()
        }

        loginButton.setOnClickListener{authenticator(userUsername, userPassword)}
        return view
    }

    private fun authenticator(username: String, password : String){
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Masih ada yang belum kamu isi tuh!\nYok kamu coba lagi 😄", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Login sebagai $username 🔐", Toast.LENGTH_SHORT).show()
        }
    }
}