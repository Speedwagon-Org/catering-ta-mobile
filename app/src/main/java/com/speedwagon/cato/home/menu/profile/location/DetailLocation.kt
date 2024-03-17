package com.speedwagon.cato.home.menu.profile.location

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.speedwagon.cato.R
import com.speedwagon.cato.home.menu.profile.location.adapter.LocationItem

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
        val btnAction = findViewById<Button>(R.id.btn_detail_location_update)

        val auth = FirebaseAuth.getInstance()
        val currentId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("customer")

        if (locationItem != null){
            label.text = Editable.Factory.getInstance().newEditable(locationItem.label)
            detail.text = Editable.Factory.getInstance().newEditable(locationItem.description)
            isDefault.isChecked = locationItem.isDefault
            if (isDefault.isChecked){
                isDefault.isClickable = false
            }
            // Update Data
            btnAction.setOnClickListener {
                val lat = 0.0
                val lng = 0.0
                val geoPoint = GeoPoint(lat, lng)
                val data = hashMapOf(
                    "active" to isDefault.isChecked,
                    "detail" to detail.text.toString(),
                    "label" to label.text.toString(),
                    "location" to geoPoint
                )

                if (detail.text.isNotEmpty() && label.text.isNotEmpty() && currentId != null) {
                    Log.d(TAG, "Notice : Trying to update data")

                    userRef.document(currentId)
                        .collection("location")
                        .document(locationItem.id)
                        .set(data)
                        .addOnSuccessListener {
                            Log.d(TAG, "DocumentSnapshot successfully updated!")
                            Toast.makeText(this, "Location Updated Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error updating document", e)
                        }
                }
            }

        } else {
            btnAction.text = "Tambah Alamat"
            // Add Data
            btnAction.setOnClickListener{
                val lat = 0.0
                val lng = 0.0
                val geoPoint = GeoPoint(lat, lng)
                val data = hashMapOf(
                    "active" to false,
                    "detail" to detail.text.toString(),
                    "label" to label.text.toString(),
                    "location" to geoPoint
                )

                if (detail.text.isNotEmpty() && label.text.isNotEmpty() && currentId != null){
                    Log.d(TAG, "Notice : Trying to write data")
                    userRef.document(currentId).collection("location").add(data).addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                        Toast.makeText(this, "Location Saved Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }
                }
            }
        }
    }
}