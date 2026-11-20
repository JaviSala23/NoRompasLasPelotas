package io.github.javisala23.norompaslaspelotas.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorDarkSage = Color(0xFF394737)

private val LightColors = lightColorScheme(
    primary = Salvia,
    onPrimary = ArenaClara,
    primaryContainer = SalviaSuave,
    onPrimaryContainer = Grafito,
    background = ArenaClara,
    onBackground = Grafito,
    surface = ArenaClara,
    onSurface = Grafito,
    surfaceVariant = ArenaSuave,
    onSurfaceVariant = GrisPiedra,
    outline = GrisPiedra,
)

private val DarkColors = darkColorScheme(
    primary = SalviaNoche,
    onPrimary = Noche,
    primaryContainer = ColorDarkSage,
    onPrimaryContainer = ArenaNoche,
    background = Noche,
    onBackground = ArenaNoche,
    surface = Noche,
    onSurface = ArenaNoche,
    surfaceVariant = NocheElevada,
    onSurfaceVariant = GrisNoche,
    outline = GrisNoche,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
