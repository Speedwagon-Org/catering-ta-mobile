package com.speedwagon.cato.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.speedwagon.cato.R
import com.speedwagon.cato.home.HomeNavigation

class Payment : AppCompatActivity() {
    private lateinit var btnFinishPayment: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btnFinishPayment = findViewById(R.id.btn_payment_debug_finish)
        val db = FirebaseFirestore.getInstance()
        val orderId = intent.getStringExtra("orderId")!!
        btnFinishPayment.setOnClickListener {
            val intent = Intent(this, HomeNavigation::class.java)
            db.collection("orders").document(orderId).update(
                "payment_status" , "paid"
            )
            db.collection("orders").document(orderId).update(
                "status" , "waiting"
            )
            startActivity(intent)
            finish()
        }
    }
}