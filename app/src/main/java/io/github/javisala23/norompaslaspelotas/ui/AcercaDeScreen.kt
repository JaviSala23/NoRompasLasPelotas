package io.github.javisala23.norompaslaspelotas.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.PublicOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R

@Composable
fun AcercaDeScreen(volver: () -> Unit) {
    val context = LocalContext.current
    PantallaConCabecera(stringResource(R.string.about), volver) { modifier ->
        Column(
            modifier = modifier.fillMaxSize().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            TarjetaPlana {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall)
                    Text(stringResource(R.string.version_name))
                    Text(stringResource(R.string.free_gpl))
                }
            }
            TarjetaPlana {
                Row(Modifier.padding(20.dp).areaTactilMinima(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.PublicOff, contentDescription = null)
                    Spacer(Modifier.width(14.dp))
                    Text(stringResource(R.string.no_internet))
                }
            }
            OutlinedButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL))) },
                modifier = Modifier.fillMaxWidth().areaTactilMinima(),
            ) {
                Icon(Icons.Rounded.Language, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.open_repository))
            }
        }
    }
}

private const val REPO_URL = "https://github.com/JaviSala23/NoRompasLasPelotas"
