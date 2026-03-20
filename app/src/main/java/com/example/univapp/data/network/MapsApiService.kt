package com.example.univapp.data.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapsApiService {
    /**
     * Google Directions API
     */
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("waypoints") waypoints: String?,
        @Query("mode") mode: String = "driving",
        @Query("units") units: String = "metric",
        @Query("key") apiKey: String
    ): DirectionsResponse

    /**
     * OSRM API - Basic Route (for ETA)
     */
    @GET("https://router.project-osrm.org/route/v1/driving/{coords}")
    suspend fun getOsrmRoute(
        @Path("coords") coords: String,
        @Query("overview") overview: String = "false"
    ): OsrmResponse

    /**
     * OSRM API - Full Route (for Polylines)
     */
    @GET("https://router.project-osrm.org/route/v1/driving/{coords}")
    suspend fun getOsrmFullRoute(
        @Path("coords") coords: String,
        @Query("overview") overview: String = "full",
        @Query("geometries") geometries: String = "polyline6"
    ): OsrmFullResponse
}

data class DirectionsResponse(
    val status: String,
    val routes: List<GoogleRoute>
)

data class GoogleRoute(
    val overview_polyline: PolylinePoints
)

data class PolylinePoints(
    val points: String
)

data class OsrmResponse(
    val code: String,
    val routes: List<OsrmRoute>
)

data class OsrmRoute(
    val duration: Double,
    val distance: Double
)

data class OsrmFullResponse(
    val code: String,
    val message: String?,
    val routes: List<OsrmFullRoute>?
)

data class OsrmFullRoute(
    val geometry: String
)
