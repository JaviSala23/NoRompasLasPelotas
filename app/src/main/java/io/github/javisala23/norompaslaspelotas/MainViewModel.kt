package io.github.javisala23.norompaslaspelotas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.javisala23.norompaslaspelotas.data.EstadoRepository
import io.github.javisala23.norompaslaspelotas.data.Config
import io.github.javisala23.norompaslaspelotas.data.ConfigRepository
import io.github.javisala23.norompaslaspelotas.service.ModoControl
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EstadoRepository(application)
    private val configRepository = ConfigRepository(application)

    val activo = repository.activo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )
    val config = configRepository.config.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Config())

    fun cambiarEstado(nuevoEstado: Boolean) {
        viewModelScope.launch {
            ModoControl.cambiar(getApplication(), nuevoEstado)
        }
    }

    fun guardarConfig(config: Config) { viewModelScope.launch { configRepository.guardar(config) } }
}
