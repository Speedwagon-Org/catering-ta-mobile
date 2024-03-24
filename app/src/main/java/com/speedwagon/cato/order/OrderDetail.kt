package com.speedwagon.cato.order

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ceil

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
    private lateinit var btnSetEndCateringDate : Button
    private lateinit var tvSetStartCateringDate : TextView
    private lateinit var tvSetEndCateringDate : TextView
    private val calendar = Calendar.getInstance()
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
        btnSetEndCateringDate = findViewById(R.id.btn_order_detail_catering_end_date)
        btnSetStartCateringDate = findViewById(R.id.btn_order_detail_catering_start_date)
        tvSetStartCateringDate =findViewById(R.id.tv_order_detail_catering_start_date)
        tvSetEndCateringDate =findViewById(R.id.tv_order_detail_catering_end_date)
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
        }

        btnEditLocation.setOnClickListener {
            val intent = Intent(this, Location::class.java)
            startActivityForResult(intent, REQUEST_CODE)

        }

        btnSetStartCateringDate.setOnClickListener {
            showDatePickerDialog(tvSetStartCateringDate)
        }

        btnSetEndCateringDate.setOnClickListener {
            showEndDatePickerDialog(tvSetEndCateringDate)
        }
        recyclerViewInit(orderType)
        spinnerInit()
        setDefaultLocation()


    }
    private fun recyclerViewInit(orderType: Int) {
        val rvOrderFood = findViewById<RecyclerView>(R.id.rv_order_detail_foods)
        val cartFoodData = cartManager.getCartData(this)
        var totalPrice: Long = 0

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
                                tvTotalPrice.text = CurrencyConverter.intToIDR(totalPrice)
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
        val paymentMethod = findViewById<Spinner>(R.id.sp_order_detail_payment)

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

    private fun showDatePickerDialog(textView: TextView) {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel(textView)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showEndDatePickerDialog(textView: TextView) {
        val startDate = calendar.time
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedEndDate = Calendar.getInstance()
                selectedEndDate.set(year, month, dayOfMonth)
                val endDate = selectedEndDate.time

                val daysDifference = calculateDaysDifference(startDate, endDate)

                if (endDate >= startDate && daysDifference <= 30) {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateLabel(textView)
                } else {
                    // Show error message or handle the case when the selected end date is invalid
                    // For now, let's just display a Toast message
                    Toast.makeText(
                        this,
                        "End date must be after the start date and within 30 days",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }


    private fun calculateDaysDifference(startDate: Date, endDate: Date): Long {
        val differenceInMillis = abs(endDate.time - startDate.time)
        return ceil(differenceInMillis.toDouble() / (1000 * 60 * 60 * 24)).toLong()
    }
    private fun updateLabel(textView: TextView) {
        val format = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        textView.text = sdf.format(calendar.time)
    }
    companion object {
        const val REQUEST_CODE = 100
    }
}