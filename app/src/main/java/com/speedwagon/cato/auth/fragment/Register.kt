package com.speedwagon.cato.auth.fragment

import android.content.Intent
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
import com.speedwagon.cato.auth.OtpAuthentication

class Register : Fragment() {

    private lateinit var userUsername : TextInputEditText
    private lateinit var userPhone : TextInputEditText
    private lateinit var userPassword : TextInputEditText
    private lateinit var userEmail : TextInputEditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        userUsername = view.findViewById(R.id.et_register_username_value)
        userPhone = view.findViewById(R.id.et_register_phone_value)
        userPassword = view.findViewById(R.id.et_register_password_value)
        userEmail = view.findViewById(R.id.et_register_email_value)

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
        val email = userEmail.editableText.toString()

        if (username.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || email.isEmpty()){
            Toast.makeText(context, "Seperti nya ada yang belum kamu isih deh, coba cek lagi ya!", Toast.LENGTH_SHORT).show()
            if (username.isEmpty()){
                userUsername.requestFocus()
            } else if (phoneNumber.isEmpty()){
                userPhone.requestFocus()
            } else if (email.isEmpty()){
                userEmail.requestFocus()
            } else {
                userPassword.requestFocus()
            }
        } else {
            val intent = Intent(activity, OtpAuthentication::class.java)
            intent.putExtra("email", email)
            intent.putExtra("username", username)
            intent.putExtra("phone", phoneNumber)
            intent.putExtra("password", password)
            startActivity(intent)
            requireActivity().finish()
//            val registrationManager = RegistrationManager()
//
//            registrationManager.registerWithEmailAndPassword(email, username, phoneNumber, password) { isSuccess, eMessage ->
//                if (isSuccess){
//                    Toast.makeText(context, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show()
//                    redirectHome()
//                } else {
//                    Toast.makeText(context, "Gagal mendaftarkan akun", Toast.LENGTH_SHORT).show()
//                    Log.e("Registration", "Error: $eMessage")
//                }
//            }
        }
    }

//    private fun redirectHome(){
//        val intent = Intent(activity, HomeNavigation::class.java)
//        startActivity(intent)
//        requireActivity().finish()
//    }
}