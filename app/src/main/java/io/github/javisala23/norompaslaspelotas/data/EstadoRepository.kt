package io.github.javisala23.norompaslaspelotas.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow

private val Context.estadoDataStore by preferencesDataStore(name = "estado_modo")

object EstadoCache { val activo = MutableStateFlow(false) }

/** Se completa cuando App terminó de cargar EstadoCache y ConfigCache desde disco. */
object Arranque { val listo = kotlinx.coroutines.CompletableDeferred<Unit>() }

class EstadoRepository(context: Context) {
    private val dataStore = context.applicationContext.estadoDataStore

    val activo: Flow<Boolean> = dataStore.data.map { preferencias ->
        preferencias[ACTIVO] ?: false
    }

    suspend fun guardarActivo(activo: Boolean) {
        dataStore.edit { preferencias ->
            preferencias[ACTIVO] = activo
        }
        EstadoCache.activo.value = activo
    }

    private companion object {
        val ACTIVO = booleanPreferencesKey("modo_activo")
    }
}
