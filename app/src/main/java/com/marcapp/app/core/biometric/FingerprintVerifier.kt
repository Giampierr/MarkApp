package com.marcapp.app.core.biometric

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Verificación para marcaciones MarcApp (modo DEMO).
 *
 * Comprueba que el dispositivo declare sensor de huellas y exige autenticación
 * biométrica FUERTE (Class 3). No ofrece PIN/patrón/contraseña como sustituto.
 *
 * IMPORTANTE: Android BiometricPrompt NO permite seleccionar por API pública
 * únicamente el sensor de huellas. Un equipo con rostro FUERTE podría ofrecerlo.
 * En Redmi 13, Xiaomi documenta lector lateral y desbloqueo facial por IA.
 * No equivale a verificar la identidad del trabajador en el servidor.
 */
sealed interface EstadoHuella {
    data object Lista : EstadoHuella
    data object SinSensor : EstadoHuella
    data object SinRegistrar : EstadoHuella
    data object NoDisponible : EstadoHuella
}

sealed interface ResultadoHuella {
    data object Aprobada : ResultadoHuella
    data object NoReconocida : ResultadoHuella
    data class Mensaje(val texto: String) : ResultadoHuella
    data class Error(val texto: String) : ResultadoHuella
}

interface VerificadorHuella {
    fun estado(): EstadoHuella
    /** Retorna una función para cancelar el cuadro biométrico. */
    fun verificar(onResultado: (ResultadoHuella) -> Unit): () -> Unit
}

class AndroidVerificadorHuella(private val contexto: Context) : VerificadorHuella {

    private fun actividadActual(): FragmentActivity? {
        var actual: Context = contexto
        while (true) {
            if (actual is FragmentActivity) return actual
            if (actual !is ContextWrapper) return null
            val siguiente = actual.baseContext
            if (siguiente === actual) return null
            actual = siguiente
        }
    }

    override fun estado(): EstadoHuella {
        // El Redmi 13 sí dispone de lector lateral. No bloquear por la presencia
        // de desbloqueo facial básico: únicamente aceptamos biometría FUERTE.
        if (!contexto.packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            return EstadoHuella.SinSensor
        }
        if (actividadActual() == null) return EstadoHuella.NoDisponible
        return when (
            BiometricManager.from(contexto).canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            )
        ) {
            BiometricManager.BIOMETRIC_SUCCESS -> EstadoHuella.Lista
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> EstadoHuella.SinRegistrar
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> EstadoHuella.SinSensor
            else -> EstadoHuella.NoDisponible
        }
    }

    override fun verificar(onResultado: (ResultadoHuella) -> Unit): () -> Unit {
        val actividad = actividadActual()
        if (actividad == null || estado() != EstadoHuella.Lista) {
            onResultado(ResultadoHuella.Error(
                "Necesitas un sensor de huellas y una biometría fuerte registrada para continuar."
            ))
            return {}
        }

        val activa = AtomicBoolean(true)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (activa.compareAndSet(true, false)) onResultado(ResultadoHuella.Aprobada)
            }

            override fun onAuthenticationFailed() {
                if (activa.get()) onResultado(ResultadoHuella.NoReconocida)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                if (activa.compareAndSet(true, false)) {
                    onResultado(ResultadoHuella.Error(errString.toString()))
                }
            }
        }

        val prompt = BiometricPrompt(
            actividad,
            ContextCompat.getMainExecutor(actividad),
            callback
        )
        val opciones = BiometricPrompt.PromptInfo.Builder()
            .setTitle("MarcApp · Confirma tu huella")
            .setSubtitle("Verificación obligatoria para marcar asistencia")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setNegativeButtonText("Cancelar")
            .build()

        try {
            prompt.authenticate(opciones)
        } catch (error: Exception) {
            if (activa.compareAndSet(true, false)) {
                onResultado(ResultadoHuella.Error(
                    "No se pudo abrir la verificación biométrica: ${error.message ?: "sin detalles"}"
                ))
            }
        }
        return {
            if (activa.compareAndSet(true, false)) prompt.cancelAuthentication()
        }
    }
}
