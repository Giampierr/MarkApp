package com.marcapp.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcapp.app.core.designsystem.*
import com.marcapp.app.core.ui.*
import com.marcapp.app.domain.model.Rol

@Composable
fun LoginScreen(
    estado: LoginUiState,
    onLogin: (String, String) -> Unit,
    onDemo: (Rol) -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var recordar by rememberSaveable { mutableStateOf(false) }
    var recuperar by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(MarcAppColors.Fondo).verticalScroll(rememberScrollState())) {
        Row(Modifier.fillMaxWidth().height(133.dp).background(MarcAppColors.VerdeOscuro)
            .padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
            // Lugar reservado para el logo definitivo de MarcApp.
            Box(Modifier.size(44.dp).background(MarcAppColors.VerdeAccion, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) {
                Text("M", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("MarcApp", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text("BuyPal Store", color = Color(0xFFD3E4DE), fontSize = 12.sp)
            }
        }
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
            TituloSeccion(if (recuperar) "Recuperar contraseña" else "Iniciar sesión",
                if (recuperar) "La recuperación estará disponible al integrar el servicio de correo." else "Marca tu asistencia de forma segura.")
            Spacer(Modifier.height(6.dp))
            CampoTexto("Correo electrónico", correo, { correo = it })
            if (!recuperar) {
                OutlinedTextField(value = clave, onValueChange = { clave = it },
                    label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MarcAppColors.VerdeAccion,
                        unfocusedBorderColor = MarcAppColors.Borde,
                        focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = recordar, onCheckedChange = { recordar = it }, enabled = false,
                        colors = CheckboxDefaults.colors(checkedColor = MarcAppColors.VerdeAccion))
                    Text("Recordarme (próximamente)", Modifier.weight(1f), fontSize = 12.sp, color = MarcAppColors.Secundario)
                    TextButton(onClick = { recuperar = true }) { Text("¿Olvidaste tu contraseña?", fontSize = 11.sp) }
                }
                if (estado.error != null) Aviso(estado.error, true)
                BotonPrincipal("Ingresar a MarcApp", { onLogin(correo, clave) },
                    Modifier.fillMaxWidth(), habilitado = !estado.cargando)
                if (estado.cargando) LinearProgressIndicator(Modifier.fillMaxWidth())
                Separador()
                Text("Acceso seguro para personal de BuyPal", Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center, fontSize = 12.sp, color = MarcAppColors.Secundario)
                Aviso("Para probar sin servidor, elige un perfil de demostración. Los datos no son reales.")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { onDemo(Rol.TRABAJADOR) }, modifier = Modifier.weight(1f)) {
                        Text("Demo trabajador", fontSize = 12.sp)
                    }
                    OutlinedButton(onClick = { onDemo(Rol.ADMINISTRADOR) }, modifier = Modifier.weight(1f)) {
                        Text("Demo admin", fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("¿Eres administrador? Usa el acceso web de tu organización.",
                    Modifier.fillMaxWidth(), color = MarcAppColors.VerdeOscuro,
                    fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, fontSize = 12.sp)
            } else {
                Aviso("RF03–RF04: el backend actual no ofrece endpoints de recuperación/restablecimiento.", true)
                OutlinedButton(onClick = { recuperar = false }) { Text("Volver al inicio de sesión") }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780, name = "MarcApp · Inicio de sesión")
@Composable
private fun LoginPreview() { MarcAppTheme { LoginScreen(LoginUiState(), { _, _ -> }, { }) } }
