package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Bloqueo
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.data.TipoBloqueo
import io.github.javisala23.norompaslaspelotas.domain.proximoCambio
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

@Composable
fun InicioScreen(
    activo: Boolean,
    config: Config,
    registro: List<Bloqueo>,
    refreshPermisos: Int,
    cambiar: (Boolean) -> Unit,
    ir: (Pantalla) -> Unit,
) {
    val faltantes = cantidadPermisosFaltantes(refreshPermisos)
    LazyColumn(
        modifier = Modifier.fillMaxSize().safeDrawingPadding(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(Modifier.padding(start = 4.dp, bottom = 8.dp)) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(saludo(), style = MaterialTheme.typography.headlineLarge)
            }
        }
        item { TarjetaEstado(activo, config, cambiar) }
        if (faltantes > 0) {
            item { AvisoPermisos(faltantes) { ir(Pantalla.PERMISOS) } }
        }
        item { TarjetaRegistro(registro) { ir(Pantalla.REGISTRO) } }
        item { TituloSeccion(stringResource(R.string.section_filters)) }
        item {
            GrupoFilas {
                FilaAjuste(
                    Icons.Rounded.People,
                    stringResource(R.string.people),
                    if (config.permitidos.isEmpty()) stringResource(R.string.people_subtitle_none)
                    else pluralStringResource(R.plurals.people_subtitle, config.permitidos.size, config.permitidos.size),
                ) { ir(Pantalla.PERSONAS) }
                FilaAjuste(
                    Icons.Rounded.NotificationsOff,
                    stringResource(R.string.silenced_apps),
                    pluralStringResource(R.plurals.apps_subtitle, config.appsSilenciadas.size, config.appsSilenciadas.size),
                    divisor = true,
                ) { ir(Pantalla.SILENCIADAS) }
                FilaAjuste(
                    Icons.Rounded.Schedule,
                    stringResource(R.string.schedule),
                    resumenHorario(config),
                    divisor = true,
                ) { ir(Pantalla.HORARIO) }
            }
        }
        item { TituloSeccion(stringResource(R.string.section_more)) }
        item {
            GrupoFilas {
                FilaAjuste(Icons.Rounded.Apps, stringResource(R.string.quiet_screen), stringResource(R.string.quiet_screen_subtitle)) { ir(Pantalla.APPS_INICIO) }
                FilaAjuste(Icons.Rounded.Tune, stringResource(R.string.options), stringResource(R.string.options_subtitle), divisor = true) { ir(Pantalla.OPCIONES) }
                FilaAjuste(
                    Icons.Rounded.Security,
                    stringResource(R.string.permissions),
                    if (faltantes == 0) stringResource(R.string.all_ready)
                    else pluralStringResource(R.plurals.missing_permissions, faltantes, faltantes),
                    divisor = true,
                ) { ir(Pantalla.PERMISOS) }
                FilaAjuste(Icons.Rounded.Info, stringResource(R.string.about), divisor = true) { ir(Pantalla.ACERCA_DE) }
            }
        }
    }
}

@Composable
private fun TarjetaEstado(activo: Boolean, config: Config, cambiar: (Boolean) -> Unit) {
    val colores = MaterialTheme.colorScheme
    val fondo by animateColorAsState(if (activo) colores.primary else colores.surfaceContainerHigh, label = "fondo estado")
    val texto by animateColorAsState(if (activo) colores.onPrimary else colores.onSurface, label = "texto estado")
    val haptico = LocalHapticFeedback.current
    Surface(
        color = fondo,
        contentColor = texto,
        shape = RoundedCornerShape(32.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    if (activo) Icons.Rounded.DarkMode else Icons.Rounded.WbSunny,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                )
                Text(
                    stringResource(if (activo) R.string.status_on_title else R.string.status_off_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (activo) {
                    LineaEstado(Icons.Rounded.Call, textoLlamadas(config))
                    LineaEstado(Icons.Rounded.NotificationsOff, pluralStringResource(R.plurals.apps_silenced_line, config.appsSilenciadas.size, config.appsSilenciadas.size))
                    LineaEstado(Icons.Rounded.Alarm, stringResource(R.string.alarms_still_ring))
                } else {
                    Text(stringResource(R.string.status_off_body), style = MaterialTheme.typography.bodyLarge)
                }
            }
            textoProximoCambio(config)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = texto.copy(alpha = 0.8f))
            }
            val accion = { haptico.performHapticFeedback(HapticFeedbackType.LongPress); cambiar(!activo) }
            if (activo) {
                OutlinedButton(
                    onClick = accion,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colores.onPrimary),
                    border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(colores.onPrimary.copy(alpha = 0.6f))),
                ) { Text(stringResource(R.string.turn_off), style = MaterialTheme.typography.titleMedium) }
            } else {
                Button(onClick = accion, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Icon(Icons.Rounded.DarkMode, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.turn_on), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun LineaEstado(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icono, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun textoLlamadas(config: Config): String =
    if (config.permitidos.isEmpty()) stringResource(R.string.nobody_can_call)
    else pluralStringResource(R.plurals.people_can_call, config.permitidos.size, config.permitidos.size)

@Composable
private fun textoProximoCambio(config: Config): String? {
    val cambio = proximoCambio(config.horario, LocalDateTime.now()) ?: return null
    val dia = diaLargo(cambio.momento.dayOfWeek.value)
    val hora = horaTexto(cambio.momento.toLocalTime())
    return stringResource(if (cambio.activar) R.string.schedule_turns_on else R.string.schedule_turns_off, dia, hora)
}

@Composable
fun resumenHorario(config: Config): String {
    val h = config.horario
    return if (!h.activo || h.dias.isEmpty()) stringResource(R.string.schedule_off)
    else stringResource(R.string.schedule_summary, diasTexto(h.dias), horaTexto(h.inicio), horaTexto(h.fin))
}

@Composable
private fun AvisoPermisos(faltantes: Int, abrir: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = abrir),
    ) {
        Row(
            Modifier.padding(20.dp).areaTactilMinima(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(Icons.Rounded.Warning, contentDescription = null)
            Text(
                pluralStringResource(R.plurals.missing_permissions, faltantes, faltantes),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun TarjetaRegistro(registro: List<Bloqueo>, abrir: () -> Unit) {
    val inicioHoy = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val hoy = registro.filter { it.momento >= inicioHoy }
    val llamadas = hoy.count { it.tipo == TipoBloqueo.LLAMADA }
    val notificaciones = hoy.size - llamadas
    TarjetaPlana(Modifier.clickable(onClick = abrir)) {
        Row(
            Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(Icons.Rounded.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.today_blocked_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    if (hoy.isEmpty()) stringResource(R.string.nothing_blocked_today)
                    else pluralStringResource(R.plurals.blocked_calls, llamadas, llamadas) + " · " +
                        pluralStringResource(R.plurals.blocked_notifications, notificaciones, notificaciones),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun saludo(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 6..11 -> stringResource(R.string.good_morning)
    in 12..19 -> stringResource(R.string.good_afternoon)
    else -> stringResource(R.string.good_evening)
}
