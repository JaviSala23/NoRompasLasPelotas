package io.github.javisala23.norompaslaspelotas.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
