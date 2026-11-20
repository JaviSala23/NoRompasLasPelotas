package io.github.javisala23.norompaslaspelotas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

private val Context.intentosDataStore by preferencesDataStore(name = "intentos_llamadas")

class IntentosRepository(context: Context) {
    private val dataStore = context.applicationContext.intentosDataStore
    private val serializer = MapSerializer(String.serializer(), Long.serializer())
    suspend fun leer(): Map<String, Long> = dataStore.data.first()[INTENTOS]
        ?.let { runCatching { Json.decodeFromString(serializer, it) }.getOrNull() }.orEmpty()
    suspend fun guardar(numero: String, ahora: Long) {
        val nuevos = leer().filterValues { ahora - it <= 24 * 60 * 60 * 1000L }.toMutableMap()
        nuevos[numero] = ahora
        dataStore.edit { it[INTENTOS] = Json.encodeToString(serializer, nuevos) }
    }
    private companion object { val INTENTOS = stringPreferencesKey("intentos_json") }
}
