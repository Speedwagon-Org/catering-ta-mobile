package com.speedwagon.cato.home.menu.profile.location

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.adapter.LocationItem
import com.speedwagon.cato.home.menu.profile.location.adapter.LocationsAdapter

class Location : AppCompatActivity() {
    private lateinit var rvListLocation : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        rvListLocation = findViewById(R.id.rv_location_list)
        val fabBtn = findViewById<FloatingActionButton>(R.id.fab_location_list)

        fabBtn.setOnClickListener{
            val intent = Intent(this, DetailLocation::class.java)
            startActivity(intent)
        }

        loadLocations()
    }

    private fun loadLocations() {
        val auth = FirebaseAuth.getInstance()
        val currentId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("customer")
        val locations = ArrayList<LocationItem>()

        if (currentId != null) {
            var defaultLocationId : String
            userRef.document(currentId).get().addOnCompleteListener { userTask ->
                if (userTask.isSuccessful){
                    val data = userTask.result
                    if (data.exists()){
                        defaultLocationId = data.get("default_location") as String

                        userRef.document(currentId).collection("location").get().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val res = task.result
                                if (res != null) {
                                    for (doc in res) {
                                        locations.add(
                                            LocationItem(
                                                id = doc.id,
                                                label = doc.get("label") as String,
                                                description = doc.get("detail") as String,
                                                lat = doc.getGeoPoint("location")!!.latitude,
                                                long = doc.getGeoPoint("location")!!.longitude,
                                                isDefault = (defaultLocationId == doc.id)
                                            )
                                        )
                                    }
                                    rvListLocation.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                                    rvListLocation.adapter = LocationsAdapter(this, locations)
                                } else {
                                    Log.d(TAG, "No documents found")
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ${task.exception}")
                            }
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationsAdapter.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadLocations()
        }
    }
}
