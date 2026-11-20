package io.github.javisala23.norompaslaspelotas.ui

import android.Manifest
import android.app.NotificationManager
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R

private data class EstadoPermiso(
    val icono: ImageVector,
    val titulo: Int,
    val motivo: Int,
    val listo: Boolean,
    val activar: () -> Unit,
    val accesoNotificaciones: Boolean = false,
)

@Composable
fun cantidadPermisosFaltantes(refresh: Int): Int = estadosPermisos(refresh, null).count { !it.listo }

@Composable
fun PermisosScreen(refresh: Int, volver: () -> Unit) {
    var mostrarAyudaRol by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val role = context.getSystemService(RoleManager::class.java)
    val launcherRol = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        mostrarAyudaRol = !role.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }
    val permisos = estadosPermisos(refresh) {
        launcherRol.launch(role.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
    }
    val listos = permisos.count { it.listo }

    PantallaConCabecera(stringResource(R.string.permissions), volver) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    stringResource(R.string.permissions_ready, listos, permisos.size),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(Modifier.height(8.dp))
            }
            permisos.forEach { permiso ->
                item {
                    PermisoCard(permiso)
                    if (permiso.titulo == R.string.call_filter) {
                        AnimatedVisibility(mostrarAyudaRol && !permiso.listo) {
                            AyudaRol()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermisoCard(permiso: EstadoPermiso) {
    var ayudaExpandida by remember { mutableStateOf(false) }
    val context = LocalContext.current
    TarjetaPlana {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Icon(permiso.icono, contentDescription = null)
                Column(Modifier.weight(1f)) {
                    Text(stringResource(permiso.titulo), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(permiso.motivo), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(if (permiso.listo) R.string.ready else R.string.missing),
                    color = if (permiso.listo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                if (!permiso.listo) {
                    Button(onClick = permiso.activar) { Text(stringResource(R.string.enable)) }
                }
            }
            if (permiso.accesoNotificaciones && !permiso.listo && Build.VERSION.SDK_INT >= 33) {
                TextButton(onClick = { ayudaExpandida = !ayudaExpandida }) {
                    Text(stringResource(R.string.restricted_settings_help))
                }
                AnimatedVisibility(ayudaExpandida) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(stringResource(R.string.restricted_settings_steps))
                        OutlinedButton(onClick = {
                            context.startActivity(
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                                    Uri.parse("package:${context.packageName}"),
                                ),
                            )
                        }) { Text(stringResource(R.string.open_app_settings)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AyudaRol() {
    val context = LocalContext.current
    Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.call_role_help))
        OutlinedButton(onClick = {
            context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
        }) { Text(stringResource(R.string.open_default_apps)) }
    }
}

@Composable
private fun estadosPermisos(refresh: Int, abrirRol: (() -> Unit)?): List<EstadoPermiso> {
    val context = LocalContext.current
    val role = context.getSystemService(RoleManager::class.java)
    val manager = context.getSystemService(NotificationManager::class.java)
    refresh.hashCode()
    return listOf(
        EstadoPermiso(Icons.Rounded.Notifications, R.string.notifications, R.string.notifications_reason,
            Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED,
            { context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)) }),
        EstadoPermiso(Icons.Rounded.Call, R.string.call_filter, R.string.call_filter_reason,
            role.isRoleHeld(RoleManager.ROLE_CALL_SCREENING),
            abrirRol ?: {}),
        EstadoPermiso(Icons.Rounded.NotificationsOff, R.string.notification_access, R.string.notification_access_reason,
            Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")?.contains(context.packageName) == true,
            { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }, true),
        EstadoPermiso(Icons.Rounded.DoNotDisturb, R.string.system_dnd, R.string.system_dnd_reason,
            manager.isNotificationPolicyAccessGranted,
            { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)) }),
        EstadoPermiso(Icons.Rounded.Home, R.string.default_home, R.string.default_home_reason,
            esLauncher(context),
            { context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }),
    )
}
