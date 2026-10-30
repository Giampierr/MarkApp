package com.marcapp.app.feature.attendance

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcapp.app.BuildConfig
import com.marcapp.app.core.designsystem.MarcAppColors as C
import com.marcapp.app.core.location.GpsCard
import com.marcapp.app.core.location.Coordenadas
import com.marcapp.app.core.location.obtenerGpsActual
import kotlinx.coroutines.launch
import com.marcapp.app.core.ui.*
import com.marcapp.app.domain.model.*
import com.marcapp.app.feature.admin.GestionViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val horaFmt = DateTimeFormatter.ofPattern("hh:mm a")
private val fechaFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
private fun ColumnaPantalla(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(C.Fondo).verticalScroll(rememberScrollState())
        .padding(horizontal = 18.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
}

@Composable
fun InicioTrabajador(usuario: Usuario, vm: AttendanceViewModel, irAMarcar: () -> Unit) {
    val marcas by vm.marcaciones.collectAsState()
    val hoy = marcas.filter { it.fechaHora.toLocalDate() == LocalDate.now() }
    ColumnaPantalla {
        TituloSeccion("Tu jornada de hoy", LocalDate.now().format(fechaFmt))
        if (usuario.esDemo) Aviso("Modo demostración: las marcaciones se guardan solo en memoria.")
        Tarjeta {
            Text("Buen día, ${usuario.nombre}", fontSize = 19.sp, color = C.VerdeOscuro, fontWeight = FontWeight.Bold)
            Text("Control de asistencia personal", color = C.Secundario, fontSize = 12.sp)
            Separador()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Dato("Marcaciones hoy", "${hoy.size} / 4")
                Dato("Estado", if (hoy.any { it.tipo == TipoMarca.SALIDA }) "Finalizada"
                     else if (hoy.any { it.tipo == TipoMarca.ENTRADA }) "En jornada" else "Por iniciar")
            }
            BotonPrincipal("Registrar una marcación", irAMarcar, Modifier.fillMaxWidth())
        }
        TituloSeccion("Resumen del día")
        TipoMarca.entries.forEach { tipo ->
            val registro = hoy.firstOrNull { it.tipo == tipo }
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (registro != null) Icons.Default.CheckCircle else Icons.Default.Schedule,
                        null, tint = if (registro != null) C.VerdeAccion else C.Secundario)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tipo.titulo, fontWeight = FontWeight.SemiBold)
                        Text(tipo.detalle, fontSize = 12.sp, color = C.Secundario)
                    }
                    Text(registro?.fechaHora?.format(horaFmt) ?: "Pendiente", fontSize = 12.sp,
                        color = if (registro == null) C.Secundario else C.VerdeOscuro)
                }
            }
        }
        Aviso("En sesión real, el servidor valida horarios, jornada y geocerca antes de guardar.")
    }
}

