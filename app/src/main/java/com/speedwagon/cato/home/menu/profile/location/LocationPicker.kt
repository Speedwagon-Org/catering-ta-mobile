package com.speedwagon.cato.home.menu.profile.location

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speedwagon.cato.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class LocationPicker : AppCompatActivity() {
    private var previousMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_picker)

        // Initialize OSM configuration
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))

        // Initialize MapView
        val mapView = findViewById<MapView>(R.id.mv_location_picker_mapview)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Set initial map position
        val mapController = mapView.controller
        mapController.setZoom(15.0)
        mapController.setCenter(GeoPoint(51.5074, -0.1278)) // London coordinates

        // Add click listener to the map
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                addMarker(mapView, p.latitude, p.longitude)
                return true
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                // Do nothing for long press
                return false
            }
        })
        mapView.overlays.add(0, mapEventsOverlay)
    }

    private fun addMarker(mapView: MapView, latitude: Double, longitude: Double) {
        // Remove previous marker
        if (previousMarker != null) {
            mapView.overlays.remove(previousMarker)
        }

        // Add marker to the clicked location
        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        mapView.overlays.add(marker)
        previousMarker = marker

        // Redraw the map
        mapView.invalidate()
    }
}
