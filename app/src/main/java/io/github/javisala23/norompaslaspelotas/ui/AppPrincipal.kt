package io.github.javisala23.norompaslaspelotas.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import io.github.javisala23.norompaslaspelotas.data.Bloqueo
import io.github.javisala23.norompaslaspelotas.data.Config

enum class Pantalla { INICIO, PERSONAS, SILENCIADAS, APPS_INICIO, OPCIONES, PERMISOS, ACERCA_DE, REGISTRO, HORARIO }

@Composable
fun AppPrincipal(
    activo: Boolean,
    config: Config,
    registro: List<Bloqueo>,
    refreshPermisos: Int,
    onActivoChange: (Boolean) -> Unit,
    onConfigChange: (Config) -> Unit,
    onBorrarRegistro: () -> Unit,
) {
    if (!config.onboardingHecho) {
        OnboardingScreen(config, refreshPermisos, onConfigChange)
        return
    }
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.INICIO) }
    BackHandler(pantalla != Pantalla.INICIO) { pantalla = Pantalla.INICIO }
    Crossfade(pantalla, label = "pantalla") { destino ->
        when (destino) {
            Pantalla.INICIO -> InicioScreen(activo, config, registro, refreshPermisos, onActivoChange) { pantalla = it }
            Pantalla.PERSONAS -> PersonasScreen(config, onConfigChange) { pantalla = Pantalla.INICIO }
            Pantalla.SILENCIADAS -> AppsCheckScreen(TipoListaApps.SILENCIADAS, config, onConfigChange) { pantalla = Pantalla.INICIO }
            Pantalla.APPS_INICIO -> AppsCheckScreen(TipoListaApps.INICIO, config, onConfigChange) { pantalla = Pantalla.INICIO }
            Pantalla.OPCIONES -> OpcionesScreen(config, onConfigChange) { pantalla = Pantalla.INICIO }
            Pantalla.PERMISOS -> PermisosScreen(refreshPermisos) { pantalla = Pantalla.INICIO }
            Pantalla.ACERCA_DE -> AcercaDeScreen { pantalla = Pantalla.INICIO }
            Pantalla.REGISTRO -> RegistroScreen(registro, onBorrarRegistro) { pantalla = Pantalla.INICIO }
            Pantalla.HORARIO -> HorarioScreen(config, onConfigChange) { pantalla = Pantalla.INICIO }
        }
    }
}
