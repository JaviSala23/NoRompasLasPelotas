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
    outlineVariant = Color(0xFFD2CBBC),
    secondary = Color(0xFF6B6452),
    onSecondary = ArenaClara,
    secondaryContainer = Color(0xFFECE2CC),
    onSecondaryContainer = Color(0xFF2D2A20),
    surfaceTint = Salvia,
    surfaceContainerLowest = Color(0xFFFBF8F1),
    surfaceContainerLow = Color(0xFFEFEADF),
    surfaceContainer = Color(0xFFEAE4D8),
    surfaceContainerHigh = Color(0xFFE4DDCF),
    surfaceContainerHighest = Color(0xFFDED6C7),
    error = Color(0xFFA3402A),
    errorContainer = Color(0xFFF6DDD5),
    onErrorContainer = Color(0xFF5C1F12),
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
    outlineVariant = Color(0xFF44483F),
    secondary = Color(0xFFD3C9B2),
    onSecondary = Noche,
    secondaryContainer = Color(0xFF4A4536),
    onSecondaryContainer = Color(0xFFECE2CC),
    surfaceTint = SalviaNoche,
    surfaceContainerLowest = Color(0xFF151713),
    surfaceContainerLow = Color(0xFF20221E),
    surfaceContainer = NocheElevada,
    surfaceContainerHigh = Color(0xFF2E322B),
    surfaceContainerHighest = Color(0xFF383C34),
    error = Color(0xFFFFB4A1),
    errorContainer = Color(0xFF6E2A1C),
    onErrorContainer = Color(0xFFFFDAD1),
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
