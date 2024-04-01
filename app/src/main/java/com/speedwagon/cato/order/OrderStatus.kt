package com.speedwagon.cato.order

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.speedwagon.cato.R
import com.speedwagon.cato.auth.Authentication
import com.speedwagon.cato.home.menu.Home

class OrderStatus : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        val cbPayment = findViewById<CheckBox>(R.id.cb_order_status_waiting_payment)
        val cbConfirm = findViewById<CheckBox>(R.id.cb_order_status_confirming)
        val cbOnProcess = findViewById<CheckBox>(R.id.cb_order_status_on_process)
        val cbOnDelivery= findViewById<CheckBox>(R.id.cb_order_status_on_delivery)
        val btnInsertCts = findViewById<Button>(R.id.btn_order_status_insert_cts)
        val tvCancelOrder = findViewById<TextView>(R.id.tv_order_status_cancel_order)
        val tvStatusResult = findViewById<TextView>(R.id.tv_order_status_result)
        val btnPayOrder = findViewById<Button>(R.id.btn_order_status_pay)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid
        val orderId = intent.getStringExtra("orderId")

        if (userId != null && orderId != null){

            // STATUS :
            //      payment       - WAITING FOR CUSTOMER PAYING FOR THE FOOD
            //      waiting       - WAITING CONFIRMATION FROM VENDOR
            //      confirm       - ORDER CONFIRMED BY VENDOR
            //      on process    - ON PROCESS
            //      on delivery   - ON DELIVERY AND WAITING FOR CTS CODE
            //      delivered     - FINISH ORDER

            // CANCEL STATUS :
            //      not confirmed - ORDER IS NOT ACCEPTED BY VENDOR
            //      canceled      - CANCELLED BY CUSTOMER
            //      adm cancel    - CANCELLED BY ADMIN

            val orderRef = db.collection("orders")
            orderRef.document(orderId).get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val res = task.result
                    if (res.exists()){
                        val orderStatus = res.getString("status")
                        val orderCtsCode = res.getString("cts")
                        val colors = listOf ("#5cb85c", "#bab12d", "#ba2d2d")

                        btnInsertCts.isEnabled = false
                        tvCancelOrder.isEnabled = false
                        if (orderStatus == "not confirmed")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.GONE
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Vendor membatalkan pesanan ini"
                        }
                        else if (orderStatus == "adm cancel"){
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.GONE
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Admin membatalkan pesanan ini"
                        }
                        else if (orderStatus == "canceled")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.GONE
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Anda membatalkan pesanan ini"
                        }
                        else if (orderStatus == "delivered")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.GONE
                            tvStatusResult.setTextColor(Color.parseColor(colors[0]))
                            cbConfirm.isChecked = true
                            cbOnProcess.isChecked = true
                            cbOnDelivery.isChecked = true
                            cbPayment.isChecked = true
                            tvStatusResult.text = "PESANAN INI SUDAH SELESAI!"

                        }
                        else {
                            tvStatusResult.setTextColor(Color.parseColor(colors[1]))
                            tvStatusResult.text = "Menunggu pembayaran oleh anda"

                            tvCancelOrder.isEnabled = true
                            btnInsertCts.visibility = View.GONE

                            if (orderStatus == "payment"){
                                tvStatusResult.text = "Menunggu konfirmasi oleh vendor"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnInsertCts.visibility = View.VISIBLE
                                btnPayOrder.visibility = View.GONE

                            }
                            if (orderStatus == "confirm"){
                                tvStatusResult.text = "Pesanan sedang dipersiapkan oleh vendor"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                            }
                            if (orderStatus == "on process"){
                                tvStatusResult.text = "Pesanan dalam perjalanan"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                cbOnProcess.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                            }
                            if (orderStatus == "on delivery"){
                                tvStatusResult.text = "Menunggu kode CTS"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                cbOnProcess.isChecked = true
                                cbOnDelivery.isChecked = true
                                btnInsertCts.isEnabled = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                                btnInsertCts.visibility = View.VISIBLE
                            }
                        }
                        btnInsertCts.setOnClickListener {
                            showCtsInsertDialog(this) {ctsCode ->
                                if (ctsCode == orderCtsCode){
                                    val newStatus = "delivered"
                                    orderRef.document(orderId).update("status", newStatus)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Yey! pesananmu telah selesai", Toast.LENGTH_SHORT).show()
                                            restartActivity()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error updating document" , e)
                                            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Kode CTS salah!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        tvCancelOrder.setOnClickListener {
                            android.app.AlertDialog.Builder(this)
                                .setTitle("Batalkan Pesanan")
                                .setMessage("Apakah anda yakin untuk membatalkan pesanan?")
                                .setPositiveButton("Iya") { _, _ ->
                                    val newStatus = "canceled"
                                    orderRef.document(orderId).update("status", newStatus)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Yah... kamu batalin pesasanannya", Toast.LENGTH_SHORT).show()
                                            restartActivity()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error updating document" , e)
                                            Toast.makeText(this, "Gagal membatalkan pesanan", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .setNegativeButton("Tidak", null)
                                .show()
                        }
                    }
                }
            }

        }
        else if (orderId != null){
            val intent = Intent(this, Home::class.java)
            Toast.makeText(this, "Pesanan tidak valid!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, Authentication::class.java)
            Toast.makeText(this, "Silahkan login dulu!", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }
    fun showCtsInsertDialog(context: Context, callback: (String) -> Unit) {
        val inputEditText = EditText(context)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Kode CTS")
            .setView(inputEditText)
            .setMessage("Masukkan kode CTS yang sudah diberikan oleh vendor")
            .setPositiveButton("OK") { _, _ ->
                val inputText = inputEditText.text.toString()
                callback(inputText)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }
    fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }

}