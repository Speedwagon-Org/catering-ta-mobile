package com.speedwagon.cato.order

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.DetailLocation
import com.speedwagon.cato.home.menu.profile.location.Location
import com.speedwagon.cato.order.adapter.OrderFoodAdapter
import com.speedwagon.cato.order.adapter.item.OrderedFood
import java.io.IOException
import java.util.Locale

class OrderDetail : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var storage : FirebaseStorage

    private lateinit var tvDetailLocation : TextView
    private lateinit var tvDetailLocationLabel : TextView
    private lateinit var tvDetailLocationGeoPoint : TextView
    private lateinit var btnEditLocation : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        tvDetailLocation = findViewById(R.id.tv_order_detail_location)
        tvDetailLocationLabel = findViewById(R.id.tv_order_detail_location_label)
        tvDetailLocationGeoPoint = findViewById(R.id.tv_order_detail_location_geo_point)
        btnEditLocation = findViewById(R.id.iv_order_detail_edit_location)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerViewInit()
        spinnerInit()
        setDefaultLocation()

        btnEditLocation.setOnClickListener {
            val intent = Intent(this, Location::class.java)
            startActivityForResult(intent, REQUEST_CODE)
            
        }
    }
    private fun recyclerViewInit(){
        val rvOrderFood = findViewById<RecyclerView>(R.id.rv_order_detail_foods)
        rvOrderFood.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val dummyDataOrderFood = listOf(
            OrderedFood(
                foodName="Food 1",
                foodPrice=24000,
                foodPictUrl="https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodQty = 2
            ),
            OrderedFood(
                foodName="Food 2",
                foodPrice=38000,
                foodPictUrl="https://asset.kompas.com/crops/AWXtnkYHOrbSxSggVuTs3EzQprM=/10x36:890x623/750x500/data/photo/2023/03/25/641e5ef63dea4.jpg",
                foodQty = 5
            )
        )
        rvOrderFood.adapter = OrderFoodAdapter(this, dummyDataOrderFood)
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
    companion object {
        const val REQUEST_CODE = 100
    }
}