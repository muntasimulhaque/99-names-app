package io.github.muntasimulhaque.names99.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.muntasimulhaque.names99.data.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF1F6E5C),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEAE2),
    onPrimaryContainer = Color(0xFF11362D),
    secondary = Color(0xFF9A7D2E),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFFBF7F0),
    onBackground = Color(0xFF221F1A),
    surface = Color(0xFFFFFCF5),
    onSurface = Color(0xFF221F1A),
    surfaceVariant = Color(0xFFF0E9DC),
    onSurfaceVariant = Color(0xFF6A6459),
    outline = Color(0xFFB9B1A2)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8CC5B0),
    onPrimary = Color(0xFF0E2A22),
    primaryContainer = Color(0xFF1E463A),
    onPrimaryContainer = Color(0xFFD3E8DE),
    secondary = Color(0xFFD4B45A),
    onSecondary = Color(0xFF2A2107),
    background = Color(0xFF14120F),
    onBackground = Color(0xFFEAE3D6),
    surface = Color(0xFF1B1916),
    onSurface = Color(0xFFEAE3D6),
    surfaceVariant = Color(0xFF2A2721),
    onSurfaceVariant = Color(0xFFB5AC9C),
    outline = Color(0xFF57524A)
)

private val BlackColors = DarkColors.copy(
    background = Color(0xFF000000),
    surface = Color(0xFF0B0B0B),
    surfaceVariant = Color(0xFF161616)
)

@Composable
fun Names99Theme(mode: ThemeMode, content: @Composable () -> Unit) {
    val colors = when (mode) {
        ThemeMode.LIGHT -> LightColors
        ThemeMode.DARK -> DarkColors
        ThemeMode.BLACK -> BlackColors
        ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) DarkColors else LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
