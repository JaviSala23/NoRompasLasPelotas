package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.data.Contacto
import io.github.javisala23.norompaslaspelotas.ui.inicio.appsLanzables

@Composable
fun OnboardingScreen(config: Config, refreshPermisos: Int, guardar: (Config) -> Unit) {
    var paso by remember { mutableIntStateOf(0) }
    var permisosAbiertos by remember { mutableStateOf(false) }
    if (permisosAbiertos) {
        PermisosScreen(refreshPermisos) { permisosAbiertos = false }
        return
    }
    Scaffold(bottomBar = {
        Row(
            Modifier.fillMaxWidth().padding(20.dp).navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TextButton(onClick = {
                if (paso == 2) guardar(config.copy(onboardingHecho = true)) else paso++
            }, modifier = Modifier.weight(1f).areaTactilMinima()) {
                Text(stringResource(R.string.skip))
            }
            Button(onClick = {
                if (paso == 2) guardar(config.copy(onboardingHecho = true)) else paso++
            }, modifier = Modifier.weight(1f).areaTactilMinima()) {
                Text(stringResource(if (paso == 2) R.string.finish else R.string.next))
            }
        }
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp, vertical = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(stringResource(R.string.welcome), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(progress = { (paso + 1) / 3f }, modifier = Modifier.fillMaxWidth())
            Text(stringResource(R.string.step_of, paso + 1, 3), color = MaterialTheme.colorScheme.onSurfaceVariant)
            when (paso) {
                0 -> PasoPermisos(refreshPermisos) { permisosAbiertos = true }
                1 -> PasoPersona(config, guardar)
                else -> PasoApps(config, guardar)
            }
        }
    }
}

@Composable
private fun PasoPermisos(refreshPermisos: Int, abrirPermisos: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(stringResource(R.string.onboarding_permissions_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.onboarding_permissions_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(stringResource(R.string.permissions_ready, 5 - cantidadPermisosFaltantes(refreshPermisos), 5))
        Button(onClick = abrirPermisos) {
            Text(stringResource(R.string.configure_permissions))
        }
    }
}

@Composable
private fun PasoPersona(config: Config, guardar: (Config) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.onboarding_people_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.onboarding_people_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(nombre, { nombre = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            numero,
            { numero = it },
            label = { Text(stringResource(R.string.phone_number)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedButton(
            onClick = {
                guardar(config.copy(permitidos = config.permitidos + Contacto(nombre.trim(), listOf(numero.trim()))))
                nombre = ""
                numero = ""
            },
            enabled = nombre.isNotBlank() && numero.isNotBlank(),
        ) { Text(stringResource(R.string.add)) }
        if (config.permitidos.isNotEmpty()) Text(stringResource(R.string.people_added, config.permitidos.size))
    }
}

@Composable
private fun PasoApps(config: Config, guardar: (Config) -> Unit) {
    val context = LocalContext.current
    val apps = remember { appsLanzables(context) }
    val sugeridas = remember(apps) { apps.filter { it.paquete in redesOnboarding } }
    LaunchedEffect(Unit) {
        if (!config.sugerenciasAppsAplicadas) {
            guardar(config.copy(appsSilenciadas = config.appsSilenciadas + sugeridas.map { it.paquete }, sugerenciasAppsAplicadas = true))
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.onboarding_apps_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.onboarding_apps_body), color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (sugeridas.isEmpty()) {
            Text(stringResource(R.string.no_suggested_apps))
        } else {
            LazyColumn(Modifier.weight(1f, fill = false)) {
                items(sugeridas, key = { it.paquete }) { app ->
                    Row(Modifier.fillMaxWidth().areaTactilMinima(), verticalAlignment = Alignment.CenterVertically) {
                        Text(app.nombre, Modifier.weight(1f))
                        Checkbox(
                            app.paquete in config.appsSilenciadas,
                            { marcada ->
                                guardar(config.copy(appsSilenciadas = if (marcada) config.appsSilenciadas + app.paquete else config.appsSilenciadas - app.paquete))
                            },
                        )
                    }
                }
            }
        }
    }
}

private val redesOnboarding = setOf(
    "com.instagram.android",
    "com.facebook.katana",
    "com.zhiliaoapp.musically",
    "com.twitter.android",
    "org.telegram.messenger",
    "com.facebook.orca",
    "com.snapchat.android",
)
