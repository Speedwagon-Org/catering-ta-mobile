package com.speedwagon.cato.home.menu.profile.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.speedwagon.cato.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale

@Suppress("DEPRECATION")
class LocationPicker : AppCompatActivity() {
    private var marker: Marker? = null
    private lateinit var etLocationDetail: EditText
    private lateinit var fabSetLocation : FloatingActionButton
    private lateinit var currentLoc : GeoPoint
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        etLocationDetail = findViewById(R.id.et_location_picker_location_detail)
        fabSetLocation = findViewById(R.id.fab_location_picker_set_location)

        fabSetLocation.visibility = View.GONE
        // Initialize OSM configuration
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Initialize MapView
        val mapView = findViewById<MapView>(R.id.mv_location_picker_mapview)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setMultiTouchControls(true)

        // Set initial map position to Medan, Sumatera Utara, Indonesia
        val initialLat = intent.getDoubleExtra("latitude", 3.5952)
        val initialLng = intent.getDoubleExtra("longitude",98.6722)
        currentLoc = GeoPoint(initialLat, initialLng)
        // Set initial map position
        val mapController = mapView.controller
        mapController.setZoom(20.0)
        mapController.setCenter(GeoPoint(initialLat, initialLng))

        // Add click listener to the map
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                moveMarker(mapView, p.latitude, p.longitude)
                updateLocationDetail(p)
                if (currentLoc != p){
                    fabSetLocation.visibility = View.VISIBLE
                    if (getLocationName(p.latitude, p.longitude) =="None"){
                        fabSetLocation.visibility = View.GONE
                    }
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                // Do nothing for long press
                return false
            }
        })
        mapView.overlays.add(0, mapEventsOverlay)

        // Initialize marker at initial location
        marker = Marker(mapView)
        marker!!.position = GeoPoint(initialLat, initialLng)
        //marker!!.icon = resources.getDrawable(R.drawable.red_marker)
        mapView.overlays.add(marker)
        mapView.invalidate()

        // Set initial location detail
        updateLocationDetail(GeoPoint(initialLat, initialLng))

        // Update location detail when marker moves
        marker!!.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker?) {
                // Do nothing on drag
            }

            override fun onMarkerDragEnd(marker: Marker?) {
                updateLocationDetail(marker!!.position)
            }

            override fun onMarkerDragStart(marker: Marker?) {
                // Do nothing on drag start
            }
        })

        // Apply change
        fabSetLocation.setOnClickListener {
            val intent = Intent()
            intent.putExtra("latitude", marker?.position?.latitude)
            intent.putExtra("longitude", marker?.position?.longitude)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun moveMarker(mapView: MapView, latitude: Double, longitude: Double) {
        // Update marker position to the clicked location
        marker?.position = GeoPoint(latitude, longitude)
        mapView.invalidate()
        updateLocationDetail(GeoPoint(latitude, longitude))
    }

    @SuppressLint("SetTextI18n")
    private fun updateLocationDetail(geoPoint: GeoPoint) {
        etLocationDetail.setText(getLocationName(geoPoint.latitude, geoPoint.longitude))
    }

    private fun getLocationName(latitude: Double, longitude: Double): String {
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
        if (town == null || street == null){
            return "None"
        }
        return "$town, $street"
    }
}
