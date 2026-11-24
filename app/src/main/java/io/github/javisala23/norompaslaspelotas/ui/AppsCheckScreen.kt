package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.ui.inicio.AppLanzable
import io.github.javisala23.norompaslaspelotas.ui.inicio.appsLanzables
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val apps by produceState<List<AppLanzable>?>(null) {
        value = withContext(Dispatchers.IO) { appsLanzables(context).filter { it.paquete != context.packageName } }
    }
    var buscar by rememberSaveable { mutableStateOf("") }
    val seleccionadas = if (tipo == TipoListaApps.SILENCIADAS) config.appsSilenciadas else config.appsInicio.toSet()
    // El orden se fija al entrar: así una app no salta de sección apenas la tocás.
    val marcadasAlEntrar = remember(config.sugerenciasAppsAplicadas) { seleccionadas }

    LaunchedEffect(apps, config.sugerenciasAppsAplicadas) {
        val lista = apps ?: return@LaunchedEffect
        if (tipo == TipoListaApps.SILENCIADAS && !config.sugerenciasAppsAplicadas) {
            guardar(
                config.copy(
                    appsSilenciadas = config.appsSilenciadas + lista.map { it.paquete }.filter { it in redesSociales },
                    sugerenciasAppsAplicadas = true,
                ),
            )
        }
    }
    val titulo = if (tipo == TipoListaApps.SILENCIADAS) R.string.silenced_apps else R.string.quiet_screen

    PantallaConCabecera(stringResource(titulo), volver) { modifier ->
        val lista = apps
        if (lista == null) {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@PantallaConCabecera
        }
        val visibles = lista.filter { it.nombre.contains(buscar.trim(), ignoreCase = true) }
        val (arriba, abajo) = visibles.partition { it.paquete in marcadasAlEntrar }
        fun cambiar(app: AppLanzable) {
            val marcada = app.paquete in seleccionadas
            val nuevas = if (marcada) seleccionadas - app.paquete else seleccionadas + app.paquete
            if (tipo == TipoListaApps.SILENCIADAS) {
                guardar(config.copy(appsSilenciadas = nuevas, sugerenciasAppsAplicadas = true))
            } else {
                guardar(config.copy(appsInicio = lista.map { it.paquete }.filter { it in nuevas }))
            }
        }
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                OutlinedTextField(
                    buscar,
                    onValueChange = { buscar = it },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (buscar.isNotEmpty()) {
                            IconButton(onClick = { buscar = "" }) { Icon(Icons.Rounded.Close, stringResource(R.string.cancel)) }
                        }
                    },
                    placeholder = { Text(stringResource(R.string.search_apps)) },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (tipo == TipoListaApps.SILENCIADAS && buscar.isEmpty()) {
                    Text(
                        stringResource(R.string.social_suggestion),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (arriba.isNotEmpty()) {
                item { TituloSeccion(if (tipo == TipoListaApps.SILENCIADAS) stringResource(R.string.silenced_section) else stringResource(R.string.quiet_screen)) }
                items(arriba, key = { "a:" + it.paquete }) { app -> AppCheckCard(app, app.paquete in seleccionadas) { cambiar(app) } }
            }
            if (abajo.isNotEmpty()) {
                item { TituloSeccion(stringResource(R.string.other_apps_section)) }
                items(abajo, key = { "b:" + it.paquete }) { app -> AppCheckCard(app, app.paquete in seleccionadas) { cambiar(app) } }
            }
        }
    }
}

@Composable
private fun AppCheckCard(app: AppLanzable, marcada: Boolean, cambiar: () -> Unit) {
    val context = LocalContext.current
    val icono by produceState<ImageBitmap?>(null, app.paquete) {
        value = withContext(Dispatchers.IO) { iconoApp(context, app) }
    }
    TarjetaPlana(Modifier.clickable(onClick = cambiar)) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 8.dp).areaTactilMinima(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.size(40.dp)) {
                icono?.let { Image(it, contentDescription = null, modifier = Modifier.size(40.dp)) }
            }
            Text(app.nombre, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Checkbox(checked = marcada, onCheckedChange = { cambiar() })
        }
    }
}
