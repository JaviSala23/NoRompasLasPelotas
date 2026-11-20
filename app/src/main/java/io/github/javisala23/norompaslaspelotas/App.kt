package io.github.javisala23.norompaslaspelotas

import android.app.Application
import io.github.javisala23.norompaslaspelotas.data.ConfigRepository
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.data.EstadoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class App : Application() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        scope.launch {
            ConfigRepository(this@App).cargarCache()
            EstadoCache.activo.value = EstadoRepository(this@App).activo.first()
            io.github.javisala23.norompaslaspelotas.data.Arranque.listo.complete(Unit)
        }
    }
}
