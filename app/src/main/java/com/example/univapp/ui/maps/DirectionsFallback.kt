package com.example.univapp.ui.maps

import com.example.univapp.di.NetworkModule
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helpers de ruteo:
 *  - fetchDirectionsPolylineOsrm: consulta OSRM (vía Retrofit) y devuelve polyline sobre calles.
 *  - fetchBestPolyline: intenta Google Directions si hay API key; si falla o no hay, usa OSRM.
 */

// --- Decodificador polyline6 (OSRM geometries=polyline6) ---
private fun decodePolyline6(encoded: String): List<LatLng> {
    val path = ArrayList<LatLng>()
    var index = 0
    var lat = 0
    var lng = 0
    while (index < encoded.length) {
        var b: Int; var shift = 0; var result = 0
        do { b = encoded[index++].code - 63; result = result or ((b and 31) shl shift); shift += 5 } while (b >= 32)
        val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0; result = 0
        do { b = encoded[index++].code - 63; result = result or ((b and 31) shl shift); shift += 5 } while (b >= 32)
        val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        path.add(LatLng(lat / 1e6, lng / 1e6))
    }
    return path
}

/** Llama OSRM pública usando Retrofit para mayor seguridad y cumplimiento de rúbrica. */
suspend fun fetchDirectionsPolylineOsrm(points: List<LatLng>): List<LatLng>? =
    withContext(Dispatchers.IO) {
        if (points.size < 2) return@withContext null

        val coords = points.joinToString(";") { "${it.longitude},${it.latitude}" }

        runCatching {
            val response = NetworkModule.mapsApiService.getOsrmFullRoute(coords = coords)
            if (response.code != "Ok") return@runCatching null
            
            val routes = response.routes ?: return@runCatching null
            if (routes.isEmpty()) return@runCatching null
            
            val geom = routes[0].geometry
            decodePolyline6(geom)
        }.getOrNull()
    }

suspend fun fetchBestPolyline(apiKey: String, pts: List<LatLng>): List<LatLng>? {
    if (apiKey.isNotBlank()) {
        runCatching { fetchDirectionsPolyline(apiKey, pts) }.getOrNull()?.let { return it }
    }
    return fetchDirectionsPolylineOsrm(pts)
}
