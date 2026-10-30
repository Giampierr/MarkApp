package com.marcapp.app.domain.model

import java.time.LocalDateTime

enum class Rol { TRABAJADOR, ADMINISTRADOR }

data class Usuario(
    val nombre: String,
    val correo: String,
    val rol: Rol,
    val idColaborador: Int? = null,
    val esDemo: Boolean = true
)

enum class TipoMarca(val titulo: String, val detalle: String) {
    ENTRADA("Entrada", "Inicio de jornada"),
    SALIDA_REFRIGERIO("Inicio de refrigerio", "Pausa de alimentación"),
    REGRESO_REFRIGERIO("Retorno de refrigerio", "Regreso al trabajo"),
    SALIDA("Salida", "Fin de jornada")
}

data class Marcacion(val tipo: TipoMarca, val fechaHora: LocalDateTime, val estado: String = "Registrado · DEMO")

data class Justificacion(
    val id: Int,
    val usuario: String,
    val fecha: String,
    val motivo: String,
    val detalle: String,
    val estado: String = "Pendiente"
)

data class Trabajador(val id: Int, val nombre: String, val cargo: String, val correo: String, val activo: Boolean = true)

data class Turno(val id: Int, val nombre: String, val inicio: String, val fin: String)

data class Feriado(val id: Int, val fecha: String, val descripcion: String)

data class Auditoria(val fecha: String, val mensaje: String)
