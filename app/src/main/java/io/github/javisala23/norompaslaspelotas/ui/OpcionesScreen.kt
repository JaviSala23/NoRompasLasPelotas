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
import io.github.javisala23.norompaslaspelotas.data.FondoInicio
import io.github.javisala23.norompaslaspelotas.ui.inicio.paletaDe
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.semantics.Role

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
            item { TituloSeccion(stringResource(R.string.quiet_screen)) }
            item { ElegirFondo(config.fondoInicio) { guardar(config.copy(fondoInicio = it)) } }
            item {
                Text(
                    stringResource(R.string.detox_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
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

@Composable
private fun ElegirFondo(actual: FondoInicio, elegir: (FondoInicio) -> Unit) {
    TarjetaPlana {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(stringResource(R.string.background_title), style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FondoInicio.entries.forEach { fondo ->
                    val paleta = paletaDe(fondo)
                    val nombre = stringResource(
                        when (fondo) {
                            FondoInicio.NEGRO -> R.string.background_black
                            FondoInicio.GRAFITO -> R.string.background_graphite
                            FondoInicio.BOSQUE -> R.string.background_forest
                            FondoInicio.ARENA -> R.string.background_sand
                        },
                    )
                    val elegido = fondo == actual
                    Column(
                        Modifier.selectable(elegido, role = Role.RadioButton) { elegir(fondo) }.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Box(
                            Modifier.size(56.dp)
                                .border(
                                    if (elegido) 3.dp else 1.dp,
                                    if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                    CircleShape,
                                )
                                .padding(if (elegido) 5.dp else 0.dp)
                                .background(paleta.fondo, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("Aa", color = paleta.texto, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(nombre, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
