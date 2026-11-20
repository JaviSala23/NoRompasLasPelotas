package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.ui.inicio.AppLanzable
import io.github.javisala23.norompaslaspelotas.ui.inicio.appsLanzables

enum class TipoListaApps { SILENCIADAS, INICIO }

private val redesSociales = setOf(
    "com.instagram.android",
    "com.facebook.katana",
    "com.zhiliaoapp.musically",
    "com.twitter.android",
    "org.telegram.messenger",
    "com.facebook.orca",
    "com.snapchat.android",
)

@Composable
fun AppsCheckScreen(tipo: TipoListaApps, config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    val context = LocalContext.current
    val apps = remember { appsLanzables(context) }
    var buscar by remember { mutableStateOf("") }
    val seleccionadas = if (tipo == TipoListaApps.SILENCIADAS) config.appsSilenciadas else config.appsInicio.toSet()

    LaunchedEffect(tipo, config.sugerenciasAppsAplicadas) {
        if (tipo == TipoListaApps.SILENCIADAS && !config.sugerenciasAppsAplicadas) {
            guardar(
                config.copy(
                    appsSilenciadas = config.appsSilenciadas + apps.map { it.paquete }.filter { it in redesSociales },
                    sugerenciasAppsAplicadas = true,
                ),
            )
        }
    }
    val visibles = apps
        .filter { it.nombre.contains(buscar, ignoreCase = true) }
        .sortedWith(compareByDescending<AppLanzable> { it.paquete in seleccionadas }.thenBy { it.nombre.lowercase() })
    val titulo = if (tipo == TipoListaApps.SILENCIADAS) R.string.silenced_apps else R.string.quiet_screen

    PantallaConCabecera(stringResource(titulo), volver) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                OutlinedTextField(
                    buscar,
                    { buscar = it },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    label = { Text(stringResource(R.string.search_apps)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (tipo == TipoListaApps.SILENCIADAS) {
                    Text(
                        stringResource(R.string.social_suggestion),
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Spacer(Modifier.height(12.dp))
                }
            }
            items(visibles, key = AppLanzable::paquete) { app ->
                val marcada = app.paquete in seleccionadas
                AppCheckCard(app, marcada) {
                    val nuevas = if (marcada) seleccionadas - app.paquete else seleccionadas + app.paquete
                    if (tipo == TipoListaApps.SILENCIADAS) {
                        guardar(config.copy(appsSilenciadas = nuevas, sugerenciasAppsAplicadas = true))
                    } else {
                        guardar(config.copy(appsInicio = apps.map { it.paquete }.filter { it in nuevas }))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppCheckCard(app: AppLanzable, marcada: Boolean, cambiar: () -> Unit) {
    val context = LocalContext.current
    val icono = remember(app.paquete) { iconoApp(context, app) }
    TarjetaPlana(Modifier.clickable(onClick = cambiar)) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 10.dp).areaTactilMinima(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            icono?.let {
                Image(it, contentDescription = null, modifier = Modifier.size(40.dp))
            }
            Text(app.nombre, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Checkbox(
                checked = marcada,
                onCheckedChange = { cambiar() },
                modifier = Modifier.size(48.dp),
            )
        }
    }
}
