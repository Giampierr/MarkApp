package com.marcapp.app.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcapp.app.BuildConfig
import com.marcapp.app.core.designsystem.MarcAppColors as C
import com.marcapp.app.core.ui.*

@Composable
private fun AdminColumna(contenido: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(C.Fondo).verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp), content = contenido)
}

@Composable
fun InicioAdministrador(vm: GestionViewModel, irAPersonal: () -> Unit) {
    val personal by vm.trabajadores.collectAsState()
    val solicitudes by vm.solicitudes.collectAsState()
    val bitacora by vm.auditoria.collectAsState()
    AdminColumna {
        TituloSeccion("Panel de administración", "Resumen visual · datos de demostración")
        Aviso("Este panel es una vista móvil demo. RF26–RF50 corresponden principalmente al panel WEB.")
        Tarjeta {
            Text("Visión general", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Dato("Colaboradores", "${personal.count { it.activo }}")
                Dato("Pendientes", "${solicitudes.count { it.estado == "Pendiente" }}")
                Dato("Cambios", "${bitacora.size}")
            }
            BotonPrincipal("Ver colaboradores", irAPersonal, Modifier.fillMaxWidth())
        }
        Tarjeta {
            Text("Acceso web", fontWeight = FontWeight.SemiBold)
            Text("El panel administrativo web se desarrolla por separado. Configura ADMIN_WEB_URL cuando tu equipo publique el sitio.",
                fontSize = 13.sp, color = C.Secundario)
            if (BuildConfig.ADMIN_WEB_URL.isBlank()) Aviso("URL del panel web pendiente.", true)
            else {
                val context = androidx.compose.ui.platform.LocalContext.current
                OutlinedButton(onClick = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse(BuildConfig.ADMIN_WEB_URL))
                    context.startActivity(intent)
                }) { Text("Abrir panel web") }
            }
        }
        TituloSeccion("Solicitudes recientes")
        solicitudes.take(3).forEach {
            Tarjeta {
                Text("${it.motivo} · ${it.usuario}", fontWeight = FontWeight.SemiBold)
                Text("${it.fecha} · ${it.estado}", color = C.Secundario, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PersonalScreen(vm: GestionViewModel) {
    val personal by vm.trabajadores.collectAsState()
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var buscar by rememberSaveable { mutableStateOf("") }
    var editarId by rememberSaveable { mutableIntStateOf(-1) }
    var nombreEditado by rememberSaveable { mutableStateOf("") }
    var cargoEditado by rememberSaveable { mutableStateOf("") }
    AdminColumna {
        TituloSeccion("Trabajadores", "RF28–RF30 · Gestión demo")
        Tarjeta {
            Text("Registrar colaborador", fontWeight = FontWeight.SemiBold)
            CampoTexto("Nombres y apellidos", nombre, { nombre = it })
            CampoTexto("Correo", correo, { correo = it })
            BotonPrincipal("Agregar trabajador", {
                if (nombre.isNotBlank() && correo.contains('@')) {
                    vm.repository.agregarTrabajador(nombre.trim(), correo.trim())
                    nombre = ""; correo = ""
                }
            }, Modifier.fillMaxWidth())
        }
        CampoTexto("Buscar colaborador", buscar, { buscar = it })
        personal.filter { it.nombre.contains(buscar, true) || it.correo.contains(buscar, true) }.forEach { persona ->
            Tarjeta {
                Text(persona.nombre, fontWeight = FontWeight.SemiBold)
                Text("${persona.cargo} · ${persona.correo}", color = C.Secundario, fontSize = 12.sp)
                if (editarId == persona.id) {
                    CampoTexto("Nombre", nombreEditado, { nombreEditado = it })
                    CampoTexto("Cargo", cargoEditado, { cargoEditado = it })
                    BotonPrincipal("Guardar edición", {
                        vm.repository.editarTrabajador(persona.id, nombreEditado, cargoEditado)
                        editarId = -1
                    }, Modifier.fillMaxWidth())
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    Text(if (persona.activo) "Activo" else "Inactivo", color = if (persona.activo) C.VerdeAccion else Color.Gray)
                    TextButton(onClick = {
                        editarId = persona.id; nombreEditado = persona.nombre; cargoEditado = persona.cargo
                    }) { Text("Editar") }
                    TextButton(onClick = { vm.repository.cambiarEstadoTrabajador(persona.id) }) {
                        Text(if (persona.activo) "Inactivar" else "Reactivar")
                    }
                }
            }
        }
        Aviso("Creación y activación simuladas. La edición de nombre/cargo es demo; persistencia real y validaciones completas deben integrarse con la API.")
    }
}

@Composable
fun GestionScreen(vm: GestionViewModel) {
    var categoria by rememberSaveable { mutableIntStateOf(0) }
    val titulos = listOf("Justificaciones", "Turnos", "Geocercas", "Feriados", "Auditoría", "Sistema")
    AdminColumna {
        TituloSeccion("Gestión", "Administración · vista demostrativa")
        // Categorías verticales: se mantienen dentro de la pestaña Gestion.
        titulos.chunked(3).forEachIndexed { chunkIndex, chunk ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                chunk.forEachIndexed { offset, titulo ->
                    val idx = chunkIndex * 3 + offset
                    FilterChip(selected = categoria == idx, onClick = { categoria = idx },
                        label = { Text(titulo, fontSize = 11.sp) }, modifier = Modifier.weight(1f))
                }
            }
        }
        when (categoria) {
            0 -> JustificacionesAdmin(vm)
            1 -> TurnosAdmin(vm)
            2 -> GeocercasAdmin(vm)
            3 -> FeriadosAdmin(vm)
            4 -> AuditoriaAdmin(vm)
            else -> SistemaAdmin(vm)
        }
    }
}

@Composable
private fun JustificacionesAdmin(vm: GestionViewModel) {
    val solicitudes by vm.solicitudes.collectAsState()
    TituloSeccion("Evaluar solicitudes", "RF33")
    solicitudes.forEach { s ->
        Tarjeta {
            Text("${s.usuario} · ${s.fecha}", fontWeight = FontWeight.SemiBold)
            Text("${s.motivo}: ${s.detalle}", fontSize = 13.sp)
            Text("Estado: ${s.estado}", color = C.Secundario, fontSize = 12.sp)
            if (s.estado == "Pendiente") Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.repository.responderJustificacion(s.id, false) }, modifier = Modifier.weight(1f)) { Text("Rechazar") }
                BotonPrincipal("Aprobar", { vm.repository.responderJustificacion(s.id, true) }, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TurnosAdmin(vm: GestionViewModel) {
    val turnos by vm.turnos.collectAsState()
    var nombre by rememberSaveable { mutableStateOf("") }
    var desde by rememberSaveable { mutableStateOf("08:00") }
    var hasta by rememberSaveable { mutableStateOf("17:00") }
    TituloSeccion("Horarios y turnos", "RF31–RF32 · Configuración demo")
    Tarjeta {
        CampoTexto("Nombre del turno", nombre, { nombre = it })
        CampoTexto("Hora de entrada (HH:mm)", desde, { desde = it })
        CampoTexto("Hora de salida (HH:mm)", hasta, { hasta = it })
        BotonPrincipal("Crear turno", {
            vm.repository.agregarTurno(nombre, desde, hasta)
            if (nombre.isNotBlank()) nombre = ""
        }, Modifier.fillMaxWidth())
    }
    turnos.forEach { turno -> Tarjeta { Text(turno.nombre, fontWeight = FontWeight.SemiBold); Text("${turno.inicio} – ${turno.fin}") } }
    Aviso("Asignación de turnos a trabajadores: pendiente de la API y el panel web.")
}

@Composable
private fun GeocercasAdmin(vm: GestionViewModel) {
    val tolerancia by vm.tolerancia.collectAsState()
    val radio by vm.radioGeocerca.collectAsState()
    var t by remember(tolerancia) { mutableStateOf(tolerancia.toString()) }
    var r by remember(radio) { mutableStateOf(radio.toString()) }
    TituloSeccion("Geocercas y políticas", "RF44 · RF47")
    Tarjeta {
        CampoTexto("Radio de sede (metros)", r, { r = it })
        CampoTexto("Tolerancia de tardanza (minutos)", t, { t = it })
        BotonPrincipal("Guardar políticas DEMO", {
            vm.repository.actualizarPolitica(t.toIntOrNull() ?: 10, r.toIntOrNull() ?: 150)
        }, Modifier.fillMaxWidth())
    }
    Aviso("No se puede cambiar la geocerca real ni la latitud/longitud del backend desde esta demo.")
}

@Composable
private fun FeriadosAdmin(vm: GestionViewModel) {
    val feriados by vm.feriados.collectAsState()
    var fecha by rememberSaveable { mutableStateOf("2026-12-25") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    TituloSeccion("Feriados", "RF50 · Calendario")
    Tarjeta {
        CampoTexto("Fecha AAAA-MM-DD", fecha, { fecha = it })
        CampoTexto("Descripción", descripcion, { descripcion = it })
        BotonPrincipal("Añadir feriado", { vm.repository.agregarFeriado(fecha, descripcion); descripcion = "" }, Modifier.fillMaxWidth())
    }
    feriados.forEach { Tarjeta { Text("${it.fecha} · ${it.descripcion}") } }
}

@Composable
private fun AuditoriaAdmin(vm: GestionViewModel) {
    val cambios by vm.auditoria.collectAsState()
    TituloSeccion("Bitácora", "RF45–RF46 · Auditoría de acciones demo")
    cambios.take(30).forEach { Tarjeta { Text(it.mensaje); Text(it.fecha, color = C.Secundario, fontSize = 12.sp) } }
    Aviso("Estos cambios viven en memoria. La auditoría confiable debe guardarse en el servidor.")
}

@Composable
private fun SistemaAdmin(vm: GestionViewModel) {
    TituloSeccion("Estado del sistema", "RF48–RF49 · Supervisión")
    Tarjeta {
        Text("Conexión al backend", fontWeight = FontWeight.SemiBold)
        Text(if (BuildConfig.API_BASE_URL.isBlank()) "API sin configurar" else "API configurada (disponibilidad no verificada)")
        Separador()
        Text("Respaldo automático", fontWeight = FontWeight.SemiBold)
        Text("Se implementa en infraestructura de PostgreSQL/Supabase, no en la APK.", color = C.Secundario, fontSize = 12.sp)
    }
    Aviso("No se muestran respaldos ni disponibilidad ficticia como si fueran reales.", true)
}

@Composable
fun ReportesScreen(vm: GestionViewModel) {
    val personas by vm.trabajadores.collectAsState()
    val solicitudes by vm.solicitudes.collectAsState()
    AdminColumna {
        TituloSeccion("Reportes", "Estadísticas y consolidado · demo")
        Tarjeta {
            Text("Vista preliminar", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Dato("Personal activo", "${personas.count { it.activo }}")
                Dato("Solicitudes", "${solicitudes.size}")
            }
            LinearProgressIndicator(progress = { personas.count { it.activo }.toFloat() / personas.size.coerceAtLeast(1) },
                modifier = Modifier.fillMaxWidth(), color = C.VerdeAccion)
            Text("Estadísticas del conjunto de prueba; no representan asistencia real.", color = C.Secundario, fontSize = 12.sp)
        }
        listOf(
            "RF35: Filtrar y buscar asistencias",
            "RF36: Horas efectivas trabajadas",
            "RF37: Minutos de tardanza",
            "RF38: Faltas justificadas/injustificadas",
            "RF39: Consolidado para planilla",
            "RF40: Exportar consolidado Excel (.xlsx)",
            "RF41: Exportar detalle diario Excel",
            "RF42: Enviar/descargar reportes por correo",
            "RF43: Gráficos estadísticos WEB",
            "RF61: Reporte diario automático"
        ).forEach { nombre ->
            Tarjeta {
                Text(nombre, fontWeight = FontWeight.Medium)
                Text("Pendiente de endpoints / servicio de reportes del backend.", color = C.Secundario, fontSize = 12.sp)
            }
        }
        Aviso("El Excel especifica principalmente reportes del panel web y trabajos programados. Esta APK no inventa exportaciones ni reportes oficiales.")
    }
}
