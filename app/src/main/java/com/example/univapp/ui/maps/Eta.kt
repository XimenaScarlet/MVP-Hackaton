package com.example.univapp.ui.maps

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class EtaResult(
    val durationSeconds: Long,
    val distanceMeters: Long
)

/**
 * ETA con OSRM pública (sin API key).
 * origin y dest en WGS84. Devuelve null si falla.
 */
suspend fun fetchEtaOsrm(origin: LatLng, dest: LatLng): EtaResult? =
    withContext(Dispatchers.IO) {
        // OSRM espera LON,LAT;LON,LAT
        val coords = "${origin.longitude},${origin.latitude};${dest.longitude},${dest.latitude}"
        val url = "https://router.project-osrm.org/route/v1/driving/$coords?overview=false"

        runCatching {
            val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
            }
            conn.inputStream.bufferedReader().use { br ->
                val json = JSONObject(br.readText())
                if (json.optString("code") != "Ok") return@use null
                val route0 = json.getJSONArray("routes").optJSONObject(0) ?: return@use null
                val duration = route0.optDouble("duration", -1.0)
                val distance = route0.optDouble("distance", -1.0)
                if (duration < 0 || distance < 0) null
                else EtaResult(durationSeconds = duration.toLong(), distanceMeters = distance.toLong())
            }
        }.getOrNull()
    }
