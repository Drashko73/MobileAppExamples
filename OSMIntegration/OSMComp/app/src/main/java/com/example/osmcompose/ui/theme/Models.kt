package com.example.osmcompose.ui.theme

data class OsrmRouteResponse(
    val routes: List<Route>
)

data class Route(
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<Double>> // [longitude, latitude] pairs
)
