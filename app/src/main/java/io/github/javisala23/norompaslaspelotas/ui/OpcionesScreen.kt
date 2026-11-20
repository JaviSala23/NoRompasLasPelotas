package io.github.javisala23.norompaslaspelotas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config

@Composable
fun OpcionesScreen(config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    PantallaConCabecera(stringResource(R.string.options), volver) { modifier ->
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Opcion(R.string.whatsapp_allowed, R.string.whatsapp_allowed_description, config.whatsappSoloPermitidos) { guardar(config.copy(whatsappSoloPermitidos = it)) } }
            item { Opcion(R.string.repeat_calls, R.string.repeat_calls_description, config.dejarPasarSiInsiste) { guardar(config.copy(dejarPasarSiInsiste = it)) } }
            item { Opcion(R.string.use_system_dnd, R.string.use_system_dnd_description, config.usarNoMolestarDelSistema) { guardar(config.copy(usarNoMolestarDelSistema = it)) } }
        }
    }
}

@Composable
private fun Opcion(titulo: Int, descripcion: Int, valor: Boolean, cambiar: (Boolean) -> Unit) {
    TarjetaPlana {
        Row(Modifier.padding(20.dp).areaTactilMinima(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(end = 12.dp)) {
                Text(stringResource(titulo), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(descripcion), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(valor, cambiar)
        }
    }
}
