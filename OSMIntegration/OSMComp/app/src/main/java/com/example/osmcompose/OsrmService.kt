package com.example.osmcompose

import com.example.osmcompose.ui.theme.OsrmRouteResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OsrmService {
    @GET("route/v1/driving/{start};{end}")
    fun getRoute(
        @Path("start") start: String, // "longitude,latitude"
        @Path("end") end: String, // "longitude,latitude"
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "geojson"
    ): Call<OsrmRouteResponse>

    companion object {
        fun create(): OsrmService {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(OsrmService::class.java)
        }
    }
}
