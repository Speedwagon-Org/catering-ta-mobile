package com.speedwagon.cato.vendor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.helper.CartManager
import com.speedwagon.cato.order.OrderDetail
import com.speedwagon.cato.vendor.adapter.DetailVendorFoodAdapter
import com.speedwagon.cato.vendor.adapter.item.VendorFood

class DetailVendor : AppCompatActivity() {
    private lateinit var cartItemList : ArrayList<Map<String, *>>
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var tvDistance : TextView
    private lateinit var tvName : TextView
    private lateinit var redirectPaymentDetail : Button
    private lateinit var ivVendorBadge : ImageView
    private lateinit var cartManager: CartManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_vendor)

        tvDistance = findViewById(R.id.tv_detail_vendor_distance)
        tvName = findViewById(R.id.tv_detail_vendor_name)
        cartItemList = ArrayList()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        ivVendorBadge = findViewById(R.id.iv_detail_vendor_badge)
        cartManager = CartManager

        val distance : Double = intent.getDoubleExtra("vendorDistance", 0.0)
        val vendorName : String? = intent.getStringExtra("vendorName")
        val vendorId = intent.getStringExtra("vendorId")

        if (distance == 0.0){
            tvDistance.text = "NOT FOUND!"
        } else {
            tvDistance.text = "%.2f km".format(distance)
        }

        if (vendorName.isNullOrEmpty()) {
            tvName.text = "NO NAME!"
        } else {
            tvName.text = vendorName
        }

        redirectPaymentDetail = findViewById(R.id.btn_detail_vendor_redirect_detail)
        redirectPaymentDetail.setOnClickListener {
            if (!cartManager.isCartEmpty(this)){
                val intent = Intent(this, OrderDetail::class.java)
                intent.putExtra("vendorId", vendorId)
                intent.putExtra("orderType", 0)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Keranjang masih kosong!!", Toast.LENGTH_SHORT).show()
            }
        }

        getVendorFoods(this)
    }

    private fun getVendorFoods(context: Context) {
        val vendorId = intent.getStringExtra("vendorId")
        val foodsList = ArrayList<VendorFood>()
        if (vendorId != null) {
            val vendorRef = db.collection("vendor")

            vendorRef.document(vendorId).collection("foods").get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val res = task.result
                    if (!res.isEmpty) {
                        for (food in res.documents) {
                            val foodId = food.id
                            val foodDiscount = food.getDouble("discount")!!
                            val foodPrice = food.get("price")!! as Long
                            val foodName = food.getString("name")!!
                            val foodPhotoUrl = food.getString("photo")!!
                            val foodRef = storage.getReferenceFromUrl(foodPhotoUrl)
                            val quantity = cartManager.getCartData(context).find { it.vendorId == vendorId && it.foodId == foodId }?.quantity ?: 0

                            foodsList.add(
                                VendorFood(
                                    foodId = foodId,
                                    foodDiscount = foodDiscount,
                                    foodName = foodName,
                                    foodImgUrl = foodRef,
                                    foodPrice = foodPrice,
                                    foodQty = quantity
                                )
                            )
                        }
                        recyclerViewInit(foodsList, vendorId)
                    }
                }
            }
        }
    }

    private fun recyclerViewInit(foodsList : ArrayList<VendorFood>, vendorId : String){
        val rvDetailVendorFood = findViewById<RecyclerView>(R.id.rv_detail_vendor_foods)
        rvDetailVendorFood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvDetailVendorFood.adapter = DetailVendorFoodAdapter(this, foodsList, vendorId, cartManager)

    }
}