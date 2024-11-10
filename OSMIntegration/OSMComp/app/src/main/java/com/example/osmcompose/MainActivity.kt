package com.example.osmcompose

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import java.io.InputStream

data class ParkingLot(val location: String, val geoPoint: GeoPoint?, val polygonPoints: List<GeoPoint>?)

class MainActivity : ComponentActivity() {
    private val osrmService = OsrmService.create()
    private var parkingLots = mutableListOf<ParkingLot>()  // List to hold all parsed parking lots
    private val nominatimService = NominatimService.create()

    private var startMarker: Marker? = null
    private var drawnRoute : Polyline? = null

    private var selectedParkingLot = mutableStateOf<ParkingLot?>(null)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set user agent for osmdroid
        Configuration.getInstance().userAgentValue = packageName

        setContent {
            MyOSMMapScreen()
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyOSMMapScreen() {
        val coroutineScope = rememberCoroutineScope()
        var mapView: MapView? = null

        // State variables for start and end locations
        var startPoint by remember { mutableStateOf<GeoPoint?>(null) }
//        var selectedParkingLot by remember { mutableStateOf<ParkingLot?>(null) }  // Selected parking lot for the route

        // Search state and radius input
        var searchQuery by remember { mutableStateOf("") }
        var filteredParkingLots by remember { mutableStateOf<List<ParkingLot>>(emptyList()) }
        var radiusInput by remember { mutableStateOf("1") }  // Default radius of 1 km

        // Load parking lot data
        LaunchedEffect(Unit) {
            coroutineScope.launch { loadParkingLotData() }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("OSM Directions") })
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp,60.dp,16.dp,16.dp)
                ) {
                    AndroidView(factory = { context ->
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setCenter(GeoPoint(44.0165, 21.0059))  // Center in Serbia
                            controller.setZoom(10.0)
                            minZoomLevel = 8.0
                            mapView = this
                        }
                    }, update = {
                        mapView = it
                    })

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .width(250.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search Location") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = radiusInput,
                            onValueChange = { radiusInput = it },
                            label = { Text("Radius (km)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val geoLocation = geocodeLocation(searchQuery)
                                    val radius = radiusInput.toDoubleOrNull() ?: 1.0  // Default to 1 km if invalid
                                    if (geoLocation != null) {
                                        filteredParkingLots = filterParkingLotsByLocation(geoLocation, radius)
                                        mapView?.let { map ->
                                            displayFilteredParkingLots(map, filteredParkingLots)
                                            map.controller.setCenter(geoLocation)
                                            map.controller.setZoom(16.0)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Search")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {

                            startPoint = mapView?.mapCenter as? GeoPoint
                            startPoint?.let { point ->

                                startMarker?.let { mapView?.overlays?.remove(it) }

                                startMarker = addMarker(mapView!!, point, "Start Point", R.drawable.start_marker, draggable = true) { updatedPoint ->
                                    startPoint = updatedPoint

                                    if (selectedParkingLot.value?.geoPoint != null) {

                                        coroutineScope.launch {
                                            drawnRoute?.let { mapView?.overlays?.remove(it) }
                                            drawnRoute = drawRoute(mapView!!, startPoint!!, selectedParkingLot.value?.geoPoint!!)
                                        }
                                    }
                                }
                            }
                        }) {
                            Text("Set Start Point")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                selectedParkingLot.value?.geoPoint?.let { destination ->
                                    coroutineScope.launch {
                                        startPoint?.let { start ->
                                            drawnRoute?.let { mapView?.overlays?.remove(it) }
                                            drawnRoute = drawRoute(mapView!!, start, destination)
                                        }
                                    }
                                }
                            },
                            enabled = startPoint != null && selectedParkingLot.value?.geoPoint != null
                        ) {
                            Text("Show Route")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(onClick = {
                            clearMarkersAndRoute(mapView!!)
                            startPoint = null
                            selectedParkingLot.value = null
                        }) {
                            Text("Clear Markers")
                        }
                    }
                }
            }
        )
    }



    // Function to clear all markers and route
    private fun clearMarkersAndRoute(mapView: MapView) {
        mapView.overlays.clear()
        mapView.invalidate() // Refresh map to remove overlays
    }

    // Function to add a marker on the map with scaled icon and drag listener
    private fun addMarker(
        mapView: MapView,
        position: GeoPoint,
        title: String,
        iconResId: Int,
        draggable: Boolean = false,
        onPositionUpdated: (GeoPoint) -> Unit
    ): Marker {
        val marker = Marker(mapView).apply {
            this.position = position
            this.title = title
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            isDraggable = draggable

            icon = getScaledDrawable(mapView.context.getDrawable(iconResId), mapView,50, 50)

            if (isDraggable) {
                setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                    override fun onMarkerDrag(marker: Marker?) {}

                    override fun onMarkerDragEnd(marker: Marker?) {
                        marker?.let {
                            onPositionUpdated(it.position)
                        }
                    }

                    override fun onMarkerDragStart(marker: Marker?) {}
                })
            }
        }
        mapView.overlays.add(marker)
        mapView.invalidate()
        return marker
    }

    // Function to scale drawable for marker
    private fun getScaledDrawable(drawable: Drawable?,mapView: MapView, width: Int, height: Int): Drawable? {
        drawable ?: return null
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, width, height)
        drawable.draw(canvas)
        return BitmapDrawable(mapView!!.context.resources, bitmap)
    }

    // Function to fetch and draw route between start and end points
    private suspend fun drawRoute(mapView: MapView, start: GeoPoint, end: GeoPoint) :Polyline? {
        val startCoords = "${start.longitude},${start.latitude}"
        val endCoords = "${end.longitude},${end.latitude}"

        try {
            val response = withContext(Dispatchers.IO) {
                osrmService.getRoute(startCoords, endCoords).execute()
            }

            if (response.isSuccessful) {
                val routeResponse = response.body()
                val geometry = routeResponse?.routes?.firstOrNull()?.geometry
                geometry?.let {
                    val geoPoints = it.coordinates.map { coord ->
                        GeoPoint(coord[1], coord[0])
                    }
                    val polyline = Polyline().apply {
                        setPoints(geoPoints)
                        outlinePaint.color = Color.Blue.toArgb()
                    }
                    mapView.overlays.removeIf { overlay -> overlay is Polyline }
                    mapView.overlays.add(polyline)
                    mapView.invalidate()
                    return polyline
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // Load and parse GeoJSON data, storing it in `parkingLots`
    private suspend fun loadParkingLotData() {
        withContext(Dispatchers.IO) {
            try {
                val inputStream: InputStream = assets.open("Srbija.geojson")
                val geoJsonString = inputStream.bufferedReader().use { it.readText() }

                val jsonObject = JSONObject(geoJsonString)
                val features = jsonObject.getJSONArray("features")

                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    val properties = feature.getJSONObject("properties")
                    val locationName = properties.optString("name", "Unknown Location")
                    val geometry = feature.getJSONObject("geometry")
                    val coordinates = geometry.getJSONArray("coordinates")

                    when (geometry.getString("type")) {
                        "Point" -> {
                            val point = GeoPoint(
                                coordinates.getDouble(1),
                                coordinates.getDouble(0)
                            )
                            parkingLots.add(ParkingLot(locationName, point, null))
                        }
                        "Polygon" -> {
                            val polygonPoints = mutableListOf<GeoPoint>()
                            val polygonCoords = coordinates.getJSONArray(0)
                            for (j in 0 until polygonCoords.length()) {
                                val coord = polygonCoords.getJSONArray(j)
                                val lat = coord.getDouble(1)
                                val lon = coord.getDouble(0)
                                polygonPoints.add(GeoPoint(lat, lon))
                            }
                            parkingLots.add(ParkingLot(locationName, null, polygonPoints))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private fun displayFilteredParkingLots(mapView: MapView, filteredLots: List<ParkingLot>) {
        mapView.overlays.clear()

        for (lot in filteredLots) {
            if (lot.geoPoint != null) {
                // Add a marker for point-based parking lots
                val marker = addMarker(
                    mapView,
                    lot.geoPoint,
                    lot.location,
                    R.drawable.parking_lot,
                    onPositionUpdated = {}
                )

                // Set a click listener to mark this lot as the selected parking lot
                marker.setOnMarkerClickListener { clickedMarker, _ ->
                    selectedParkingLot.value = lot
                    clickedMarker.showInfoWindow()  // Show info window on the selected marker
                    true  // Returning true to consume the click event
                }
            } else if (lot.polygonPoints != null) {
                // Add a polygon for polygon-based parking lots
                addPolygon(mapView, lot.polygonPoints)
            }
        }
        mapView.invalidate()
    }


    // Function to add a polygon overlay on the map
    private fun addPolygon(mapView: MapView, points: List<GeoPoint>) {
        val polygon = Polygon().apply {
            this.points = points
            fillColor = Color(0x55001AFF).toArgb()
            strokeColor = Color.Blue.toArgb()
            strokeWidth = 2f
        }
        polygon.setOnClickListener { _, _, _ ->
            // Display a simple message or dialog with polygon information
            selectedParkingLot.value = ParkingLot("",calculateCentroid(points),points)
            true // return true to consume the click event
        }
        mapView.overlays.add(polygon)
    }
    // Geocode the location string to a GeoPoint using Nominatim API
    private suspend fun geocodeLocation(location: String): GeoPoint? {
        return try {
            val response = withContext(Dispatchers.IO) {
                nominatimService.geocode(location, "json", 1,1,"testApp").execute()
            }
            if (response.isSuccessful) {
                val result = response.body()?.firstOrNull()
                val lat = result?.lat?.toDoubleOrNull()
                val lon = result?.lon?.toDoubleOrNull()
                if (lat != null && lon != null) GeoPoint(lat, lon) else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun calculateCentroid(points: List<GeoPoint>): GeoPoint {
        var centroidLat = 0.0
        var centroidLon = 0.0

        for (point in points) {
            centroidLat += point.latitude
            centroidLon += point.longitude
        }

        val totalPoints = points.size
        return GeoPoint(centroidLat / totalPoints, centroidLon / totalPoints)
    }

    // Update the filter function to include polygon-based parking lots
    private fun filterParkingLotsByLocation(location: GeoPoint, radiusKm: Double): List<ParkingLot> {
        return parkingLots.filter { lot ->
            if (lot.geoPoint != null) {
                // Calculate distance for point-based parking lots
                val distance = calculateDistance(location, lot.geoPoint)
                distance <= radiusKm * 1000 // Convert km to meters
            } else if (lot.polygonPoints != null) {
                // Calculate centroid and use it to determine if within radius
                val centroid = calculateCentroid(lot.polygonPoints)
                val distance = calculateDistance(location, centroid)
                distance <= radiusKm * 1000
            } else {
                false
            }
        }
    }

    // Function to calculate distance between two GeoPoints in meters
    private fun calculateDistance(start: GeoPoint, end: GeoPoint): Float {
        val startLocation = android.location.Location("start").apply {
            latitude = start.latitude
            longitude = start.longitude
        }
        val endLocation = android.location.Location("end").apply {
            latitude = end.latitude
            longitude = end.longitude
        }
        return startLocation.distanceTo(endLocation) // Returns distance in meters
    }
}
