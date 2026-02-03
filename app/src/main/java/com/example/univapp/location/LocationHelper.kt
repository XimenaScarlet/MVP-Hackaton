package com.example.univapp.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fused by lazy { LocationServices.getFusedLocationProviderClient(context) }

    fun hasPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    /**
     * Ubicación **fresca** y de **alta precisión**:
     * 1) getCurrentLocation(HIGH_ACCURACY) (no usa caché)
     * 2) si falla, una sola actualización activa
     * 3) si falla, lastLocation como último recurso
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        if (!hasPermission()) return null

        // 1) Solicitud fresca (sin caché)
        val tokenSrc = CancellationTokenSource()
        val freshed = runCatching {
            fused.getCurrentLocation(
                CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    .build(),
                tokenSrc.token
            ).awaitNullable()
        }.getOrNull()
        if (freshed != null) return LatLng(freshed.latitude, freshed.longitude)

        // 2) Una sola actualización activa (8s)
        val single = runCatching { requestSingleUpdate(8000L) }.getOrNull()
        if (single != null) return LatLng(single.latitude, single.longitude)

        // 3) Fallback: última conocida (puede ser vieja)
        val last = runCatching { fused.lastLocation.awaitNullable() }.getOrNull()
        return last?.let { LatLng(it.latitude, it.longitude) }
    }

    @SuppressLint("MissingPermission")
    private suspend fun requestSingleUpdate(timeoutMs: Long): android.location.Location? =
        suspendCancellableCoroutine { cont ->
            val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L)
                .setWaitForAccurateLocation(true)
                .setMaxUpdates(1)
                .setDurationMillis(timeoutMs)
                .build()

            val cb = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fused.removeLocationUpdates(this)
                    if (!cont.isCompleted) cont.resume(result.lastLocation)
                }
            }
            fused.requestLocationUpdates(req, cb, android.os.Looper.getMainLooper())
            cont.invokeOnCancellation { fused.removeLocationUpdates(cb) }
        }
}

/* ---- helpers Task.awaitNullable ---- */
private suspend fun <T> Task<T>.awaitNullable(): T? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { cont.resume(null) }
        addOnCanceledListener { if (!cont.isCompleted) cont.resume(null) }
    }
