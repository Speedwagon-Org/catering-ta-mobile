package com.speedwagon.cato.home.menu.profile.location

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.speedwagon.cato.R

class LocationPicker : AppCompatActivity() {
    private lateinit var mapView: MapView
    private var pointAnnotationManager: PointAnnotationOptions? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(R.layout.activity_location_picker)
        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lng = intent.getDoubleExtra("longitude", 0.0)

        if (lat == 0.0 && lng == 0.0) {
            finish()
        } else {
//            mapView = findViewById(R.id.mapView)
//            val annotationApi = mapView.annotations
//
//            val annotationConfig = AnnotationConfig(
//                layerId = "map_annotation",
//
//            )
//            val redMarker = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
//
//            val pointAnnotationOptions = PointAnnotationOptions()
//                .withPoint(Point.fromLngLat(lng, lat))
//                .withIconImage(redMarker)
//
//            pointAnnotationManager
            mapView.getMapboxMap().loadStyleUri("mapbox://styles/titorahman/cltz96mz000qc01p7002c69fd") { style ->
                mapView.getMapboxMap().setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(lng, lat))
                        .zoom(16.0)
                        .build()
                )
            }
        }
    }

}
