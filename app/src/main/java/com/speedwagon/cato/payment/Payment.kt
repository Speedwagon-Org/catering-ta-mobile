package com.speedwagon.cato.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.speedwagon.cato.R
import com.speedwagon.cato.order.OrderDetail
import com.speedwagon.cato.order.OrderStatus

class Payment : AppCompatActivity() {
    private lateinit var btnFinishPayment: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)

        btnFinishPayment = findViewById(R.id.btn_payment_debug_finish)

        btnFinishPayment.setOnClickListener {
            val intent = Intent(this, OrderStatus::class.java)
            startActivity(intent)
        }
    }
}