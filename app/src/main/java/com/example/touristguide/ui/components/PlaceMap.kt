package com.example.touristguide.ui.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.example.touristguide.data.model.Place
import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.Polyline

@Composable
fun PlaceMap(
    context: Context,
    places: List<Place>,
    currentLocation: GeoPoint?,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = {
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            val allGeoPoints = mutableListOf<GeoPoint>()
            // Add current location marker
            currentLocation?.let { geoPoint ->
                val marker = Marker(mapView)
                marker.position = geoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "You are here"
                mapView.overlays.add(marker)
                allGeoPoints.add(geoPoint)
            }
            // Add place markers
            places.forEach { place ->
                val marker = Marker(mapView)
                marker.position = GeoPoint(place.latitude, place.longitude)
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = place.name
                marker.snippet = place.address
                mapView.overlays.add(marker)
                allGeoPoints.add(marker.position)
            }
            // Draw line between user and first place
            if (currentLocation != null && places.isNotEmpty()) {
                val polyline = Polyline().apply {
                    setPoints(listOf(currentLocation, GeoPoint(places[0].latitude, places[0].longitude)))
                    outlinePaint.color = android.graphics.Color.BLUE
                    outlinePaint.strokeWidth = 5f
                }
                mapView.overlays.add(polyline)
            }
            // Center and zoom logic
            if (currentLocation != null) {
                mapView.controller.setCenter(currentLocation)
                mapView.controller.setZoom(15.0)
            } else if (allGeoPoints.size == 1) {
                mapView.controller.setCenter(allGeoPoints.first())
                mapView.controller.setZoom(15.0)
            } else if (allGeoPoints.size > 1) {
                val lats = allGeoPoints.map { it.latitude }
                val lons = allGeoPoints.map { it.longitude }
                val bbox = BoundingBox(
                    lats.maxOrNull() ?: 0.0,
                    lons.maxOrNull() ?: 0.0,
                    lats.minOrNull() ?: 0.0,
                    lons.minOrNull() ?: 0.0
                )
                mapView.zoomToBoundingBox(bbox, true)
            }
            mapView.invalidate()
        },
        modifier = modifier
    )
} 