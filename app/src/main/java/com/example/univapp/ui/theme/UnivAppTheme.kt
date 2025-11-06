package com.example.univapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Verdes suaves (no chillones)
private val GreenPrimary = Color(0xFF2E7D32)      // verde sobrio
private val GreenPrimaryContainer = Color(0xFFA5D6A7) // verde claro contenedor
private val GreenSecondary = Color(0xFF388E3C)
private val GreenTertiary = Color(0xFF1B5E20)

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = Color(0xFF0A2E0E),
    secondary = GreenSecondary,
    onSecondary = Color.White,
    tertiary = GreenTertiary,
    background = Color(0xFFF8FAF8),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF245D27),
    onPrimaryContainer = Color(0xFFCCF0D0),
    secondary = GreenSecondary,
    onSecondary = Color.White,
    background = Color(0xFF111311),
    surface = Color(0xFF141614)
)

@Composable
fun UnivAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = Typography(), content = content)
}
