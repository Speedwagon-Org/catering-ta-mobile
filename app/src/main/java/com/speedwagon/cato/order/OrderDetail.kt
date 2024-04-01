package com.speedwagon.cato.order

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.helper.CartManager
import com.speedwagon.cato.helper.CurrencyConverter
import com.speedwagon.cato.home.menu.profile.location.DetailLocation
import com.speedwagon.cato.home.menu.profile.location.Location
import com.speedwagon.cato.order.adapter.OrderFoodAdapter
import com.speedwagon.cato.order.adapter.item.OrderedFood
import com.speedwagon.cato.payment.Payment
import java.io.IOException
import java.time.LocalDateTime
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class OrderDetail : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var vendorId : String
    private lateinit var cartManager : CartManager
    private lateinit var llDatePickContainer : LinearLayout
    private lateinit var tvDetailLocation : TextView
    private lateinit var tvDetailLocationLabel : TextView
    private lateinit var tvDetailLocationGeoPoint : TextView
    private lateinit var tvOrderType : TextView
    private lateinit var btnEditLocation : ImageView
    private lateinit var tvTotalPrice : TextView
    private lateinit var btnSetStartCateringDate : Button
    private lateinit var tvSetStartCateringDate : TextView
    private lateinit var tvSetEndCateringDate : TextView
    private lateinit var tvTotalLabel : TextView
    private lateinit var btnContinueToPayment : Button
    private lateinit var paymentMethod : Spinner
    private var startDate: Long = 0
    private var endDate: Long = 0
    private var durationInDays : Long= 0
    private var totalPrice : Long = 0
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        tvDetailLocation = findViewById(R.id.tv_order_detail_location)
        tvDetailLocationLabel = findViewById(R.id.tv_order_detail_location_label)
        tvDetailLocationGeoPoint = findViewById(R.id.tv_order_detail_location_geo_point)
        btnEditLocation = findViewById(R.id.iv_order_detail_edit_location)
        tvOrderType = findViewById(R.id.tv_order_detail_type)
        tvTotalPrice = findViewById(R.id.tv_order_detail_total_price)
        llDatePickContainer = findViewById(R.id.ll_order_detail_catering_start_end_container)
        btnSetStartCateringDate = findViewById(R.id.btn_order_detail_catering_start_date)
        tvSetStartCateringDate =findViewById(R.id.tv_order_detail_catering_start_date)
        tvSetEndCateringDate =findViewById(R.id.tv_order_detail_catering_end_date)
        tvTotalLabel = findViewById(R.id.tv_order_detail_total_label)
        btnContinueToPayment = findViewById(R.id.btn_order_detail_continue_payment)
        paymentMethod = findViewById(R.id.sp_order_detail_payment)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        vendorId = intent.getStringExtra("vendorId").toString()
        cartManager = CartManager

        val orderType = intent.getIntExtra("orderType", -1)

        if (orderType == 0){
            tvOrderType.text = "Pesan antar"
        } else {
            tvOrderType.text = "Katering Langganan"
            llDatePickContainer.visibility = View.VISIBLE
            btnContinueToPayment.isEnabled = false
        }

        btnEditLocation.setOnClickListener {
            val intent = Intent(this, Location::class.java)
            startActivityForResult(intent, REQUEST_CODE)

        }

        btnSetStartCateringDate.setOnClickListener {
            // Set up MaterialDatePicker for date range selection
            val builder = MaterialDatePicker.Builder.dateRangePicker()
            val picker = builder.build()

            picker.addOnPositiveButtonClickListener { selection ->
                // Handle the selection
                startDate = selection.first ?: 0
                endDate = selection.second ?: 0

                // Update the TextViews to display selected dates
                tvSetStartCateringDate.text = formatDate(startDate)
                tvSetEndCateringDate.text = formatDate(endDate)
                durationInDays = calculateDuration(startDate, endDate)
                tvTotalPrice.text = CurrencyConverter.intToIDR(totalPrice * durationInDays)
                btnContinueToPayment.isEnabled = true

            }
            // Show the date picker
            picker.show(supportFragmentManager, picker.toString())
        }

        btnContinueToPayment.setOnClickListener {
            val intent = Intent(this, Payment::class.java)
            var orderDetail : HashMap<String, *>
            if (orderType == 0){
                orderDetail = hashMapOf(
                    "customer" to auth.currentUser?.uid,
                    "order_time" to LocalDateTime.now(),
                    "order_type" to 0,
                    "payment_method" to "qris",
                    "payment_status" to "pending",
                    "status" to "payment",
                    "total_price" to totalPrice,
                    "vendor" to vendorId,
                )

                db.collection("orders").add(orderDetail).addOnSuccessListener {ref ->
                    val orderId = ref.id
                    val listFoodDetail : ArrayList<HashMap<String, *>> = arrayListOf()
                    db.collection("vendor").document(vendorId).collection("foods").get().addOnCompleteListener {foodTask ->
                        if(foodTask.isSuccessful){
                            val foodData = foodTask.result
                            for (food in foodData){
                                listFoodDetail.add(
                                    hashMapOf(
                                        "name" to food.getString("name"),
                                        "price" to food.getLong("price"),
                                        "photo" to food.getString("photo")
                                        // TODO : ALSO ADD DATA FOR QUANTITY INSIDE CART
                                    )
                                )
                            }
                            ref.collection("foods").add(listFoodDetail)
                            intent.putExtra("orderId", orderId)
                            startActivity(intent)
                        }
                    }

                }
            } else if (orderType == 1){

            }

        }
        recyclerViewInit(orderType)
        spinnerInit()
        setDefaultLocation()
    }

    private fun formatDate(date: Long): String {
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }
    private fun calculateDuration(startDate: Long, endDate: Long): Long {
        val durationInMillis = endDate - startDate
        return TimeUnit.MILLISECONDS.toDays(durationInMillis) + 1
    }

    @SuppressLint("SetTextI18n")
    private fun recyclerViewInit(orderType: Int) {
        val rvOrderFood = findViewById<RecyclerView>(R.id.rv_order_detail_foods)
        val cartFoodData = cartManager.getCartData(this)

        rvOrderFood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val foodCartList = ArrayList<OrderedFood>()
        val vendorRef = db.collection("vendor")

        if (orderType != -1) {
            vendorRef.document(vendorId).collection("foods").get().addOnSuccessListener { foods ->
                for (food in foods) {
                    val foodId = food.id

                    if (orderType == 0) {
                        val itemInsideCart = cartManager.isItemInCart(this, vendorId, foodId)
                        println("Is Item Exist? $itemInsideCart")
                        if (itemInsideCart) {
                            val cartFood = cartFoodData.find { it.foodId == foodId }

                            val foodName = food.getString("name")!!
                            val foodQty = cartFood?.quantity ?: 0
                            val foodPrice = food.getDouble("price")!!
                            val foodDiscount = food.getDouble("discount")!!
                            val finalPrice = foodPrice * foodQty * (1 - foodDiscount)
                            val foodImg = food.getString("photo")!!
                            val foodImgRef = storage.getReferenceFromUrl(foodImg)

                            totalPrice += finalPrice.toLong()
                            foodCartList.add(
                                OrderedFood(
                                    foodId = foodId,
                                    foodName = foodName,
                                    foodPrice = finalPrice.toLong(),
                                    foodPictUrl = foodImgRef,
                                    foodQty = foodQty
                                )
                            )
                        }
                    } else if (orderType == 1) {
                        val foodAvailableCatering = food.getBoolean("catering_available") ?: false
                        println("food isAvailable $foodAvailableCatering")
                        if (foodAvailableCatering) {
                            val foodName = food.getString("name")!!
                            val foodImg = food.getString("photo")!!
                            val foodImgRef = storage.getReferenceFromUrl(foodImg)

                            vendorRef.document(vendorId).get().addOnSuccessListener { vendorRes ->
                                val cateringPrice = vendorRes.getLong("catering_price") ?: 0
                                totalPrice = vendorRes.getLong("catering_price")?:0
                                foodCartList.add(
                                    OrderedFood(
                                        foodId = foodId,
                                        foodName = foodName,
                                        foodPrice = cateringPrice,
                                        foodPictUrl = foodImgRef,
                                        foodQty = 1
                                    )
                                )

                                rvOrderFood.adapter = OrderFoodAdapter(this, foodCartList, orderType)
                                tvTotalLabel.text = CurrencyConverter.intToIDR(totalPrice) + "/Hari"
                            }
                        }
                    }
                }

                rvOrderFood.adapter = OrderFoodAdapter(this, foodCartList, orderType)
                tvTotalPrice.text = CurrencyConverter.intToIDR(totalPrice)
            }.addOnFailureListener { exception ->
                Log.e("RecyclerViewInit", "Error fetching food data: $exception")
            }
        }
    }

    private fun spinnerInit(){

        val paymentMethodAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.payment_method)
        )
        paymentMethodAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        val orderTypeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.order_type)
        )
        orderTypeAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        paymentMethod.adapter = paymentMethodAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setDefaultLocation(){
        val userId = auth.currentUser?.uid
        val customerRef = db.collection("customer")
        val locationRef = customerRef.document(userId!!).collection("location")

        customerRef.document(userId).get().addOnCompleteListener {customerTask ->
            if (customerTask.isSuccessful){
                val res = customerTask.result
                if(res.exists()){
                    val defaultLocationId = res.getString("default_location")
                    if (!defaultLocationId.isNullOrEmpty()){
                        locationRef.document(defaultLocationId).get().addOnCompleteListener {locationTask ->
                            if (locationTask.isSuccessful){
                                val locRes = locationTask.result
                                if (locRes.exists()){
                                    val locLabel = locRes.getString("label")!!
                                    val locDetail = locRes.getString("detail")!!
                                    val locGeoPointDetail = locRes.getGeoPoint("location")!!
                                    val (town, street) = getTownAndStreet(locGeoPointDetail.latitude, locGeoPointDetail.longitude)
                                    tvDetailLocation.text = locDetail
                                    tvDetailLocationLabel.text = locLabel.uppercase()
                                    tvDetailLocationGeoPoint.text = "[$town, $street]"
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTownAndStreet(latitude: Double, longitude: Double): Pair<String?, String?> {
        val geocoder = Geocoder(this, Locale.getDefault())
        var town: String? = null
        var street: String? = null

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    town = address.locality.removePrefix("Kecamatan").trim()
                    street = address.thoroughfare
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Pair(town, street)
    }

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DetailLocation.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setDefaultLocation()
        }
    }


    companion object {
        const val REQUEST_CODE = 100
    }
}