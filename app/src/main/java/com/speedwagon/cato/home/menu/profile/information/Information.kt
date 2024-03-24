package com.speedwagon.cato.home.menu.profile.information

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.speedwagon.cato.R

class Information : AppCompatActivity() {
    private lateinit var tvToc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_customer_service)

        tvToc = findViewById(R.id.tv_information_toc)
        tvToc.setOnClickListener {
            openWebView()
        }
    }

    private fun openWebView() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/d/1q0Xb657LmIKUGMOZq2gj5KL_hF3kfhHLQnZh0ZO7azo/preview"))
        startActivity(intent)
    }
}
