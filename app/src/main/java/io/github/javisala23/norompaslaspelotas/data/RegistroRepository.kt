package io.github.javisala23.norompaslaspelotas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.registroDataStore by preferencesDataStore(name = "registro_bloqueos")

enum class TipoBloqueo { LLAMADA, NOTIFICACION }

/** Algo que la app frenó. Se guarda sólo en el teléfono, sin el texto de los mensajes. */
@Serializable
data class Bloqueo(
    val tipo: TipoBloqueo,
    val quien: String,
    val momento: Long,
    val paquete: String? = null,
    val clave: String? = null,
)

class RegistroRepository(context: Context) {
    private val dataStore = context.applicationContext.registroDataStore
    private val serializer = ListSerializer(Bloqueo.serializer())
    private val json = Json { ignoreUnknownKeys = true }

    val bloqueos: Flow<List<Bloqueo>> = dataStore.data.map { decodificar(it[REGISTRO]) }

    suspend fun agregar(bloqueo: Bloqueo) {
        dataStore.edit { preferencias ->
            val desde = bloqueo.momento - DURACION
            val lista = decodificar(preferencias[REGISTRO]).filter { it.momento >= desde }
            // Una app que actualiza la misma notificación varias veces cuenta una sola vez por minuto.
            val repetida = bloqueo.clave != null && lista.any {
                it.clave == bloqueo.clave && it.quien == bloqueo.quien && bloqueo.momento - it.momento < 60_000
            }
            if (!repetida) {
                preferencias[REGISTRO] = json.encodeToString(serializer, (lista + bloqueo).takeLast(MAXIMO))
            }
        }
    }

    suspend fun borrar() { dataStore.edit { it.remove(REGISTRO) } }

    private fun decodificar(texto: String?): List<Bloqueo> =
        texto?.let { runCatching { json.decodeFromString(serializer, it) }.getOrNull() }.orEmpty()

    private companion object {
        val REGISTRO = stringPreferencesKey("registro_json")
        const val DURACION = 7 * 24 * 60 * 60 * 1000L
        const val MAXIMO = 500
    }
}
