package io.github.javisala23.norompaslaspelotas.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Bloqueo
import io.github.javisala23.norompaslaspelotas.data.TipoBloqueo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun RegistroScreen(registro: List<Bloqueo>, borrar: () -> Unit, volver: () -> Unit) {
    var confirmar by remember { mutableStateOf(false) }
    val zona = ZoneId.systemDefault()
    val porDia = remember(registro) {
        registro.sortedByDescending { it.momento }
            .groupBy { Instant.ofEpochMilli(it.momento).atZone(zona).toLocalDate() }
    }
    PantallaConCabecera(
        stringResource(R.string.blocked_log),
        volver,
        acciones = {
            if (registro.isNotEmpty()) {
                IconButton(onClick = { confirmar = true }) {
                    Icon(Icons.Rounded.DeleteSweep, stringResource(R.string.clear_log))
                }
            }
        },
    ) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (registro.isEmpty()) {
                item { Vacio(Icons.Rounded.Shield, stringResource(R.string.blocked_log_empty)) }
            }
            porDia.forEach { (dia, bloqueos) ->
                item(key = dia.toString()) { TituloSeccion(nombreDia(dia)) }
                item(key = "$dia-grupo") {
                    GrupoFilas {
                        bloqueos.forEachIndexed { i, bloqueo ->
                            if (i > 0) HorizontalDivider(Modifier.padding(start = 72.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            FilaBloqueo(bloqueo)
                        }
                    }
                }
            }
            item {
                Text(
                    stringResource(R.string.blocked_log_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                )
            }
        }
    }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text(stringResource(R.string.clear_log_confirm)) },
            confirmButton = { TextButton(onClick = { borrar(); confirmar = false }) { Text(stringResource(R.string.delete)) } },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text(stringResource(R.string.cancel)) } },
        )
    }
}

@Composable
private fun FilaBloqueo(bloqueo: Bloqueo) {
    val context = LocalContext.current
    val app by produceState<Pair<String, ImageBitmap?>?>(null, bloqueo.paquete) {
        value = bloqueo.paquete?.let { withContext(Dispatchers.IO) { datosApp(context, it) } }
    }
    val hora = Instant.ofEpochMilli(bloqueo.momento).atZone(ZoneId.systemDefault()).toLocalTime()
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
            val icono = app?.second
            if (bloqueo.tipo == TipoBloqueo.NOTIFICACION && icono != null) {
                Image(icono, contentDescription = null, modifier = Modifier.size(40.dp))
            } else {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Rounded.Call, contentDescription = null, modifier = Modifier.size(20.dp)) }
                }
            }
        }
        Column(Modifier.weight(1f)) {
            val titulo = when {
                bloqueo.quien.isNotBlank() -> bloqueo.quien
                bloqueo.tipo == TipoBloqueo.LLAMADA -> stringResource(R.string.unknown_number)
                else -> app?.first ?: bloqueo.paquete.orEmpty()
            }
            Text(titulo, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                if (bloqueo.tipo == TipoBloqueo.LLAMADA) stringResource(R.string.call) else app?.first ?: bloqueo.paquete.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(horaTexto(hora), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun Vacio(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String, accion: @Composable () -> Unit = {}) {
    Column(
        Modifier.fillMaxWidth().padding(vertical = 48.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(72.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        Text(texto, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        accion()
    }
}

@Composable
private fun nombreDia(dia: LocalDate): String {
    val hoy = LocalDate.now()
    return when (dia) {
        hoy -> stringResource(R.string.today)
        hoy.minusDays(1) -> stringResource(R.string.yesterday)
        else -> diaLargo(dia.dayOfWeek.value).replaceFirstChar { it.uppercase() } + " " +
            dia.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }
}

private fun datosApp(context: Context, paquete: String): Pair<String, ImageBitmap?>? = runCatching {
    val pm = context.packageManager
    val info = pm.getApplicationInfo(paquete, 0)
    pm.getApplicationLabel(info).toString() to runCatching { pm.getApplicationIcon(info).toBitmap(96, 96).asImageBitmap() }.getOrNull()
}.getOrNull()
