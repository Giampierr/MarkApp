package com.marcapp.app.domain.repository

import com.marcapp.app.domain.model.*
import kotlinx.coroutines.flow.StateFlow

sealed interface ResultadoLogin {
    data class Correcto(val usuario: Usuario) : ResultadoLogin
    data class Error(val mensaje: String) : ResultadoLogin
}

interface AuthRepository {
    suspend fun iniciar(correo: String, clave: String): ResultadoLogin
    fun demo(rol: Rol): Usuario
    fun cerrar() {}
}

interface AsistenciaRepository {
    val marcaciones: StateFlow<List<Marcacion>>
    suspend fun cargar(usuario: Usuario): String? = null
    suspend fun marcar(tipo: TipoMarca, usuario: Usuario, latitud: Double? = null, longitud: Double? = null): String
}

interface GestionRepository {
    val solicitudes: StateFlow<List<Justificacion>>
    val trabajadores: StateFlow<List<Trabajador>>
    val turnos: StateFlow<List<Turno>>
    val feriados: StateFlow<List<Feriado>>
    val auditoria: StateFlow<List<Auditoria>>
    val tolerancia: StateFlow<Int>
    val radioGeocerca: StateFlow<Int>
    fun enviarJustificacion(correo: String, fecha: String, motivo: String, detalle: String): String
    fun responderJustificacion(id: Int, aprobar: Boolean)
    fun agregarTrabajador(nombre: String, correo: String)
    fun editarTrabajador(id: Int, nombre: String, cargo: String)
    fun cambiarEstadoTrabajador(id: Int)
    fun agregarTurno(nombre: String, inicio: String, fin: String)
    fun agregarFeriado(fecha: String, descripcion: String)
    fun actualizarPolitica(tolerancia: Int, radio: Int)
}
