package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import java.util.Calendar

@Composable
fun InicioScreen(
    activo: Boolean,
    config: Config,
    refreshPermisos: Int,
    cambiar: (Boolean) -> Unit,
    ir: (Pantalla) -> Unit,
) {
    val faltantes = cantidadPermisosFaltantes(refreshPermisos)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(stringResource(R.string.app_name), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(saludo(), style = MaterialTheme.typography.headlineLarge)
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                BotonModo(activo, cambiar)
                Spacer(Modifier.height(18.dp))
                Text(resumen(config), color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
        if (faltantes > 0) {
            item {
                TarjetaPlana(Modifier.clickable { ir(Pantalla.PERMISOS) }) {
                    Row(
                        Modifier.padding(20.dp).areaTactilMinima(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Icon(Icons.Rounded.Security, contentDescription = null)
                        Text(pluralStringResource(R.plurals.missing_permissions, faltantes, faltantes), Modifier.weight(1f))
                    }
                }
            }
        }
        item { FilaMenu(Icons.Rounded.People, R.string.people, Pantalla.PERSONAS, ir) }
        item { FilaMenu(Icons.Rounded.NotificationsOff, R.string.silenced_apps, Pantalla.SILENCIADAS, ir) }
        item { FilaMenu(Icons.Rounded.Apps, R.string.quiet_screen, Pantalla.APPS_INICIO, ir) }
        item { FilaMenu(Icons.Rounded.Settings, R.string.options, Pantalla.OPCIONES, ir) }
        item { FilaMenu(Icons.Rounded.Security, R.string.permissions, Pantalla.PERMISOS, ir) }
        item { FilaMenu(Icons.Rounded.Info, R.string.about, Pantalla.ACERCA_DE, ir) }
    }
}

@Composable
private fun BotonModo(activo: Boolean, cambiar: (Boolean) -> Unit) {
    val fondo by animateColorAsState(
        if (activo) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        label = "color modo",
    )
    val borde = if (activo) Color.Transparent else MaterialTheme.colorScheme.outlineVariant
    Card(
        onClick = { cambiar(!activo) },
        modifier = Modifier.size(200.dp).semantics { role = Role.Switch },
        shape = CircleShape,
        border = BorderStroke(1.dp, borde),
        colors = CardDefaults.cardColors(containerColor = fondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.DarkMode, contentDescription = null, modifier = Modifier.size(54.dp))
                Spacer(Modifier.height(10.dp))
                Text(stringResource(if (activo) R.string.active else R.string.turn_on), style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Composable
private fun FilaMenu(icono: ImageVector, texto: Int, destino: Pantalla, ir: (Pantalla) -> Unit) {
    TarjetaPlana(Modifier.clickable { ir(destino) }) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp).areaTactilMinima(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(icono, contentDescription = null)
            Text(stringResource(texto), style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun resumen(config: Config): String = stringResource(R.string.status_summary, config.permitidos.size, config.appsSilenciadas.size)

@Composable
private fun saludo(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 6..11 -> stringResource(R.string.good_morning)
    in 12..19 -> stringResource(R.string.good_afternoon)
    else -> stringResource(R.string.good_evening)
}
