package com.example.univapp

import com.google.android.gms.maps.model.LatLng
import kotlin.math.*

/**
 * Utilidad optimizada para calcular la distancia entre dos puntos (Haversine) en metros.
 * Se utiliza kotlin.math para mejor rendimiento e idiomaticidad en Kotlin.
 */
fun LatLng.distanceToMeters(b: LatLng): Double {
    val r = 6371000.0 // Radio de la Tierra en metros

    val lat1Rad = latitude.toRadians()
    val lat2Rad = b.latitude.toRadians()
    val deltaLat = (b.latitude - latitude).toRadians()
    val deltaLng = (b.longitude - longitude).toRadians()

    // Optimizamos calculando el seno una sola vez
    val sinDeltaLat = sin(deltaLat / 2)
    val sinDeltaLng = sin(deltaLng / 2)

    val a = sinDeltaLat * sinDeltaLat +
            cos(lat1Rad) * cos(lat2Rad) *
            sinDeltaLng * sinDeltaLng

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return r * c
}

private fun Double.toRadians(): Double = Math.toRadians(this)
