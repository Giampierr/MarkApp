package com.marcapp.app.core.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.resume

data class Coordenadas(val latitud: Double, val longitud: Double)

/** Nueva lectura GPS en cada marcación; no reutiliza coordenadas antiguas. */
suspend fun obtenerGpsActual(context: Context): Coordenadas? {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) return null
    return withTimeout(25_000L) {
        suspendCancellableCoroutine { cont ->
            val cancelacion = CancellationTokenSource()
            try {
                @Suppress("MissingPermission")
                val tarea = LocationServices.getFusedLocationProviderClient(context)
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelacion.token)
                tarea.addOnSuccessListener { loc ->
                    if (cont.isActive) cont.resume(
                        if (loc == null || (loc.hasAccuracy() && loc.accuracy > 120f) || loc.isFromMockProvider)
                            null else Coordenadas(loc.latitude, loc.longitude))
                }.addOnFailureListener { if (cont.isActive) cont.resume(null) }
            } catch (_: SecurityException) {
                if (cont.isActive) cont.resume(null)
            }
            cont.invokeOnCancellation { cancelacion.cancel() }
        }
    }
}
