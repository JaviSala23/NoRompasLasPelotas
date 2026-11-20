package io.github.javisala23.norompaslaspelotas.ui

import android.Manifest
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.data.Contacto
import io.github.javisala23.norompaslaspelotas.ui.inicio.AppLanzable
import io.github.javisala23.norompaslaspelotas.ui.inicio.appsLanzables

private enum class Pantalla { INICIO, PERSONAS, SILENCIADAS, APPS_INICIO, OPCIONES, PERMISOS }

@Composable fun AppPrincipal(activo: Boolean, config: Config, refreshPermisos: Int,
    onActivoChange: (Boolean) -> Unit, onConfigChange: (Config) -> Unit) {
    var pantalla by remember { mutableStateOf(Pantalla.INICIO) }
    BackHandler(pantalla != Pantalla.INICIO) { pantalla = Pantalla.INICIO }
    Crossfade(pantalla, label = "pantalla") { destino -> when (destino) {
        Pantalla.INICIO -> MenuPrincipal(activo, onActivoChange) { pantalla = it }
        Pantalla.PERSONAS -> PersonasScreen(config, onConfigChange) { pantalla = Pantalla.INICIO }
        Pantalla.SILENCIADAS -> AppsCheckScreen("Apps silenciadas", config.appsSilenciadas, { onConfigChange(config.copy(appsSilenciadas = it.toSet())) }) { pantalla = Pantalla.INICIO }
        Pantalla.APPS_INICIO -> AppsCheckScreen("Apps en la pantalla tranquila", config.appsInicio.toSet(), { onConfigChange(config.copy(appsInicio = it)) }) { pantalla = Pantalla.INICIO }
        Pantalla.OPCIONES -> OpcionesScreen(config, onConfigChange) { pantalla = Pantalla.INICIO }
        Pantalla.PERMISOS -> PermisosScreen(refreshPermisos) { pantalla = Pantalla.INICIO }
    } }
}

@Composable private fun MenuPrincipal(activo: Boolean, cambiar: (Boolean) -> Unit, ir: (Pantalla) -> Unit) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(28.dp)) {
        Text("NoRompasLasPelotas", style = MaterialTheme.typography.displaySmall)
        Text("Un rato más tranquilo.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Switch(activo, cambiar, Modifier.semantics { role = Role.Switch })
                Text(if (activo) "Está activo" else "Está apagado", style = MaterialTheme.typography.titleLarge)
            }
        }
        listOf("Personas que pueden llamarte" to Pantalla.PERSONAS, "Apps silenciadas" to Pantalla.SILENCIADAS,
            "Apps en la pantalla tranquila" to Pantalla.APPS_INICIO, "Opciones" to Pantalla.OPCIONES,
            "Permisos" to Pantalla.PERMISOS).forEach { (texto, destino) ->
            TextButton({ ir(destino) }, Modifier.fillMaxWidth()) { Text(texto) }
        }
    }
}

@Composable private fun Cabecera(titulo: String, volver: () -> Unit) {
    OutlinedButton(volver) { Text("Volver") }; Spacer(Modifier.height(24.dp))
    Text(titulo, style = MaterialTheme.typography.headlineLarge); Spacer(Modifier.height(20.dp))
}

