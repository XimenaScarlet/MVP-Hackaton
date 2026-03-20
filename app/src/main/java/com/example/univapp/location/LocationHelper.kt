package com.example.univapp.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class LocationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "LocationHelper_DEBUG"
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

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        if (!hasPermission()) {
            Log.e(TAG, "No hay permisos de ubicación")
            return null
        }

        // Intento 1: Current Location (Google Play Services)
        Log.d(TAG, "Intento 1: Obteniendo getCurrentLocation...")
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
        
        if (freshed != null) {
            Log.d(TAG, "Ubicación obtenida vía getCurrentLocation: ${freshed.latitude}, ${freshed.longitude}")
            return LatLng(freshed.latitude, freshed.longitude)
        }

        // Intento 2: Single Update Request (Fallback)
        Log.d(TAG, "Intento 2: Solicitando actualización única (8s timeout)...")
        val single = runCatching { requestSingleUpdate(8000L) }.getOrNull()
        if (single != null) {
            Log.d(TAG, "Ubicación obtenida vía SingleUpdate: ${single.latitude}, ${single.longitude}")
            return LatLng(single.latitude, single.longitude)
        }

        // Intento 3: Last Location (Último recurso)
        Log.d(TAG, "Intento 3: Consultando LastLocation...")
        val last = runCatching { fused.lastLocation.awaitNullable() }.getOrNull()
        if (last != null) {
            Log.d(TAG, "Ubicación obtenida vía LastLocation: ${last.latitude}, ${last.longitude}")
            return LatLng(last.latitude, last.longitude)
        }

        Log.e(TAG, "Todos los mecanismos de ubicación fallaron")
        return null
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
                    if (!cont.isCompleted) {
                        Log.d(TAG, "LocationCallback: Ubicación recibida")
                        cont.resume(result.lastLocation)
                    }
                }
                override fun onLocationAvailability(availability: LocationAvailability) {
                    if (!availability.isLocationAvailable) {
                        Log.w(TAG, "LocationCallback: Ubicación no disponible actualmente")
                    }
                }
            }
            fused.requestLocationUpdates(req, cb, android.os.Looper.getMainLooper())
            cont.invokeOnCancellation { fused.removeLocationUpdates(cb) }
        }
}

private suspend fun <T> Task<T>.awaitNullable(): T? =
    suspendCancellableCoroutine { cont ->
        addOnSuccessListener { cont.resume(it) }
        addOnFailureListener { 
            Log.e("LocationHelper_DEBUG", "Task falló: ${it.message}")
            cont.resume(null) 
        }
        addOnCanceledListener { if (!cont.isCompleted) cont.resume(null) }
    }
