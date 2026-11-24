package io.github.javisala23.norompaslaspelotas.ui

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.domain.Horario

@Composable
fun HorarioScreen(config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    val h = config.horario
    fun cambiar(nuevo: Horario) = guardar(config.copy(horario = nuevo))
    var editando by remember { mutableStateOf<Boolean?>(null) } // true = desde, false = hasta

    PantallaConCabecera(stringResource(R.string.schedule), volver) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                TarjetaPlana(Modifier.clickable { cambiar(h.copy(activo = !h.activo)) }) {
                    Row(Modifier.padding(20.dp).areaTactilMinima(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(stringResource(R.string.schedule_enable), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.schedule_enable_desc), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(h.activo, { cambiar(h.copy(activo = it)) })
                    }
                }
            }
            item { TituloSeccion(stringResource(R.string.schedule_days)) }
            item {
                TarjetaPlana {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            (1..7).forEach { dia ->
                                val marcado = dia in h.dias
                                Surface(
                                    shape = CircleShape,
                                    color = if (marcado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest,
                                    contentColor = if (marcado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f).aspectRatio(1f)
                                        .semantics {
                                            role = Role.Checkbox
                                            selected = marcado
                                            contentDescription = diaLargo(dia)
                                        }
                                        .clickable {
                                            cambiar(h.copy(dias = if (marcado) h.dias - dia else h.dias + dia))
                                        },
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(diaInicial(dia), style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                        if (h.dias.isEmpty()) {
                            Text(stringResource(R.string.schedule_no_days), color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(4.dp)) }
            item {
                GrupoFilas {
                    FilaHora(Icons.Rounded.Bedtime, stringResource(R.string.schedule_from), horaTexto(h.inicio), null) { editando = true }
                    HorizontalDivider(Modifier.padding(start = 60.dp), color = MaterialTheme.colorScheme.outlineVariant)
                    FilaHora(
                        Icons.Rounded.WbSunny,
                        stringResource(R.string.schedule_until),
                        horaTexto(h.fin),
                        if (h.terminaAlDiaSiguiente && !h.siempre) stringResource(R.string.next_day) else null,
                    ) { editando = false }
                }
            }
            item {
                Text(
                    if (h.siempre) stringResource(R.string.schedule_always) else stringResource(R.string.schedule_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                )
            }
        }
    }

    editando?.let { esDesde ->
        val minutos = if (esDesde) h.inicio else h.fin
        ElegirHora(minutos, cerrar = { editando = null }) { nuevos ->
            cambiar(if (esDesde) h.copy(inicio = nuevos) else h.copy(fin = nuevos))
            editando = null
        }
    }
}

@Composable
private fun FilaHora(icono: ImageVector, titulo: String, hora: String, nota: String?, alTocar: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = alTocar).padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            nota?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        Text(hora, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElegirHora(minutos: Int, cerrar: () -> Unit, elegir: (Int) -> Unit) {
    val context = LocalContext.current
    val estado = rememberTimePickerState(minutos / 60, minutos % 60, DateFormat.is24HourFormat(context))
    AlertDialog(
        onDismissRequest = cerrar,
        confirmButton = { TextButton(onClick = { elegir(estado.hour * 60 + estado.minute) }) { Text(stringResource(R.string.ok)) } },
        dismissButton = { TextButton(onClick = cerrar) { Text(stringResource(R.string.cancel)) } },
        text = { TimePicker(estado) },
    )
}
