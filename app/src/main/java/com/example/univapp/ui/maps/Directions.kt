package com.example.univapp.ui.maps

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

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

/** Google Directions (si tienes API key). Devuelve polyline pegada a calle. */
suspend fun fetchDirectionsPolyline(
    apiKey: String,
    points: List<LatLng>
): List<LatLng>? = withContext(Dispatchers.IO) {
    if (apiKey.isBlank() || points.size < 2) return@withContext null

    val origin = "${points.first().latitude},${points.first().longitude}"
    val dest   = "${points.last().latitude},${points.last().longitude}"
    val middle = points.drop(1).dropLast(1)
    val waypoints = if (middle.isNotEmpty()) {
        "waypoints=" + middle.joinToString("|") { "via:${it.latitude},${it.longitude}" }
    } else null

    val urlStr = buildString {
        append("https://maps.googleapis.com/maps/api/directions/json?")
        append("origin=").append(URLEncoder.encode(origin, "UTF-8"))
        append("&destination=").append(URLEncoder.encode(dest, "UTF-8"))
        append("&mode=driving&units=metric&avoid=ferries")
        if (waypoints != null) append("&").append(waypoints)
        append("&key=").append(apiKey)
    }

    val conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
        connectTimeout = 10_000
        readTimeout = 10_000
    }

    return@withContext conn.inputStream.bufferedReader().use { br ->
        val json = JSONObject(br.readText())
        if (json.optString("status") != "OK") return@use null
        val routes = json.optJSONArray("routes") ?: return@use null
        if (routes.length() == 0) return@use null
        val overview = routes.getJSONObject(0)
            .getJSONObject("overview_polyline")
            .getString("points")
        decodePolyline(overview)
    }
}
