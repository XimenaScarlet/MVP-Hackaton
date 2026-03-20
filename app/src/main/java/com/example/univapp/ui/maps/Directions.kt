package com.example.univapp.ui.maps

import com.example.univapp.data.network.MapsApiService
import com.example.univapp.di.NetworkModule
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Directions migrado a Retrofit para mayor seguridad y centralización.
 */
@Singleton
class DirectionsFetcher @Inject constructor(
    private val apiService: MapsApiService
) {
    suspend fun fetchDirectionsPolyline(
        apiKey: String,
        points: List<LatLng>
    ): List<LatLng>? = com.example.univapp.ui.maps.fetchDirectionsPolyline(apiKey, points)
}

// Decodifica polyline de Google Directions
private fun decodePolyline(encoded: String): List<LatLng> {
    val poly = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0
    while (index < encoded.length) {
        var b: Int; var shift = 0; var result = 0
        do { b = encoded[index++].code - 63; result = result or ((b and 0x1F) shl shift); shift += 5 } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat
        shift = 0; result = 0
        do { b = encoded[index++].code - 63; result = result or ((b and 0x1F) shl shift); shift += 5 } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng
        poly.add(LatLng(lat / 1E5, lng / 1E5))
    }
    return poly
}

/**
 * Función global para compatibilidad con el código existente.
 * Usa el puente estático de NetworkModule para obtener el servicio de Retrofit.
 */
suspend fun fetchDirectionsPolyline(
    apiKey: String,
    points: List<LatLng>
): List<LatLng>? = withContext(Dispatchers.IO) {
    if (apiKey.isBlank() || points.size < 2) return@withContext null

    val origin = "${points.first().latitude},${points.first().longitude}"
    val dest   = "${points.last().latitude},${points.last().longitude}"
    val middle = points.drop(1).dropLast(1)
    val waypoints = if (middle.isNotEmpty()) {
        "via:" + middle.joinToString("|via:") { "${it.latitude},${it.longitude}" }
    } else null

    runCatching {
        val response = NetworkModule.mapsApiService.getDirections(
            origin = origin,
            destination = dest,
            waypoints = waypoints,
            apiKey = apiKey
        )
        if (response.status != "OK") return@runCatching null
        val overview = response.routes.firstOrNull()?.overview_polyline?.points ?: return@runCatching null
        decodePolyline(overview)
    }.getOrNull()
}
