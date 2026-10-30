package com.marcapp.app.data.demo

import com.marcapp.app.domain.model.*
import com.marcapp.app.domain.repository.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** All data is in-memory demonstration data: NOT persisted and NOT sent to backend. */
class DemoAsistenciaRepository : AsistenciaRepository {
    private val eventos = MutableStateFlow<List<Marcacion>>(emptyList())
    override val marcaciones: StateFlow<List<Marcacion>> = eventos

    suspend fun marcar(tipo: TipoMarca, usuario: Usuario): String = marcar(tipo, usuario, null, null)

    override suspend fun marcar(tipo: TipoMarca, usuario: Usuario, latitud: Double?, longitud: Double?): String {
        if (!usuario.esDemo) return "La API actual no devuelve idColaborador y aún no valida GPS. No se enviará esta marcación."
        val hoy = eventos.value.filter { it.fechaHora.toLocalDate() == LocalDate.now() }
        if (hoy.any { it.tipo == tipo }) return "Esta marcación ya se registró hoy (demo)."
        val tipos = hoy.map { it.tipo }.toSet()
        val permitido = when (tipo) {
            TipoMarca.ENTRADA -> tipos.isEmpty()
            TipoMarca.SALIDA_REFRIGERIO -> TipoMarca.ENTRADA in tipos && TipoMarca.SALIDA !in tipos
            TipoMarca.REGRESO_REFRIGERIO -> TipoMarca.SALIDA_REFRIGERIO in tipos && TipoMarca.SALIDA !in tipos
            TipoMarca.SALIDA -> TipoMarca.ENTRADA in tipos &&
                (TipoMarca.SALIDA_REFRIGERIO !in tipos || TipoMarca.REGRESO_REFRIGERIO in tipos)
        }
        if (!permitido) return "No puedes registrar ${tipo.titulo.lowercase()} en este momento. Revisa el orden de las marcaciones."
        eventos.value = listOf(Marcacion(tipo, LocalDateTime.now())) + eventos.value
        return "${tipo.titulo} registrada en modo DEMO. No se envió al servidor."
    }
}

class DemoGestionRepository : GestionRepository {
    private val items = MutableStateFlow(listOf(
        Justificacion(1, "maria@buypal.com.pe", "2026-10-20", "Tardanza", "Tráfico intenso"),
        Justificacion(2, "luis@buypal.com.pe", "2026-10-22", "Falta", "Cita médica", "Aprobada")
    ))
    override val solicitudes: StateFlow<List<Justificacion>> = items
    private val personas = MutableStateFlow(listOf(
        Trabajador(101, "María Torres", "Ventas", "maria@buypal.com.pe"),
        Trabajador(102, "Luis Gómez", "Almacén", "luis@buypal.com.pe"),
        Trabajador(103, "Ana Flores", "Caja", "ana@buypal.com.pe")
    ))
    override val trabajadores: StateFlow<List<Trabajador>> = personas
    private val horas = MutableStateFlow(listOf(Turno(1, "Jornada regular", "08:00", "17:00")))
    override val turnos: StateFlow<List<Turno>> = horas
    private val fechas = MutableStateFlow(listOf(Feriado(1, "2026-12-25", "Navidad")))
    override val feriados: StateFlow<List<Feriado>> = fechas
    private val bitacora = MutableStateFlow(listOf(Auditoria("Hoy", "Proyecto demo iniciado")))
    override val auditoria: StateFlow<List<Auditoria>> = bitacora
    private val politica = MutableStateFlow(10)
    override val tolerancia: StateFlow<Int> = politica
    private val radio = MutableStateFlow(150)
    override val radioGeocerca: StateFlow<Int> = radio

    private fun auditar(accion: String) {
        bitacora.value = listOf(Auditoria(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")), accion)) + bitacora.value
    }
    override fun enviarJustificacion(correo: String, fecha: String, motivo: String, detalle: String): String {
        if (fecha.isBlank() || detalle.isBlank()) return "Completa la fecha y el sustento."
        items.value = listOf(Justificacion((items.value.maxOfOrNull { it.id } ?: 0) + 1,
            correo, fecha, motivo, detalle)) + items.value
        auditar("Justificación de $correo creada (DEMO)")
        return "Solicitud enviada en modo demo (sin servidor)."
    }
    override fun responderJustificacion(id: Int, aprobar: Boolean) {
        items.value = items.value.map { if (it.id == id) it.copy(estado = if (aprobar) "Aprobada" else "Rechazada") else it }
        auditar("Justificación #$id: ${if (aprobar) "aprobada" else "rechazada"} (DEMO)")
    }
    override fun agregarTrabajador(nombre: String, correo: String) {
        if (nombre.isBlank() || !correo.contains('@')) return
        personas.value = personas.value + Trabajador((personas.value.maxOfOrNull { it.id } ?: 100) + 1, nombre, "Por asignar", correo)
        auditar("Colaborador $nombre agregado (DEMO)")
    }
    override fun editarTrabajador(id: Int, nombre: String, cargo: String) {
        if (nombre.isBlank() || cargo.isBlank()) return
        personas.value = personas.value.map { if (it.id == id) it.copy(nombre = nombre, cargo = cargo) else it }
        auditar("Colaborador #$id editado (DEMO)")
    }
    override fun cambiarEstadoTrabajador(id: Int) {
        personas.value = personas.value.map { if (it.id == id) it.copy(activo = !it.activo) else it }
        auditar("Cambio de estado de colaborador #$id (DEMO)")
    }
    override fun agregarTurno(nombre: String, inicio: String, fin: String) {
        if (nombre.isBlank() || inicio.isBlank() || fin.isBlank()) return
        horas.value = horas.value + Turno((horas.value.maxOfOrNull { it.id } ?: 0) + 1, nombre, inicio, fin)
        auditar("Turno $nombre agregado (DEMO)")
    }
    override fun agregarFeriado(fecha: String, descripcion: String) {
        if (fecha.isBlank() || descripcion.isBlank()) return
        fechas.value = fechas.value + Feriado((fechas.value.maxOfOrNull { it.id } ?: 0) + 1, fecha, descripcion)
        auditar("Feriado $descripcion agregado (DEMO)")
    }
    override fun actualizarPolitica(tolerancia: Int, radio: Int) {
        politica.value = tolerancia.coerceIn(0,60)
        this.radio.value = radio.coerceIn(50,2000)
        auditar("Políticas cambiadas (DEMO)")
    }
}
