package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.javisala23.norompaslaspelotas.MainActivity
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.service.ModoControl
import io.github.javisala23.norompaslaspelotas.ui.areaTactilMinima
import io.github.javisala23.norompaslaspelotas.ui.iconoApp
import io.github.javisala23.norompaslaspelotas.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class InicioActivity : ComponentActivity() {
    private var reanudaciones by mutableIntStateOf(0)

    override fun onResume() {
        super.onResume()
        reanudaciones++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AppTheme { InicioTranquilo(reanudaciones) } }
    }
}

@Composable
private fun InicioTranquilo(reanudaciones: Int) {
    BackHandler { }
    val activo by EstadoCache.activo.collectAsStateWithLifecycle()
    val config by ConfigCache.config.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val todas = remember { appsLanzables(context) }
    var buscar by remember { mutableStateOf("") }
    var ahora by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            ahora = Date()
            delay(30_000)
        }
    }
    LaunchedEffect(reanudaciones) { buscar = "" }
    if (activo) {
        ModoActivo(ahora, config.appsInicio.mapNotNull { paquete -> todas.find { it.paquete == paquete } })
    } else {
        ModoNormal(ahora, todas, buscar) { buscar = it }
    }
}

@Composable
private fun ModoActivo(ahora: Date, visibles: List<AppLanzable>) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(
        Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.6f))
        Text(DateFormat.getTimeInstance(DateFormat.SHORT).format(ahora), style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Light)
        Text(DateFormat.getDateInstance(DateFormat.FULL).format(ahora), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(20.dp))
        Text(stringResource(R.string.calm_phrase), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))
        LazyColumn(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            items(visibles, key = { it.paquete }) { app ->
                Text(
                    app.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.clickable { abrirApp(context, app.paquete) }.padding(14.dp).areaTactilMinima(),
                )
            }
        }
        TextButton(onClick = { scope.launch { ModoControl.desactivar(context) } }) {
            Text(stringResource(R.string.return_normal))
        }
        IconButton(onClick = { context.startActivity(Intent(context, MainActivity::class.java)) }) {
            Icon(Icons.Rounded.Settings, stringResource(R.string.settings))
        }
    }
}

@Composable
private fun ModoNormal(ahora: Date, todas: List<AppLanzable>, buscar: String, cambiarBusqueda: (String) -> Unit) {
    val context = LocalContext.current
    val visibles = todas.filter { it.nombre.contains(buscar, ignoreCase = true) }
    val elementos = remember(visibles) {
        visibles.groupBy { it.nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "#" }.toSortedMap()
    }
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(horizontal = 20.dp)) {
        Text(
            DateFormat.getTimeInstance(DateFormat.SHORT).format(ahora),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 20.dp),
        )
        OutlinedTextField(
            buscar,
            cambiarBusqueda,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            label = { Text(stringResource(R.string.search_apps)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        )
        LazyColumn(Modifier.weight(1f)) {
            elementos.forEach { (letra, apps) ->
                item(key = "seccion-$letra") {
                    Text(letra, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
                }
                items(apps, key = { it.paquete }) { app ->
                    val icono = remember(app.paquete) { iconoApp(context, app) }
                    Row(
                        Modifier.fillMaxWidth().clickable { abrirApp(context, app.paquete) }.padding(vertical = 8.dp).areaTactilMinima(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        icono?.let {
                            val matrix = remember { ColorMatrix().apply { setToSaturation(0f) } }
                            Image(it, null, Modifier.size(34.dp), colorFilter = ColorFilter.colorMatrix(matrix))
                            Spacer(Modifier.width(14.dp))
                        }
                        Text(app.nombre, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
