package com.example.univapp.ui.maps

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Helpers de ruteo:
 *  - fetchDirectionsPolylineOsrm: consulta OSRM (sin API key) y devuelve polyline sobre calles.
 *  - fetchBestPolyline: intenta Google Directions si hay API key; si falla o no hay, usa OSRM.
 *
 * Nota: La función `fetchDirectionsPolyline(apiKey, points)` de GOOGLE
 *       debe estar definida en tu archivo directions.kt (no se redefine aquí).
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

/** Llama OSRM pública (sin API key). OJO: coord en formato LON,LAT;LON,LAT… */
suspend fun fetchDirectionsPolylineOsrm(points: List<LatLng>): List<LatLng>? =
    withContext(Dispatchers.IO) {
        if (points.size < 2) return@withContext null

        // ¡NO URLEncoder! No codifiques ';' y ','
        val coords = points.joinToString(";") { "${it.longitude},${it.latitude}" }
        val url = "https://router.project-osrm.org/route/v1/driving/$coords?overview=full&geometries=polyline6"

        runCatching {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000; readTimeout = 10000
            }
            val text = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(text)

            if (json.optString("code") != "Ok") {
                Log.w("OSRM", "code=${json.optString("code")} msg=${json.optString("message")}")
                return@withContext null
            }

            val routes = json.optJSONArray("routes") ?: return@withContext null
            if (routes.length() == 0) return@withContext null
            val geom = routes.getJSONObject(0).getString("geometry") // polyline6
            decodePolyline6(geom)
        }.getOrNull()
    }

/**
 * Wrapper: usa Google si hay API key; si falla o no hay, usa OSRM.
 * Requiere que exista directions.kt con:
 * suspend fun fetchDirectionsPolyline(apiKey: String, points: List<LatLng>): List<LatLng>?
 */
suspend fun fetchBestPolyline(apiKey: String, pts: List<LatLng>): List<LatLng>? {
    if (apiKey.isNotBlank()) {
        // Intenta Google Directions primero
        runCatching { fetchDirectionsPolyline(apiKey, pts) }.getOrNull()?.let { return it }
    }
    // Fallback OSRM
    return fetchDirectionsPolylineOsrm(pts)
}
