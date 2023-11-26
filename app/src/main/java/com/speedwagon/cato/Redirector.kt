package com.speedwagon.cato

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.speedwagon.cato.auth.Authentication

class Redirector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redirector)

        // Start Auth Activity
        val btnRedirectorAuth : Button = findViewById(R.id.btn_redirect_auth)
        btnRedirectorAuth.setOnClickListener {
            val intent = Intent(this, Authentication::class.java)
            startActivity(intent)
        }
    }
}