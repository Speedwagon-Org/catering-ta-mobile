package com.speedwagon.cato.home.menu.profile.location

import android.annotation.SuppressLint
import android.app.Activity
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
    private lateinit var label : EditText
    private lateinit var detail : EditText
    private lateinit var isDefault : SwitchMaterial
    private lateinit var btnAction : FloatingActionButton
    private lateinit var btnDelete : FloatingActionButton
    private lateinit var btnPickLocation : Button
    private lateinit var detailLocation : TextView
    private var newLat : Double? = null
    private var newLng : Double? = null
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_location)
        val locationItem = intent.getParcelableExtra<LocationItem>("data")

        label = findViewById(R.id.et_detail_location_label)
        detail = findViewById(R.id.et_detail_location_description)
        isDefault = findViewById(R.id.sw_detail_location)
        btnAction = findViewById(R.id.fab_location_confirm)
        btnDelete = findViewById(R.id.fab_location_delete)
        btnPickLocation = findViewById(R.id.btn_detail_location_pick_location)
        detailLocation = findViewById(R.id.tv_detail_location_town)
        val currentLocationIsDefault = isDefault.isChecked

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
                intent.putExtra("latitude", 3.5952 )
                intent.putExtra("longitude",98.6722)
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
                var lat = locationItem.lat
                var lng = locationItem.long
                if (newLat != null && newLng != null){
                    lat = newLat!!
                    lng = newLng!!
                }

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
                                        val intent = Intent()
                                        setResult(Activity.RESULT_OK, intent)
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
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
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
                                    if (!currentLocationIsDefault){
                                        Toast.makeText(this, "Lokasi telah dihapus", Toast.LENGTH_SHORT).show()
                                        val intent = Intent()
                                        setResult(Activity.RESULT_OK, intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Gagal menghapus dikarenakan ini adalah lokasi utama", Toast.LENGTH_SHORT).show()

                                    }

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
            var lat = 3.5952
            var lng = 98.6722
            if (newLat != null && newLng != null){
                lat = newLat!!
                lng = newLng!!
            }
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
                                    val intent = Intent()
                                    setResult(Activity.RESULT_OK, intent)
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

    @SuppressLint("SetTextI18n")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            newLat = data?.getDoubleExtra("latitude", 0.0)
            newLng = data?.getDoubleExtra("longitude", 0.0)
            detailLocation.text = "${getTownAndStreet(newLat!!, newLng!!).first}, ${getTownAndStreet(newLat!!, newLng!!).second}"
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }

}