@Composable
fun MarcarScreen(usuario: Usuario, vm: AttendanceViewModel) {
    val marcas by vm.marcaciones.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val enProceso by vm.enProceso.collectAsState()
    val hoy = marcas.filter { it.fechaHora.toLocalDate() == LocalDate.now() }
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()
    var tipoPendiente by remember { mutableStateOf<TipoMarca?>(null) }
    var solicitado by remember { mutableStateOf<TipoMarca?>(null) }
    var gpsPendiente by remember { mutableStateOf<Coordenadas?>(null) }
    var consultandoGps by remember { mutableStateOf(false) }
    var errorGps by remember { mutableStateOf<String?>(null) }

    val preparar: (TipoMarca) -> Unit = { tipo ->
        if (usuario.esDemo) {
            gpsPendiente = null
            tipoPendiente = tipo
        } else {
            scope.launch {
                consultandoGps = true
                errorGps = null
                try {
                    val coords = obtenerGpsActual(contexto)
                    if (coords == null) {
                        errorGps = "No se pudo obtener un GPS preciso (máx. 120 m). Activa la ubicación precisa y prueba al aire libre."
                    } else {
                        gpsPendiente = coords
                        tipoPendiente = tipo // Huella DESPUÉS de obtener GPS.
                    }
                } catch (_: Exception) {
                    errorGps = "El GPS no respondió. Actívalo y vuelve a intentarlo."
                } finally {
                    consultandoGps = false
                }
            }
        }
    }
    val permisoGps = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) solicitado?.let { preparar(it) }
        else errorGps = "Debes conceder ubicación precisa para registrar en Supabase."
        solicitado = null
    }

    ColumnaPantalla {
        TituloSeccion("Marcar asistencia", "Registra tu jornada con huella y GPS")
        if (usuario.esDemo) Aviso("DEMO: la huella se exige, pero no se guarda en Supabase.")
        else Aviso("Marcación REAL: se verifica tu huella y se consulta el GPS actual. El servidor aplica horario y geocerca.")
        GpsCard()
        errorGps?.let { Aviso(it, true) }
        if (consultandoGps || enProceso) LinearProgressIndicator(Modifier.fillMaxWidth())
        TituloSeccion("Acciones de marcación")
        TipoMarca.entries.forEach { tipo ->
            val ya = hoy.any { it.tipo == tipo }
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (ya) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                        null, tint = if (ya) C.VerdeAccion else C.VerdeOscuro)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(tipo.titulo, fontWeight = FontWeight.SemiBold)
                        Text(tipo.detalle, color = C.Secundario, fontSize = 12.sp)
                    }
                }
                BotonPrincipal(if (ya) "Registrado" else "Registrar ${tipo.titulo.lowercase()}",
                    {
                        if (!usuario.esDemo && ContextCompat.checkSelfPermission(contexto,
                                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            solicitado = tipo
                            permisoGps.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else preparar(tipo)
                    }, Modifier.fillMaxWidth(), habilitado = !ya && !consultandoGps && !enProceso)
            }
        }
        if (mensaje != null) AlertDialog(onDismissRequest = vm::limpiarMensaje,
            title = { Text("MarcApp") }, text = { Text(mensaje ?: "") },
            confirmButton = { TextButton(onClick = vm::limpiarMensaje) { Text("Entendido") } })
    }
    val pendiente = tipoPendiente
    if (pendiente != null) {
        FingerprintDialog(tipo = pendiente,
            onVerificado = {
                tipoPendiente = null
                vm.marcar(pendiente, usuario, gpsPendiente?.latitud, gpsPendiente?.longitud)
                gpsPendiente = null
            },
            onCancelar = { tipoPendiente = null; gpsPendiente = null })
    }
}

@Composable
fun HistorialScreen(vm: AttendanceViewModel) {
    val marcas by vm.marcaciones.collectAsState()
    var filtro by rememberSaveable { mutableIntStateOf(0) }
    val hoy = LocalDate.now()
    val mostradas = marcas.filter { it.fechaHora.toLocalDate() == hoy || (filtro == 1 && !it.fechaHora.toLocalDate().isBefore(hoy.minusDays(6))) }
    ColumnaPantalla {
        TituloSeccion("Mi historial", "Consulta tus registros diarios y semanales")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(selected = filtro == 0, onClick = { filtro = 0 }, label = { Text("Hoy") })
            FilterChip(selected = filtro == 1, onClick = { filtro = 1 }, label = { Text("Esta semana") })
        }
        if (mostradas.isEmpty()) {
            Tarjeta {
                Text("Todavía no hay marcaciones", fontWeight = FontWeight.SemiBold)
                Text("Usa la pestaña Marcar para generar registros.", color = C.Secundario)
            }
        } else mostradas.sortedByDescending { it.fechaHora }.forEach { marca ->
            Tarjeta {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = C.VerdeAccion)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(marca.tipo.titulo, fontWeight = FontWeight.SemiBold)
                        Text(marca.fechaHora.format(fechaFmt), color = C.Secundario, fontSize = 12.sp)
                    }
                    Text(marca.fechaHora.format(horaFmt), color = C.VerdeOscuro)
                }
                Text(marca.estado, fontSize = 11.sp, color = C.Secundario)
            }
        }
        Aviso("En sesión real se consulta el historial de los últimos 7 días desde Supabase. En DEMO es local.")
    }
}

