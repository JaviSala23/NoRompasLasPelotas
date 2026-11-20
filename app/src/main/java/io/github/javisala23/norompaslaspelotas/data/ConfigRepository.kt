package io.github.javisala23.norompaslaspelotas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val Context.configDataStore by preferencesDataStore(name = "configuracion")

@Serializable
data class Contacto(val nombre: String, val numeros: List<String>)

@Serializable
data class Config(
    val permitidos: List<Contacto> = emptyList(),
    val appsSilenciadas: Set<String> = emptySet(),
    val whatsappSoloPermitidos: Boolean = true,
    val dejarPasarSiInsiste: Boolean = true,
    val usarNoMolestarDelSistema: Boolean = true,
    val appsInicio: List<String> = emptyList(),
)

object ConfigCache {
    val config = MutableStateFlow(Config())
}

class ConfigRepository(context: Context) {
    private val dataStore = context.applicationContext.configDataStore
    private val json = Json { ignoreUnknownKeys = true }
    val config: Flow<Config> = dataStore.data.map { preferencias ->
        preferencias[CONFIG]?.let { runCatching { json.decodeFromString<Config>(it) }.getOrNull() } ?: Config()
    }

    suspend fun guardar(config: Config) {
        dataStore.edit { it[CONFIG] = json.encodeToString(Config.serializer(), config) }
        ConfigCache.config.value = config
    }

    suspend fun cargarCache() { ConfigCache.config.value = config.first() }

    private companion object { val CONFIG = stringPreferencesKey("config_json") }
}
