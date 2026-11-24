package io.github.javisala23.norompaslaspelotas.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.ui.inicio.AppLanzable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConCabecera(
    titulo: String,
    volver: () -> Unit,
    acciones: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    contenido: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                navigationIcon = {
                    IconButton(onClick = volver) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = acciones,
            )
        },
        floatingActionButton = floatingActionButton,
    ) { padding -> contenido(Modifier.padding(padding)) }
}

@Composable
fun TarjetaPlana(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) { contenido() }
}

fun iconoApp(context: Context, app: AppLanzable): ImageBitmap? = runCatching {
    context.packageManager.getApplicationIcon(app.paquete).toBitmap(96, 96).asImageBitmap()
}.getOrNull()

fun esLauncher(context: Context): Boolean = context.packageManager.resolveActivity(
    Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
    PackageManager.MATCH_DEFAULT_ONLY,
)?.activityInfo?.packageName == context.packageName

fun Modifier.areaTactilMinima(): Modifier = this.heightIn(min = 48.dp)

@Composable
fun TituloSeccion(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
    )
}

/** Varias filas en una sola tarjeta, separadas por líneas finas. */
@Composable
fun GrupoFilas(contenido: @Composable ColumnScope.() -> Unit) {
    TarjetaPlana { Column(content = contenido) }
}

@Composable
fun FilaAjuste(
    icono: ImageVector,
    titulo: String,
    subtitulo: String? = null,
    divisor: Boolean = false,
    alTocar: () -> Unit,
) {
    if (divisor) HorizontalDivider(Modifier.padding(start = 60.dp), color = MaterialTheme.colorScheme.outlineVariant)
    Row(
        Modifier.fillMaxWidth().clickable(onClick = alTocar)
            .padding(horizontal = 20.dp, vertical = 14.dp).areaTactilMinima(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            subtitulo?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(
            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
