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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.speedwagon.cato.R
import com.speedwagon.cato.home.HomeNavigation

class Login : Fragment() {
    private lateinit var userUsername : TextInputEditText
    private lateinit var userPassword : TextInputEditText
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        userUsername = view.findViewById(R.id.et_login_username_value)
        userPassword = view.findViewById(R.id.et_login_password_value)
        auth = Firebase.auth
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val redirectRegister = view.findViewById<TextView>(R.id.tv_login_redirectToRegister)
        val currentUserId = auth.currentUser?.uid
        if (currentUserId != null){
            redirectHome()
        }

        redirectRegister.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            val registerFragment = Register()

            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.fcv_auth, registerFragment)
            fragmentTransaction.commit()
        }

        loginButton.setOnClickListener {
            authenticateUser()
        }
        return view
    }

    private fun authenticateUser(){
        val username = userUsername.editableText.toString()
        val password = userPassword.editableText.toString()
        Toast.makeText(context, "Signing in...", Toast.LENGTH_SHORT).show()
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Masih ada yang belum kamu isi tuh!\nYok kamu coba lagi 😄", Toast.LENGTH_SHORT).show()
            if (username.isEmpty()){
                userUsername.requestFocus()
            } else {
                userPassword.requestFocus()
            }
        } else {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        redirectHome()
                    } else {
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun redirectHome(){
        val intent = Intent(activity, HomeNavigation::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}