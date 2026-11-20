package io.github.javisala23.norompaslaspelotas.data

import android.app.NotificationManager
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.dndDataStore by preferencesDataStore(name = "estado_dnd")

@Serializable
data class EstadoDndPrevio(
    val filtro: Int,
    val categorias: Int,
    val llamadas: Int,
    val mensajes: Int,
    val efectosSuprimidos: Int,
    val conversaciones: Int,
)

class DndRepository(context: Context) {
    private val dataStore = context.applicationContext.dndDataStore
    suspend fun guardar(estado: EstadoDndPrevio) = dataStore.edit {
        it[ESTADO] = Json.encodeToString(EstadoDndPrevio.serializer(), estado)
    }
    suspend fun leer(): EstadoDndPrevio? = dataStore.data.first()[ESTADO]
        ?.let { runCatching { Json.decodeFromString<EstadoDndPrevio>(it) }.getOrNull() }
    suspend fun borrar() = dataStore.edit { it.remove(ESTADO) }
    private companion object { val ESTADO = stringPreferencesKey("estado_previo_json") }
}

fun NotificationManager.Policy.aEstado(filtro: Int) = EstadoDndPrevio(
    filtro, priorityCategories, priorityCallSenders, priorityMessageSenders,
    suppressedVisualEffects,
    if (android.os.Build.VERSION.SDK_INT >= 30) priorityConversationSenders else 0,
)