@Composable
fun SolicitudesScreen(usuario: Usuario, vm: GestionViewModel) {
    var fecha by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var motivo by rememberSaveable { mutableStateOf("Tardanza") }
    var detalle by rememberSaveable { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    val solicitudes by vm.solicitudes.collectAsState()
    ColumnaPantalla {
        TituloSeccion("Justificaciones", "Solicitudes de tardanza e inasistencia")
        Tarjeta {
            Text("Nueva solicitud", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            CampoTexto("Fecha (AAAA-MM-DD)", fecha, { fecha = it })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = motivo == "Tardanza", onClick = { motivo = "Tardanza" }, label = { Text("Tardanza") })
                FilterChip(selected = motivo == "Falta", onClick = { motivo = "Falta" }, label = { Text("Falta") })
            }
            CampoTexto("Motivo / sustento", detalle, { detalle = it }, unaLinea = false)
            BotonPrincipal("Enviar justificación", {
                mensaje = vm.repository.enviarJustificacion(usuario.correo, fecha, motivo, detalle)
                if (mensaje?.startsWith("Solicitud enviada") == true) detalle = ""
            }, Modifier.fillMaxWidth(), habilitado = usuario.esDemo)
        }
        mensaje?.let { Aviso(it, it.startsWith("Completa")) }
        TituloSeccion("Mis solicitudes")
        val mias = solicitudes.filter { it.usuario.equals(usuario.correo, ignoreCase = true) }
        if (mias.isEmpty()) Tarjeta { Text("Aún no tienes solicitudes registradas.") }
        mias.forEach {
            Tarjeta {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${it.motivo} · ${it.fecha}", fontWeight = FontWeight.SemiBold)
                    Text(it.estado, color = if (it.estado == "Aprobada") C.VerdeAccion else C.Secundario, fontSize = 12.sp)
                }
                Text(it.detalle, fontSize = 12.sp, color = C.Secundario)
            }
        }
        Aviso("El archivo adjunto, la revisión real y las notificaciones dependen de APIs pendientes.")
    }
}

@Composable
fun PerfilScreen(usuario: Usuario, salir: () -> Unit) {
    ColumnaPantalla {
        TituloSeccion("Mi perfil", "Configuración y datos de cuenta")
        Tarjeta {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(56.dp).background(C.VerdeAccion, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center) {
                    Text(usuario.nombre.take(1), color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(usuario.nombre, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(if (usuario.rol == Rol.TRABAJADOR) "Colaborador" else "Administrador", color = C.Secundario)
                }
            }
            Separador()
            Dato("Correo", usuario.correo)
            Dato("Modo", if (usuario.esDemo) "Demostración sin servidor" else "Sesión real · Render y Supabase")
            Dato("Dispositivo", "Vinculación pendiente de API")
        }
        Tarjeta {
            Text("Preferencias", fontWeight = FontWeight.SemiBold)
            Text("Logo definitivo: pendiente de proporcionar.", fontSize = 12.sp, color = C.Secundario)
            Text("Notificaciones y recuperación de contraseña: pendientes de integrar.", fontSize = 12.sp, color = C.Secundario)
        }
        BotonPrincipal("Cerrar sesión", salir, Modifier.fillMaxWidth())
        Aviso(if (usuario.esDemo) "Modo DEMO: no registra asistencia real." else "Asistencia e historial conectados. Gestión, justificaciones y reportes siguen pendientes de integrar.")
    }
}
