package io.github.javisala23.norompaslaspelotas.ui

import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Contacts
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.data.Contacto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonasScreen(config: Config, guardar: (Config) -> Unit, volver: () -> Unit) {
    var mostrarSheet by remember { mutableStateOf(false) }
    var eliminado by remember { mutableStateOf<Pair<Int, Contacto>?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val mensajeEliminado = stringResource(R.string.person_deleted)
    val deshacer = stringResource(R.string.undo)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { mostrarSheet = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add)) },
            )
        },
    ) { outerPadding ->
        PantallaConCabecera(stringResource(R.string.people), volver) { modifier ->
            LazyColumn(
                modifier = modifier.fillMaxSize().padding(outerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (config.permitidos.isEmpty()) {
                    item {
                        Text(
                            stringResource(R.string.people_empty),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                itemsIndexed(config.permitidos, key = { index, contacto -> "$index:${contacto.nombre}" }) { index, contacto ->
                    ContactoCard(contacto) {
                        eliminado = index to contacto
                        guardar(config.copy(permitidos = config.permitidos.toMutableList().also { it.removeAt(index) }))
                        scope.launch {
                            val resultado = snackbar.showSnackbar(
                                message = mensajeEliminado,
                                actionLabel = deshacer,
                                duration = SnackbarDuration.Long,
                            )
                            if (resultado == SnackbarResult.ActionPerformed) {
                                eliminado?.let { (posicion, persona) ->
                                    val restaurados = config.permitidos.toMutableList()
                                    restaurados.add(posicion.coerceAtMost(restaurados.size), persona)
                                    guardar(config.copy(permitidos = restaurados))
                                }
                            }
                            eliminado = null
                        }
                    }
                }
            }
        }
    }
    if (mostrarSheet) {
        AgregarPersonaSheet(config, guardar) { mostrarSheet = false }
    }
}

@Composable
private fun ContactoCard(contacto: Contacto, borrar: () -> Unit) {
    TarjetaPlana {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) { Text(contacto.nombre.take(1).uppercase()) }
            }
            Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(contacto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(contacto.numeros.joinToString(" · "), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = borrar) {
                Icon(Icons.Rounded.Delete, stringResource(R.string.delete_person, contacto.nombre))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgregarPersonaSheet(config: Config, guardar: (Config) -> Unit, cerrar: () -> Unit) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var numero by remember { mutableStateOf("") }
    fun agregar(n: String, tel: String) {
        if (n.isBlank() || tel.isBlank()) return
        val lista = config.permitidos.toMutableList()
        val indice = lista.indexOfFirst { it.nombre.equals(n.trim(), true) }
        if (indice >= 0) {
            lista[indice] = lista[indice].copy(numeros = (lista[indice].numeros + tel.trim()).distinct())
        } else {
            lista += Contacto(n.trim(), listOf(tel.trim()))
        }
        guardar(config.copy(permitidos = lista))
        cerrar()
    }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
        resultado.data?.data?.let { uri ->
            context.contentResolver.query(uri, arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) agregar(cursor.getString(0).orEmpty(), cursor.getString(1).orEmpty())
            }
        }
    }
    ModalBottomSheet(onDismissRequest = cerrar) {
        Column(Modifier.padding(horizontal = 24.dp).padding(bottom = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.add_person), style = MaterialTheme.typography.headlineSmall)
            OutlinedButton(
                onClick = { picker.launch(Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)) },
                modifier = Modifier.fillMaxWidth().areaTactilMinima(),
            ) {
                Icon(Icons.Rounded.Contacts, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.choose_contact))
            }
            HorizontalDivider()
            OutlinedTextField(nombre, { nombre = it }, label = { Text(stringResource(R.string.name)) }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                numero,
                { numero = it },
                label = { Text(stringResource(R.string.phone_number)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { agregar(nombre, numero) },
                enabled = nombre.isNotBlank() && numero.isNotBlank(),
                modifier = Modifier.fillMaxWidth().areaTactilMinima(),
            ) { Text(stringResource(R.string.add)) }
        }
    }
}
