package com.speedwagon.cato.home.menu.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.adapter.search.SearchVendorAdapter
import com.speedwagon.cato.home.menu.adapter.search.item.Vendor
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

class PesanAntarFragment : Fragment() {
    private lateinit var rvSearchVendor: RecyclerView
    private lateinit var auth : FirebaseAuth
    private lateinit var vendors : ArrayList<Map<String, *>>
    private lateinit var db : FirebaseFirestore
    private lateinit var userRef : CollectionReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pesan_antar, container, false)
        rvSearchVendor = view.findViewById(R.id.rv_search_vendor)
        rvSearchVendor.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false)
        db = FirebaseFirestore.getInstance()
        vendors = ArrayList()
        auth = FirebaseAuth.getInstance()
        userRef = db.collection("customer")
        val vendorRef = db.collection("vendor")
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
                    recyclerviewInitialization()
                }
            }
        }


        recyclerviewInitialization()


        return view
    }
    private fun recyclerviewInitialization(){
        val dataVendor : ArrayList<Vendor> = ArrayList()
        var vendorsProcessed = 0
        for (vendor in vendors){
            val id = vendor["id"] as String
            val name = (vendor["data"] as Map<*,*>) ["name"] as String
            val img =(vendor["data"] as Map<*,*>) ["profile_picture"] as String
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img)
            val vendorLat = ((vendor["data"] as Map<*, *>)["location"] as GeoPoint).latitude
            val vendorLng = ((vendor["data"] as Map<*, *>)["location"] as GeoPoint).longitude
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
                                        dataVendor.add(
                                            Vendor(
                                                id = id,
                                                name = name,
                                                distance = distance,
                                                imgUrl = storageReference
                                            )
                                        )

                                    }
                                    vendorsProcessed++
                                    if (vendorsProcessed == vendors.size) {
                                        rvSearchVendor.adapter = SearchVendorAdapter(requireContext(), dataVendor, 0)
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }
    }
    private fun vincentyDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val a = 6378137.0 // equatorial radius in meters
        val f = 1 / 298.257223563 // flattening
        val b = (1 - f) * a // polar radius in meters

        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val lambda1 = Math.toRadians(lon1)
        val lambda2 = Math.toRadians(lon2)

        val lL = lambda2 - lambda1
        var lambda = lL
        var sigma: Double
        var cosSqAlpha: Double
        var cos2SigmaM: Double
        var sinLambda: Double
        var cosLambda: Double
        var cosU1: Double
        var cosU2: Double
        var sinU1: Double
        var sinU2: Double
        var tanU1: Double
        var tanU2: Double
        val aA: Double
        val deltaSigma: Double
        val MAX_ITERATIONS = 100
        var iter = 0

        do {
            sinLambda = sin(lambda)
            cosLambda = cos(lambda)
            tanU1 = (1 - f) * tan(phi1)
            tanU2 = (1 - f) * tan(phi2)
            cosU1 = 1 / sqrt((1 + tanU1 * tanU1))
            cosU2 = 1 / sqrt((1 + tanU2 * tanU2))
            sinU1 = tanU1 * cosU1
            sinU2 = tanU2 * cosU2
            sigma = atan2(
                sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                        (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) *
                        (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)),
                sinU1 * sinU2 + cosU1 * cosU2 * cosLambda)
            val sinAlpha = cosU1 * cosU2 * sinLambda / sin(sigma)
            cosSqAlpha = 1 - sinAlpha * sinAlpha
            cos2SigmaM = cos(sigma) - 2 * sinU1 * sinU2 / cosSqAlpha
            val cC = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha))
            val lambdaP = lambda
            lambda = lL + (1 - cC) * f * sinAlpha *
                    (sigma + cC * sin(sigma) *
                            (cos2SigmaM + cC * cos(sigma) *
                                    (-1 + 2 * cos2SigmaM * cos2SigmaM)))
        } while (abs(lambda - lambdaP) > 1e-12 && ++iter < MAX_ITERATIONS)

        if (iter >= MAX_ITERATIONS) {
            throw RuntimeException("Formula failed to converge")
        }

        val uSq: Double = cosSqAlpha * (a * a - b * b) / (b * b)
        aA = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)))
        val bB: Double = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)))
        deltaSigma = bB * sin(sigma) *
                (cos2SigmaM + bB / 4 *
                        (cos(sigma) * (-1 + 2 * cos2SigmaM * cos2SigmaM) - bB / 6 * cos2SigmaM *
                                (-3 + 4 * sin(sigma) * sin(sigma)) *
                                (-3 + 4 * cos2SigmaM * cos2SigmaM)))

        val s = b * aA * (sigma - deltaSigma) // Distance in meters

        return s / 1000 // Convert meters to kilometers
    }

}