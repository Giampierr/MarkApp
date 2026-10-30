package com.marcapp.app.core.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.marcapp.app.core.ui.*

/** Shows real device location; DOES NOT certify proximity or transmit GPS to Spring Boot. */
@Composable
fun GpsCard() {
    val context = LocalContext.current
    var ubicacion by remember { mutableStateOf("Aún no consultada") }
    fun permitido(): Boolean = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    fun consultar() {
        if (!permitido()) { ubicacion = "Debes conceder acceso a ubicación."; return }
        try {
            val client = LocationServices.getFusedLocationProviderClient(context)
            @Suppress("MissingPermission")
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    ubicacion = if (loc == null) "Sin ubicación disponible. Activa GPS y vuelve a intentar."
                    else "Lat ${"%.5f".format(loc.latitude)}, Lon ${"%.5f".format(loc.longitude)}"
                }
                .addOnFailureListener { ubicacion = "No se pudo obtener GPS." }
        } catch (_: SecurityException) { ubicacion = "Permiso GPS denegado." }
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (permitido()) consultar() else ubicacion = "GPS denegado. Activa el permiso en Ajustes."
    }
    Tarjeta {
        Text("Ubicación del dispositivo", style = MaterialTheme.typography.titleMedium)
        Text(ubicacion, style = MaterialTheme.typography.bodySmall)
        OutlinedButton(onClick = {
            if (permitido()) consultar() else launcher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }) { Text("Consultar GPS") }
        Aviso("La geocerca de producción debe ser verificada por el servidor. En DEMO no se valida ni se envía la ubicación.")
    }
}
