package com.marcapp.app.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcapp.app.core.designsystem.MarcAppColors as C

@Composable
fun TituloSeccion(titulo: String, detalle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(titulo, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = C.Texto)
        if (detalle != null) Text(detalle, fontSize = 13.sp, color = C.Secundario)
    }
}

@Composable
fun Tarjeta(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(Modifier.padding(17.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = contenido)
    }
}

@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    Button(onClick = onClick, modifier = modifier.height(52.dp), enabled = habilitado,
        colors = ButtonDefaults.buttonColors(containerColor = C.VerdeAccion, contentColor = Color.White),
        shape = RoundedCornerShape(11.dp)) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun Dato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, color = C.Secundario, fontSize = 12.sp)
        Text(valor, color = C.Texto, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun Aviso(texto: String, esAlerta: Boolean = false) {
    Surface(color = if (esAlerta) Color(0xFFFFF4E5) else Color(0xFFE8F8F2),
        shape = RoundedCornerShape(12.dp)) {
        Text(texto, Modifier.fillMaxWidth().padding(13.dp),
            color = if (esAlerta) Color(0xFF8A5A17) else C.VerdeOscuro, fontSize = 12.sp)
    }
}

@Composable
fun CampoTexto(etiqueta: String, valor: String, cambiar: (String) -> Unit,
               modifier: Modifier = Modifier, unaLinea: Boolean = true) {
    OutlinedTextField(value = valor, onValueChange = cambiar, label = { Text(etiqueta) },
        modifier = modifier.fillMaxWidth(), singleLine = unaLinea,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = C.VerdeAccion, unfocusedBorderColor = C.Borde,
            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
        ))
}

@Composable
fun Separador() { HorizontalDivider(color = C.Borde) }

@Composable
fun PantallaScroll(contenido: @Composable ColumnScope.() -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxSize()
            .background(C.Fondo)
            .then(Modifier)
            .padding(horizontal = 19.dp, vertical = 20.dp)
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = contenido
    )
}
