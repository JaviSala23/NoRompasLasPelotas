package io.github.javisala23.norompaslaspelotas.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private enum class Pantalla { INICIO, PERMISOS }

data class PermisoUi(
    val titulo: String,
    val explicacion: String,
    val abrirAjustes: (Context) -> Unit,
)

@Composable
fun AppPrincipal(
    activo: Boolean,
    onActivoChange: (Boolean) -> Unit,
) {
    var pantalla by remember { mutableStateOf(Pantalla.INICIO) }
    BackHandler(enabled = pantalla != Pantalla.INICIO) { pantalla = Pantalla.INICIO }

    Crossfade(targetState = pantalla, label = "cambio de pantalla") { destino ->
        when (destino) {
            Pantalla.INICIO -> InicioScreen(
                activo = activo,
                onActivoChange = onActivoChange,
                onPermisosClick = { pantalla = Pantalla.PERMISOS },
            )
            Pantalla.PERMISOS -> PermisosScreen(onBack = { pantalla = Pantalla.INICIO })
        }
    }
}

@Composable
private fun InicioScreen(
    activo: Boolean,
    onActivoChange: (Boolean) -> Unit,
    onPermisosClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 28.dp, vertical = 32.dp),
    ) {
        Text("NoRompasLasPelotas", style = MaterialTheme.typography.displaySmall)
        Text(
            text = "Un rato más tranquilo.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp),
            ) {
                Switch(
                    checked = activo,
                    onCheckedChange = onActivoChange,
                    modifier = Modifier
                        .scale(1.75f)
                        .semantics {
                            role = Role.Switch
                            stateDescription = if (activo) "Activo" else "Apagado"
                        },
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (activo) "Está activo" else "Está apagado",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (activo) "Podés descansar." else "Prendelo cuando quieras.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        Spacer(Modifier.height(18.dp))
        OutlinedButton(
            onClick = onPermisosClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Permisos")
        }
    }
}

@Composable
private fun PermisosScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val permisos = remember {
        listOf(
            PermisoUi(
                titulo = "Notificaciones",
                explicacion = "Para mostrar que NoRompasLasPelotas sigue activo.",
                abrirAjustes = ::abrirAjustesDeNotificaciones,
            ),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 28.dp, vertical = 24.dp),
    ) {
        OutlinedButton(onClick = onBack) { Text("Volver") }
        Spacer(Modifier.height(36.dp))
        Text("Permisos", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(12.dp))
        Text(
            "Los activás desde Ajustes, sólo si querés.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(42.dp))

        permisos.forEach { permiso ->
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(permiso.titulo, style = MaterialTheme.typography.titleLarge)
                Text(
                    permiso.explicacion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = { permiso.abrirAjustes(context) }) {
                    Text("Ir a Ajustes")
                }
            }
        }
    }
}

private fun abrirAjustesDeNotificaciones(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}
