package com.speedwagon.cato.home.menu

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.home.NearVendorAdapter
import com.speedwagon.cato.home.menu.adapter.home.OnProcessAdapter
import com.speedwagon.cato.home.menu.adapter.home.PopularFoodAdapter
import com.speedwagon.cato.home.menu.adapter.home.item.NearVendor
import com.speedwagon.cato.home.menu.adapter.home.item.OnProcessItem
import com.speedwagon.cato.home.menu.adapter.home.item.PopularFood
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalTime
import java.util.Locale

@Suppress("DEPRECATION")
class Home : Fragment() {
    private lateinit var rvOnProcessItem: RecyclerView
    private lateinit var rvPopularItem : RecyclerView
    private lateinit var rvNearVendor: RecyclerView
    private lateinit var tvUserName: TextView
    private lateinit var tvBerandaWelcomeMessage : TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLocation: TextView
    private lateinit var orders : ArrayList<Map<String, *>>
    private lateinit var vendors : ArrayList<Map<String, *>>
    private lateinit var userRef : CollectionReference
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var tvOrderOnProcessAvailable : TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvBerandaWelcomeMessage = view.findViewById(R.id.tv_beranda_welcome_msg)
        tvBerandaWelcomeMessage.text = getTimeOfDay()
        tvOrderOnProcessAvailable = view.findViewById(R.id.tv_home_on_process_is_not_available)
        // Location Get
        tvLocation = view.findViewById(R.id.tv_beranda_location)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Firebase init
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid
        db = FirebaseFirestore.getInstance()
        userRef = db.collection("customer")
        val orderRef = db.collection("orders")
        val vendorRef = db.collection("vendor")
        orders = ArrayList()
        vendors = ArrayList()
        getLastLocation()
        if (currentUserId != null) {
            vendorRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val snapshot = task.result
                    if (snapshot != null){
                        for (document in snapshot.documents){
                            val vendorId = document.id
                            val vendorData = document.data
                            vendors.add(
                                mapOf(
                                    "id" to vendorId,
                                    "data" to vendorData
                                )
                            )
                        }
                        setVendorRecyclerView()
                    }
                }
            }
            orderRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result
                    if (querySnapshot != null) {

                        var completedOrders = 0
                        for (document in querySnapshot.documents) {
                            val orderId = document.id
                            val orderData = document.data
                            val filterStatus = listOf("waiting", "confirm", "on process", "on delivery")
                            if (orderData?.get("customer") == currentUserId && filterStatus.contains(orderData["status"])) {
                                val foods: ArrayList<Map<String, Any>> = ArrayList()
                                val foodRef = orderRef.document(orderId).collection("foods")
                                val foodPromise = foodRef.get()
                                foodPromise.addOnSuccessListener { foodsRes ->
                                    for (food in foodsRes.documents) {
                                        val foodId = food.id
                                        val foodData = food.data
                                        foods.add(
                                            mapOf(
                                                "id" to foodId,
                                                "food" to foodData as Map<String, Any>
                                            )
                                        )
                                    }
                                    orders.add(
                                        mapOf(
                                            "id" to orderId,
                                            "order" to orderData,
                                            "foods" to foods
                                        )
                                    )
                                    completedOrders++
                                    if (completedOrders < 6) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            setOrdersRecyclerView()
                                        }
                                    }
                                }.addOnFailureListener { exception ->
                                    Log.e(TAG, "Error getting foods: ", exception)
                                }
                            } else {
                                completedOrders++
                                if (completedOrders < 6) {

                                    CoroutineScope(Dispatchers.Main).launch {
                                        setOrdersRecyclerView()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting orders: ", task.exception)
                }
            }


            userRef.document(currentUserId).get().addOnCompleteListener{task ->
                if (task.isSuccessful){
                    val doc = task.result
                    if (doc.exists()){
                        val name = doc.get("name")
                        tvUserName.text = name.toString()
                    } else {
                        Toast.makeText(context, "doc doesn't exist", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    task.exception?.message?.let {
                        Log.d(TAG, it)
                    }
                }
            }
        }
        // RecyclerView value initializer
        rvOnProcessItem = view.findViewById(R.id.rv_home_on_process)
        rvPopularItem = view.findViewById(R.id.rv_home_popular)
        rvNearVendor = view.findViewById(R.id.rv_home_newly_open)
        tvUserName = view.findViewById(R.id.tv_beranda_username)


        // Popular RecyclerVIew
        rvPopularItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dummyDataPopularFood = listOf(
            PopularFood(
                foodName = "Food 1",
                vendorName = "Vendor 1",
                foodPrice = 12000,
                foodImgUrl = "https://www.indonesia.travel/content/dam/indtravelrevamp/id-id/ide-liburan/fakta-menarik-di-balik-nikmatnya-sate-padang/thumbnail.jpg"
            )
        )
        rvPopularItem.adapter = PopularFoodAdapter(requireContext(), dummyDataPopularFood)

        return view
    }




    private fun getTimeOfDay(): String {
        val currentTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return when {
            currentTime.isAfter(LocalTime.of(6, 0)) && currentTime.isBefore(LocalTime.of(12, 0)) -> "Selamat Pagi"
            currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(18, 0)) -> "Selamat Siang"
            currentTime.isAfter(LocalTime.of(18, 0)) && currentTime.isBefore(LocalTime.of(21, 0)) -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }
    private suspend fun getVendor(id: String): String {
        return withContext(Dispatchers.IO) {
            val vendorRef = db.collection("vendor").document(id).get().await()
            if (vendorRef.exists()) {
                return@withContext vendorRef.getString("name") ?: "No Name"
            } else {
                return@withContext "No Name"
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getLastLocation() {
        val userId = auth.currentUser?.uid
        if (userId != null){
            userRef.document(userId).get().addOnCompleteListener {userTask ->
                if(userTask.isSuccessful){
                    val data = userTask.result
                    if(data.exists()){
                        val locationDefaultId = data.getString("default_location")
                        val locationRef = userRef.document(userId).collection("location")
                        if (locationDefaultId != null){
                            locationRef.document(locationDefaultId).get().addOnCompleteListener {locationTask ->
                                if (locationTask.isSuccessful){
                                    val locationData = locationTask.result
                                    val latitude = locationData.getGeoPoint("location")?.latitude
                                    val longitude = locationData.getGeoPoint("location")?.longitude
                                    if (latitude == null || longitude == null){
                                        tvLocation.text = "Pilih lokasi"
                                    } else {
                                        val (town, street) = getTownAndStreet(latitude, longitude)
                                        tvLocation.text = "$town, $street"
                                    }

                                }
                            }
                        } else {
                            tvLocation.text = "Pilih lokasi"
                        }
                    }
                }
            }
        }


    }

    private fun getTownAndStreet(latitude: Double, longitude: Double): Pair<String?, String?> {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
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
    private fun setVendorRecyclerView() {
        rvNearVendor.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dataNearVendor : ArrayList<NearVendor> = ArrayList()

        for (vendor in vendors){
            val id = vendor["id"] as String
            val name = (vendor["data"] as Map<*,*>) ["name"] as String
            val img =(vendor["data"] as Map<*,*>) ["profile_picture"] as String
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img)
            dataNearVendor.add(
                NearVendor(
                    vendorId = id,
                    vendorName = name,
                    vendorDistance = 10.1,
                    vendorImgUrl = storageReference
                )
            )
        }
        rvNearVendor.adapter = NearVendorAdapter(requireContext(), dataNearVendor)
    }
    private suspend fun setOrdersRecyclerView() {
        tvOrderOnProcessAvailable.visibility = View.GONE
        // On Process RecyclerView
        rvOnProcessItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dataOnProcessOrders: ArrayList<OnProcessItem> = ArrayList()

        for (order in orders) {
            val id = order["id"] as String
            val foodName = (((order["foods"] as ArrayList<*>)[0] as Map<*, *>)["food"] as Map<*, *>)["name"] as String
            val foodStatus = (order["order"] as Map<*, *>)["status"] as String
            val vendorId = (order["order"] as Map<*, *>)["vendor"] as String
            val foodImgPath = (((order["foods"] as ArrayList<*>)[0] as Map<*, *>)["food"] as Map<*, *>)["photo"] as String
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(foodImgPath)

            val vendorName = withContext(Dispatchers.IO) {
                getVendor(vendorId)
            }

            dataOnProcessOrders.add(
                OnProcessItem(
                    orderId = id,
                    foodName = foodName,
                    foodStatus = foodStatus,
                    foodImgUrl = storageReference,
                    vendorName = vendorName
                )
            )
        }

        rvOnProcessItem.adapter = OnProcessAdapter(requireContext(), dataOnProcessOrders)
    }

}