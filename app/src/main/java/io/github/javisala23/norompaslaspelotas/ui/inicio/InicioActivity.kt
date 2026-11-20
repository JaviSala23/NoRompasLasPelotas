package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.javisala23.norompaslaspelotas.MainActivity
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.service.ModoControl
import io.github.javisala23.norompaslaspelotas.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class InicioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); enableEdgeToEdge()
        setContent { AppTheme { InicioTranquilo() } }
    }
}

@Composable
private fun InicioTranquilo() {
    BackHandler { }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val activo by EstadoCache.activo.collectAsStateWithLifecycle()
    val config by ConfigCache.config.collectAsStateWithLifecycle()
    val todas = remember { appsLanzables(context) }
    var buscar by remember { mutableStateOf("") }
    var ahora by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) { while (true) { ahora = Date(); delay(30_000) } }
    val visibles = if (activo) config.appsInicio.mapNotNull { p -> todas.find { it.paquete == p } }
        else todas.filter { it.nombre.contains(buscar, ignoreCase = true) }
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(28.dp)) {
        Text(DateFormat.getTimeInstance(DateFormat.SHORT).format(ahora), style = MaterialTheme.typography.displayLarge)
        Text(DateFormat.getDateInstance(DateFormat.FULL).format(ahora), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        if (!activo) OutlinedTextField(buscar, { buscar = it }, label = { Text("Buscar apps") }, modifier = Modifier.fillMaxWidth())
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(vertical = 18.dp)) {
            items(visibles, key = { it.paquete }) { app ->
                Text(app.nombre, style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth().clickable { abrirApp(context, app.paquete) }.padding(vertical = 14.dp))
            }
        }
        if (activo) TextButton(onClick = { scope.launch { ModoControl.desactivar(context) } }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al modo normal")
        }
        TextButton(onClick = { context.startActivity(Intent(context, MainActivity::class.java)) }, modifier = Modifier.fillMaxWidth()) { Text("Ajustes") }
    }
}
