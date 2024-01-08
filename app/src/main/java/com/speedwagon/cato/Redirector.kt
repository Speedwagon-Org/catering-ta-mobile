package com.speedwagon.cato

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.speedwagon.cato.auth.Authentication
import com.speedwagon.cato.auth.Otp
import com.speedwagon.cato.home.HomeNavigation
import com.speedwagon.cato.order.OrderDetail
import com.speedwagon.cato.vendor.foods.DetailFood

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

        // Start OTP Activity
        val btnRedirectorOtp : Button = findViewById(R.id.btn_redirect_otp)
        btnRedirectorOtp.setOnClickListener {
            val intent = Intent(this, Otp::class.java)
            startActivity(intent)
        }

        // Start OTP Activity
        val btnRedirectorHomeNavigation : Button = findViewById(R.id.btn_redirect_home)
        btnRedirectorHomeNavigation.setOnClickListener {
            val intent = Intent(this, HomeNavigation::class.java)
            startActivity(intent)
        }
        // Start Detail Order Activity
        val btnRedirectorOrderDetail : Button = findViewById(R.id.btn_redirect_order_detail)
        btnRedirectorOrderDetail.setOnClickListener {
            val intent = Intent(this, OrderDetail::class.java)
            startActivity(intent)
        }
        // Start Detail Vendor Activity
        val btnRedirectorVendorDetail : Button = findViewById(R.id.btn_redirect_vendor_detail)
        btnRedirectorVendorDetail.setOnClickListener {
            val intent = Intent(this, DetailFood::class.java)
            startActivity(intent)
        }
    }
}