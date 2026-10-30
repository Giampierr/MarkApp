package com.marcapp.app.feature.attendance

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.marcapp.app.core.biometric.AndroidVerificadorHuella
import com.marcapp.app.core.biometric.EstadoHuella
import com.marcapp.app.core.biometric.ResultadoHuella
import com.marcapp.app.core.designsystem.MarcAppColors as C
import com.marcapp.app.domain.model.TipoMarca

/** La biometría local es requisito antes de solicitar la marcación. */
@Composable
fun FingerprintDialog(
    tipo: TipoMarca,
    onVerificado: () -> Unit,
    onCancelar: () -> Unit
) {
    val contexto = LocalContext.current
    // IMPORTANTE: usar contexto de la actividad, no applicationContext.
    val verificador = remember(contexto) { AndroidVerificadorHuella(contexto) }
    var estado by remember(verificador) { mutableStateOf(verificador.estado()) }
    val confirmarActual by rememberUpdatedState(onVerificado)
    var mensaje by remember { mutableStateOf("") }
    val ciclo = LocalLifecycleOwner.current

    DisposableEffect(ciclo, verificador) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) estado = verificador.estado()
        }
        ciclo.lifecycle.addObserver(observador)
        onDispose { ciclo.lifecycle.removeObserver(observador) }
    }

    DisposableEffect(verificador, estado) {
        val cancelar = if (estado == EstadoHuella.Lista) {
            verificador.verificar { resultado ->
                when (resultado) {
                    ResultadoHuella.Aprobada -> confirmarActual()
                    ResultadoHuella.NoReconocida ->
                        mensaje = "Huella no reconocida. Coloca nuevamente el dedo."
                    is ResultadoHuella.Mensaje -> mensaje = resultado.texto
                    is ResultadoHuella.Error -> mensaje = "Marcación bloqueada: ${resultado.texto}"
                }
            }
        } else null
        onDispose { cancelar?.invoke() }
    }

    val descripcion = when (estado) {
        EstadoHuella.Lista -> "Coloca tu dedo en el lector de huellas del teléfono (botón lateral o pantalla, según el modelo)."
        EstadoHuella.SinRegistrar -> "Configura una huella en los ajustes de seguridad de Android."
        EstadoHuella.SinSensor -> "Este celular no declara sensor de huella digital. No se puede marcar."
        EstadoHuella.NoDisponible -> "El sensor o la biometría fuerte no están disponibles ahora. " +
            "Comprueba la configuración de huellas en los ajustes de Android."
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(Icons.Default.Fingerprint, null, tint = C.VerdeAccion) },
        title = { Text("Verifica tu huella digital") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Acción: ${tipo.titulo}")
                Text(descripcion)
                if (mensaje.isNotEmpty()) Text(mensaje, color = C.Secundario)
                Text("En Redmi 13, el lector está en el botón lateral de encendido.", color = C.Secundario)
                Text("Android gestiona la huella; MarcApp no la almacena.", color = C.Secundario)
            }
        },
        confirmButton = {
            if (estado == EstadoHuella.SinRegistrar) {
                TextButton(onClick = {
                    runCatching {
                        contexto.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                    }.onFailure {
                        mensaje = "Abre Ajustes > Seguridad > Huellas digitales manualmente."
                    }
                }) { Text("Configurar huella") }
            } else {
                TextButton(onClick = onCancelar) { Text("Cerrar") }
            }
        },
        dismissButton = {
            if (estado == EstadoHuella.SinRegistrar) {
                TextButton(onClick = onCancelar) { Text("Cancelar") }
            }
        }
    )
}