@Composable private fun PersonasScreen(config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }; var numero by remember { mutableStateOf("") }
    fun agregar(n: String, tel: String) {
        if (n.isBlank() || tel.isBlank()) return
        val lista = config.permitidos.toMutableList(); val indice = lista.indexOfFirst { it.nombre.equals(n.trim(), true) }
        if (indice >= 0) lista[indice] = lista[indice].copy(numeros = (lista[indice].numeros + tel.trim()).distinct())
        else lista += Contacto(n.trim(), listOf(tel.trim()))
        guardar(config.copy(permitidos = lista)); nombre = ""; numero = ""
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        resultado.data?.data?.let { uri -> context.contentResolver.query(uri, arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)
            ?.use { c -> if (c.moveToFirst()) agregar(c.getString(0).orEmpty(), c.getString(1).orEmpty()) } }
    }
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp)) {
        Cabecera("Personas que pueden llamarte", volver)
        Button({ picker.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)) }) { Text("Elegir de contactos") }
        OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(numero, { numero = it }, label = { Text("Número") }, modifier = Modifier.fillMaxWidth())
        Button({ agregar(nombre, numero) }, enabled = nombre.isNotBlank() && numero.isNotBlank()) { Text("Agregar") }
        LazyColumn { items(config.permitidos, key = { it.nombre }) { contacto ->
            ListItem({ Text(contacto.nombre) }, modifier = Modifier.clickable { guardar(config.copy(permitidos = config.permitidos - contacto)) },
                supportingContent = { Text(contacto.numeros.joinToString(" · ")) })
        } }
    }
}

@Composable private fun AppsCheckScreen(titulo: String, seleccionadas: Set<String>, guardar: (List<String>) -> Unit, volver: () -> Unit) {
    val contexto = LocalContext.current
    val apps = remember { appsLanzables(contexto) }
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp)) {
        Cabecera(titulo, volver)
        LazyColumn { items(apps, key = AppLanzable::paquete) { app -> val marcada = app.paquete in seleccionadas
            ListItem({ Text(app.nombre) }, trailingContent = { Checkbox(marcada, null) }, modifier = Modifier.clickable {
                val nuevas = if (marcada) seleccionadas - app.paquete else seleccionadas + app.paquete
                guardar(apps.map { it.paquete }.filter { it in nuevas })
            })
        } }
    }
}

@Composable private fun OpcionesScreen(config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp)) {
        Cabecera("Opciones", volver)
        Opcion("WhatsApp sólo de personas permitidas", "Filtra por el nombre que muestra WhatsApp.", config.whatsappSoloPermitidos) { guardar(config.copy(whatsappSoloPermitidos = it)) }
        Opcion("Dejar pasar si insiste", "La segunda llamada del mismo número en 15 minutos pasa.", config.dejarPasarSiInsiste) { guardar(config.copy(dejarPasarSiInsiste = it)) }
        Opcion("Usar No molestar del sistema", "Mantiene alarmas y contenido multimedia.", config.usarNoMolestarDelSistema) { guardar(config.copy(usarNoMolestarDelSistema = it)) }
    }
}
@Composable private fun Opcion(t: String, d: String, v: Boolean, cambiar: (Boolean) -> Unit) =
    ListItem({ Text(t) }, supportingContent = { Text(d) }, trailingContent = { Switch(v, cambiar) })

private data class Permiso(val titulo: String, val listo: Boolean, val abrir: () -> Unit)
@Composable private fun PermisosScreen(refresh: Int, volver: () -> Unit) {
    val context = LocalContext.current; val role = context.getSystemService(RoleManager::class.java)
    val manager = context.getSystemService(NotificationManager::class.java); refresh.hashCode()
    val permisos = listOf(
        Permiso("Notificaciones", Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)) },
        Permiso("Filtro de llamadas", role.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) { context.startActivity(role.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)) },
        Permiso("Acceso a notificaciones", Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")?.contains(context.packageName) == true) { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
        Permiso("No molestar", manager.isNotificationPolicyAccessGranted) { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)) },
        Permiso("Pantalla de inicio predeterminada", esLauncher(context)) { context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) },
    )
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(24.dp)) {
        Cabecera("Permisos", volver)
        LazyColumn { items(permisos) { p -> ListItem({ Text(p.titulo) }, supportingContent = { Text(if (p.listo) "✓ Listo" else "Falta configurar") }, trailingContent = { Button(p.abrir) { Text("Ir a Ajustes") } }) } }
    }
}
private fun esLauncher(context: Context): Boolean = context.packageManager.resolveActivity(
    Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), PackageManager.MATCH_DEFAULT_ONLY)?.activityInfo?.packageName == context.packageName
