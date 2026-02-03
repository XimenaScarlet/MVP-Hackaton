package com.example.univapp

import com.google.android.gms.maps.model.LatLng

/** Util simple de distancia Haversine (metros). */
fun LatLng.distanceToMeters(b: LatLng): Double {
    val R = 6371000.0
    val dLat = Math.toRadians(b.latitude - latitude)
    val dLng = Math.toRadians(b.longitude - longitude)
    val s1 = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(latitude)) *
            Math.cos(Math.toRadians(b.latitude)) *
            Math.sin(dLng / 2) * Math.sin(dLng / 2)
    return 2 * R * Math.asin(Math.sqrt(s1))
}
