package com.speedwagon.cato.home.menu

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Geocoder
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
import com.google.firebase.firestore.GeoPoint
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

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
                            val filterStatus = listOf(
                                "payment",
                                "waiting",
                                "confirm",
                                "on process",
                                "on delivery"
                            )
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
        val currentTime =
            LocalTime.now()
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
        val dataNearVendor: ArrayList<NearVendor> = ArrayList()
        var vendorsProcessed = 0

        for (vendor in vendors) {
            val id = vendor["id"] as String
            val name = (vendor["data"] as Map<*, *>)["name"] as String
            val img = (vendor["data"] as Map<*, *>)["profile_picture"] as String
            val vendorLat = ((vendor["data"] as Map<*, *>)["location"] as GeoPoint).latitude
            val vendorLng = ((vendor["data"] as Map<*, *>)["location"] as GeoPoint).longitude

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img)
            userRef.document(auth.currentUser!!.uid).get().addOnCompleteListener { customerTask ->
                if (customerTask.isSuccessful) {
                    val res = customerTask.result
                    if (res != null) {
                        val locationDefaultId = res.getString("default_location")!!
                        val locationRef = userRef.document(auth.currentUser!!.uid).collection("location")
                        locationRef.document(locationDefaultId).get().addOnCompleteListener { userLocationTask ->
                            if (userLocationTask.isSuccessful) {
                                val userLocationRes = userLocationTask.result
                                if (userLocationRes != null) {
                                    val customerLat = userLocationRes.getGeoPoint("location")!!.latitude
                                    val customerLng = userLocationRes.getGeoPoint("location")!!.longitude
                                    val distance = vincentyDistance(lat1 = customerLat, lat2 = vendorLat, lon1 = customerLng, lon2 = vendorLng)

                                    if (distance <= 15.0) {
                                        dataNearVendor.add(
                                            NearVendor(
                                                vendorId = id,
                                                vendorName = name,
                                                vendorDistance = distance,
                                                vendorImgUrl = storageReference
                                            )
                                        )
                                    }
                                    vendorsProcessed++
                                    if (vendorsProcessed == vendors.size || dataNearVendor.size <= 5) {
                                        rvNearVendor.adapter = NearVendorAdapter(requireContext(), dataNearVendor)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    private suspend fun setOrdersRecyclerView() {
        if (orders.size>0){
            tvOrderOnProcessAvailable.visibility = View.GONE
        } else {
            tvOrderOnProcessAvailable.visibility = View.VISIBLE
        }
        // On Process RecyclerView
        rvOnProcessItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val dataOnProcessOrders: ArrayList<OnProcessItem> = ArrayList()

        for (order in orders) {
            val id = order["id"] as String
            val foodName = (((order["foods"] as ArrayList<*>)[0] as Map<*, *>)["food"] as Map<*, *>)["name"] as String
            val foodStatus = (order["order"] as Map<*, *>)["status"] as String
            val vendorId = (order["order"] as Map<*, *>)["vendor"] as String
            val orderType = (order["order"] as Map<*, *>)["order_type"] as Long
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
                    vendorName = vendorName,
                    orderType = orderType
                )
            )
        }

        rvOnProcessItem.adapter = OnProcessAdapter(requireContext(), dataOnProcessOrders)
    }



    private fun vincentyDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val a = 6378137.0 // equatorial radius in meters
        val f = 1 / 298.257223563 // flattening
        val b = (1 - f) * a // polar radius in meters

        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val lambda1 = Math.toRadians(lon1)
        val lambda2 = Math.toRadians(lon2)

        val L = lambda2 - lambda1
        var lambda = L
        var sigma: Double
        var cosSqAlpha: Double
        var cos2SigmaM: Double
        var sinLambda: Double
        var cosLambda: Double
        var cosU1: Double
        var cosU2: Double
        var sinU1: Double
        var sinU2: Double
        var sinPhi: Double
        var cosPhi: Double
        var tanU1: Double
        var tanU2: Double
        val A: Double
        val B: Double
        val MAX_ITERATIONS = 100
        var iter = 0

        do {
            sinLambda = sin(lambda)
            cosLambda = cos(lambda)
            sinPhi = sin(phi1)
            cosPhi = cos(phi1)
            tanU1 = (1 - f) * tan(phi1)
            tanU2 = (1 - f) * tan(phi2)
            cosU1 = 1 / sqrt((1 + tanU1 * tanU1))
            cosU2 = 1 / sqrt((1 + tanU2 * tanU2))
            sinU1 = tanU1 * cosU1
            sinU2 = tanU2 * cosU2
            sigma = atan2(sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) *
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)),
                sinU1 * sinU2 + cosU1 * cosU2 * cosLambda)
            val sinAlpha = cosU1 * cosU2 * sinLambda / sin(sigma)
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2SigmaM = cos(sigma) - 2 * sinU1 * sinU2 / cosSqAlpha
            val c = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))
            val lambdaP = lambda
            lambda = L + (1 - c) * f * sinAlpha *
                    (sigma + c * sin(sigma) *
                            (cos2SigmaM + c * cos(sigma) *
                                    (-1 + 2 * cos2SigmaM * cos2SigmaM)))
        } while (abs(lambda - lambdaP) > 1e-12 && ++iter < MAX_ITERATIONS)

        if (iter >= MAX_ITERATIONS) {
            throw RuntimeException("Formula failed to converge")
        }

        val uSq: Double = cosSqAlpha * (a * a - b * b) / (b * b)
        A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
        B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
        val deltaSigma: Double = B * sin(sigma) *
                (cos2SigmaM + B / 4 *
                        (cos(sigma) * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM *
                                (-3 + 4 * sin(sigma) * sin(sigma)) *
                                (-3 + 4 * cos2SigmaM * cos2SigmaM)))

        val s = b * A * (sigma - deltaSigma) // Distance in meters

        return s / 1000 // Convert meters to kilometers
    }

}