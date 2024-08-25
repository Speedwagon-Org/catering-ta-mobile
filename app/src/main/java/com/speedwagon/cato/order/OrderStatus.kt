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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.auth.Authentication
import com.speedwagon.cato.helper.CurrencyConverter
import com.speedwagon.cato.home.menu.Home
import com.speedwagon.cato.order.adapter.OrderFoodAdapter
import com.speedwagon.cato.order.adapter.item.OrderedFood
import com.speedwagon.cato.payment.Payment

class OrderStatus : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var foodList : ArrayList<OrderedFood>
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
        val tvTotalPrice = findViewById<TextView>(R.id.tv_order_status_total_price)
        val tvDayLeft = findViewById<TextView>(R.id.tv_order_status_catering_days_left)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        val userId = auth.currentUser?.uid
        val orderId = intent.getStringExtra("orderId")

        btnPayOrder.setOnClickListener{
            val intent = Intent(this, Payment::class.java)
            intent.putExtra("orderId", orderId)
            startActivity(intent)
        }
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
            val rvListMakanan = findViewById<RecyclerView>(R.id.rv_order_status_list_makanan)
            foodList  = ArrayList()
            rvListMakanan.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            val orderRef = db.collection("orders")
            orderRef.document(orderId).get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val res = task.result
                    if (res.exists()){
                        val orderStatus = res.getString("status")
                        val orderCtsCode = res.getString("cts")
                        val colors = listOf ("#5cb85c", "#bab12d", "#ba2d2d")
                        val orderType = res.getLong("order_type")
                        val orderDaysLeft = res.getLong("order_day_left")
                        val orderTotalPrice = res.getLong("total_price")
                        tvTotalPrice.text = CurrencyConverter.intToIDR(orderTotalPrice!!)
                        if (orderType == 0L){
                            tvDayLeft.visibility = View.GONE
                        } else {
                            tvDayLeft.text = "Sisa $orderDaysLeft Hari"
                        }
                        orderRef.document(orderId).collection("foods").get().addOnCompleteListener { foodsTask ->
                            if (foodsTask.isSuccessful){
                                val foodRes = foodsTask.result
                                if (!foodRes.isEmpty) {
                                    for (food in foodRes){
                                        if (orderType == 0L ){
                                            val photoRef = storage.getReferenceFromUrl(food.getString("photo")!!)
                                            val discount = food.getDouble("discount")!!
                                            val qty = food.getLong("quantity")!!
                                            val price = (food.getLong("price")!! * qty * (1.0 - discount)).toLong()
                                            foodList.add(
                                                OrderedFood(
                                                    foodId = food.id,
                                                    foodName = food.getString("name")!!.capitalize(),
                                                    foodPrice = price,
                                                    foodPictUrl = photoRef,
                                                    foodQty = qty,
                                                    foodDiscount = discount
                                                )
                                            )
                                        } else if (orderType == 1L){
                                            val photoRef = storage.getReferenceFromUrl(food.getString("photo")!!)
                                            foodList.add(
                                                OrderedFood(
                                                    foodId = food.id,
                                                    foodName = food.getString("name")!!.capitalize(),
                                                    foodPrice = 0,
                                                    foodPictUrl = photoRef,
                                                    foodQty = 0,
                                                    foodDiscount = 1.0
                                                )
                                            )
                                        }

                                        rvListMakanan.adapter = OrderFoodAdapter(this, foodList, orderType!!.toInt())
                                    }
                                }
                            }
                        }

                        btnInsertCts.isEnabled = false
                        tvCancelOrder.isEnabled = false
                        if (orderStatus == "not confirmed")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.VISIBLE
                            btnInsertCts.isEnabled = false
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Vendor membatalkan pesanan ini"
                        }
                        else if (orderStatus == "adm cancel"){
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.VISIBLE
                            btnInsertCts.isEnabled = false
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Admin membatalkan pesanan ini"
                        }
                        else if (orderStatus == "canceled")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.VISIBLE
                            btnInsertCts.isEnabled = false
                            tvStatusResult.setTextColor(Color.parseColor(colors[2]))
                            tvStatusResult.text = "Anda membatalkan pesanan ini"
                        }
                        else if (orderStatus == "delivered")
                        {
                            tvCancelOrder.visibility = View.GONE
                            btnPayOrder.visibility = View.GONE
                            btnInsertCts.visibility = View.VISIBLE
                            tvStatusResult.setTextColor(Color.parseColor(colors[0]))
                            cbConfirm.isChecked = true
                            cbOnProcess.isChecked = true
                            cbOnDelivery.isChecked = true
                            cbPayment.isChecked = true
                            btnInsertCts.isEnabled = false
                            if (orderType == 1L && orderDaysLeft!! > 0){
                                tvStatusResult.text = "Tunggu toko untuk mempersiapkan makanan besok"
                            }
                            if (orderType == 0L || orderDaysLeft == null || orderDaysLeft == 0L){
                                tvStatusResult.text = "Pesanan sudah selesai!"
                            }

                        }
                        else {
                            tvStatusResult.setTextColor(Color.parseColor(colors[1]))
                            tvStatusResult.text = "Menunggu pembayaran oleh anda"

                            tvCancelOrder.isEnabled = true
                            btnInsertCts.visibility = View.GONE

                            if (orderStatus == "waiting"){
                                tvStatusResult.text = "Menunggu konfirmasi oleh vendor"
                                cbPayment.isChecked = true
                                btnInsertCts.visibility = View.VISIBLE
                                btnPayOrder.visibility = View.GONE

                            }
                            if (orderStatus == "confirm"){
                                tvStatusResult.text = "Pesanan telah dikonfirmasi oleh vendor"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                                btnInsertCts.visibility = View.VISIBLE
                            }
                            if (orderStatus == "on process"){
                                tvStatusResult.text = "Pesanan sedang dipersiapkan"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                cbOnProcess.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                                btnInsertCts.visibility = View.VISIBLE
                            }
                            if (orderStatus == "on delivery"){
                                tvStatusResult.text = "Pesanan sedang dalam perjalanan"
                                cbPayment.isChecked = true
                                cbConfirm.isChecked = true
                                cbOnProcess.isChecked = true
                                cbOnDelivery.isChecked = true
                                tvCancelOrder.isEnabled = false
                                btnPayOrder.visibility = View.GONE
                                btnInsertCts.visibility = View.VISIBLE
                                btnInsertCts.isEnabled = true
                            }
                        }
                        btnInsertCts.setOnClickListener {
                            showCtsInsertDialog(this) { ctsCode ->
                                if (ctsCode == orderCtsCode) {
                                    val newStatus = "delivered"
                                    orderRef.document(orderId).update("status", newStatus)
                                        .addOnSuccessListener {
                                            val vendorId = res.getString("vendor")
                                            if (vendorId != null) {
                                                decreaseVendorStock(vendorId, orderId)
                                            }
                                            Toast.makeText(this, "Yey! pesananmu telah selesai", Toast.LENGTH_SHORT).show()
                                            restartActivity()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error updating document", e)
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
    private fun showCtsInsertDialog(context: Context, callback: (String) -> Unit) {
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

    private fun restartActivity() {
        val intent = intent
        finish()
        startActivity(intent)
    }
    private fun decreaseVendorStock(vendorId: String, orderId: String) {
        val orderFoodsRef = db.collection("orders").document(orderId).collection("foods")
        val vendorFoodsRef = db.collection("vendor").document(vendorId).collection("foods")

        orderFoodsRef.get().addOnSuccessListener { orderFoodsSnapshot ->
            if (!orderFoodsSnapshot.isEmpty) {
                for (orderFood in orderFoodsSnapshot) {
                    val orderedFoodName = orderFood.getString("name")?.capitalize()
                    val quantityOrdered = orderFood.getLong("quantity") ?: 0L

                    vendorFoodsRef.whereEqualTo("name", orderedFoodName).get().addOnSuccessListener { vendorFoodsSnapshot ->
                        if (!vendorFoodsSnapshot.isEmpty) {
                            val vendorFood = vendorFoodsSnapshot.documents[0]
                            val currentStock = vendorFood.getLong("stock") ?: 0L
                            Log.d(TAG, "newstck: $currentStock - $quantityOrdered")
                            val newStock = currentStock - quantityOrdered
                            if (newStock >= 0) {
                                vendorFoodsRef.document(vendorFood.id).update("stock", newStock)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Stock updated for foodName: $orderedFoodName")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error updating stock for foodName: $orderedFoodName", e)
                                    }
                            } else {
                                Log.e(TAG, "Not enough stock for foodName: $orderedFoodName")
                            }
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error getting vendor food", e)
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Error getting ordered foods", e)
        }
    }
}