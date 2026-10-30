package com.marcapp.app.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val scheme = lightColorScheme(
    primary = MarcAppColors.VerdeOscuro,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4F4EA),
    secondary = MarcAppColors.VerdeAccion,
    onSecondary = Color.White,
    background = MarcAppColors.Fondo,
    onBackground = MarcAppColors.Texto,
    surface = Color.White,
    onSurface = MarcAppColors.Texto,
    outline = MarcAppColors.Borde
)

@Composable
fun MarcAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
