package com.speedwagon.cato.home.menu.profile.location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.adapter.LocationItem
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION")
class DetailLocation : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_location)
        val locationItem = intent.getParcelableExtra<LocationItem>("data")

        val label = findViewById<EditText>(R.id.et_detail_location_label)
        val detail = findViewById<EditText>(R.id.et_detail_location_description)
        val isDefault = findViewById<SwitchMaterial>(R.id.sw_detail_location)
        val btnAction = findViewById<FloatingActionButton>(R.id.fab_location_confirm)
        val btnDelete = findViewById<FloatingActionButton>(R.id.fab_location_delete)
        val btnPickLocation = findViewById<Button>(R.id.btn_detail_location_pick_location)
        val detailLocation = findViewById<TextView>(R.id.tv_detail_location_town)

        val auth = FirebaseAuth.getInstance()
        val currentId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("customer")

        if (locationItem == null) {
            detailLocation.text = "Tambah titik"
        } else {
            val (town, street) = getTownAndStreet(locationItem.lat, locationItem.long)
            detailLocation.text = "$town, $street"
        }

        btnPickLocation.setOnClickListener {
            val intent = Intent(this, LocationPicker::class.java)
            if (locationItem != null){
                intent.putExtra("latitude", locationItem.lat )
                intent.putExtra("longitude", locationItem.long )
            } else {
                intent.putExtra("latitude", 3.5876939752586146 )
                intent.putExtra("longitude",98.69074579547087 )
            }

            startActivityForResult(intent, REQUEST_CODE)
        }
        if (locationItem != null){

            label.text = Editable.Factory.getInstance().newEditable(locationItem.label)
            detail.text = Editable.Factory.getInstance().newEditable(locationItem.description)
            isDefault.isChecked = locationItem.isDefault
            if (isDefault.isChecked){
                isDefault.isClickable = false
            }
            // Update Data
            btnAction.setOnClickListener {
                val lat = locationItem.lat
                val lng = locationItem.long
                val geoPoint = GeoPoint(lat, lng)
                val locationData = hashMapOf(
                    "detail" to detail.text.toString(),
                    "label" to label.text.toString(),
                    "location" to geoPoint
                )
                var defaultLocation : Map<String, Any> = hashMapOf()
                if (isDefault.isChecked){
                    defaultLocation = hashMapOf(
                        "default_location" to locationItem.id
                    )
                }
                if (defaultLocation["default_location"] != locationItem.isDefault || locationData["detail"] != locationItem.description || locationData["label"] != locationItem.label ){
                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Apakah anda yakin memperbaharui lokasi ${locationItem.label}?")
                        .setPositiveButton("Iya") { _, _ ->
                            if (detail.text.isNotEmpty() && label.text.isNotEmpty() && currentId != null) {
                                Log.d(TAG, "Notice : Trying to update data")

                                userRef.document(currentId).update(defaultLocation).addOnSuccessListener {
                                    Log.d(TAG, "Default location successfully updated!")
                                }.addOnFailureListener { e ->
                                    Log.w(TAG, "Default location failed updated", e)
                                }

                                userRef.document(currentId)
                                    .collection("location")
                                    .document(locationItem.id)
                                    .set(locationData)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                                        Toast.makeText(this, "Lokasi telah di perbaharui", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error updating document", e)
                                    }
                            }
                        }
                        .setNegativeButton("Tidak", null)
                        .show()
                } else {
                    finish()
                }
            }

            btnDelete.setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Hapus Lokasi")
                    .setMessage("Apakah anda yakin menghapus lokasi ${locationItem.label}?")
                    .setPositiveButton("Iya") { _, _ ->
                        if (currentId != null) {
                            userRef
                                .document(currentId)
                                .collection("location")
                                .document(locationItem.id)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                                    Toast.makeText(this, "Lokasi telah dihapus", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error deleting document", e)
                                    Toast.makeText(this, "Lokasi gagal untuk dihapus", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .setNegativeButton("Tidak", null)
                    .show()

            }

        } else {
            btnDelete.visibility = View.GONE
            // Add Data
            val lat = 3.5876939752586146
            val lng = 98.69074579547087
            val geoPoint = GeoPoint(lat, lng)
            btnAction.setOnClickListener{


                val data = hashMapOf(
                    "detail" to detail.text.toString(),
                    "label" to label.text.toString(),
                    "location" to geoPoint
                )

                if (detail.text.isNotEmpty() && label.text.isNotEmpty() && currentId != null){

                    AlertDialog.Builder(this)
                        .setTitle("Logout")
                        .setMessage("Apakah anda yakin untuk menambahkan lokasi ${data["label"]}?")
                        .setPositiveButton("Iya") { _, _ ->
                            Log.d(TAG, "Notice : Trying to write data")
                            userRef.document(currentId).collection("location").add(data)
                                .addOnSuccessListener { documentReference ->
                                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                                Toast.makeText(this, "Location Saved Successfully", Toast.LENGTH_SHORT).show()
                                finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                        .setNegativeButton("Tidak", null)
                        .show()

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
    companion object {
        const val REQUEST_CODE = 100
    }

}