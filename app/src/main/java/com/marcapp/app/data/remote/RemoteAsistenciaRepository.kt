package com.marcapp.app.data.remote

import com.marcapp.app.data.demo.DemoAsistenciaRepository
import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.AsistenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.HttpException
import java.time.LocalDateTime

/** Nunca convierte un error de red en un falso "registrado". */
class RemoteAsistenciaRepository(private val api: BackendApi = MarcAppHttp.api) : AsistenciaRepository {
    private val demo = DemoAsistenciaRepository()
    private val eventos = MutableStateFlow<List<Marcacion>>(emptyList())
    override val marcaciones: StateFlow<List<Marcacion>> = eventos

    override suspend fun cargar(usuario: Usuario): String? {
        eventos.value = emptyList() // No mostrar historial de otro usuario.
        if (usuario.esDemo) {
            eventos.value = demo.marcaciones.value
            return null
        }
        val auth = SesionRemota.bearer() ?: return "Sesión no disponible. Ingresa de nuevo."
        return try {
            eventos.value = api.historial(auth, 7).mapNotNull { dato -> dato.aMarcacion() }
            null
        } catch (e: HttpException) {
            errorHttp(e)
        } catch (_: Exception) {
            "No se pudo cargar el historial desde Render."
        }
    }

    override suspend fun marcar(tipo: TipoMarca, usuario: Usuario, latitud: Double?, longitud: Double?): String {
        if (usuario.esDemo) {
            val mensaje = demo.marcar(tipo, usuario)
            eventos.value = demo.marcaciones.value
            return mensaje
        }
        val auth = SesionRemota.bearer() ?: return "Sesión expirada. Cierra sesión e ingresa nuevamente."
        if (latitud == null || longitud == null) return "Falta una ubicación GPS actual para registrar en el servidor."
        return try {
            val confirmada = api.marcar(auth, MarcaRequest(tipo.name, latitud, longitud))
            // Solo mostramos como real si el servidor confirmó la operación.
            cargar(usuario)
            "${tipo.titulo} guardada correctamente en Supabase (${confirmada.fechaHora.replace('T',' ')})."
        } catch (e: HttpException) {
            errorHttp(e)
        } catch (_: Exception) {
            "No se pudo confirmar la marcación. Comprueba tu historial antes de reintentar."
        }
    }

    private fun MarcaResponse.aMarcacion(): Marcacion? = try {
        Marcacion(TipoMarca.valueOf(tipo), LocalDateTime.parse(fechaHora), estado)
    } catch (_: Exception) { null }

    private fun errorHttp(e: HttpException): String = when (e.code()) {
        400 -> "Datos inválidos. Verifica tu ubicación GPS."
        401 -> "La sesión expiró (15 min). Cierra sesión e ingresa de nuevo."
        403 -> "Marcación denegada por el servidor: revisa el horario, geocerca o permisos."
        404 -> "Marcación no disponible o datos del colaborador/sucursal incompletos."
        409 -> "Marcación duplicada o fuera de orden. Consulta tu historial."
        502, 503, 504 -> "El servidor está despertando o temporalmente no disponible."
        else -> "El servidor rechazó la operación (HTTP ${e.code()})."
    }
}
