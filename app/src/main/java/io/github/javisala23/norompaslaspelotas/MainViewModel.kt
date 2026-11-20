package io.github.javisala23.norompaslaspelotas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.javisala23.norompaslaspelotas.data.EstadoRepository
import io.github.javisala23.norompaslaspelotas.domain.EstadoModo
import io.github.javisala23.norompaslaspelotas.service.ModoService
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EstadoRepository(application)

    val activo = repository.activo.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false,
    )

    fun cambiarEstado(nuevoEstado: Boolean) {
        viewModelScope.launch {
            val resultado = EstadoModo.cambiar(activo.value, nuevoEstado)
            repository.guardarActivo(resultado.activo)
            if (resultado.debeEjecutarServicio) {
                ModoService.iniciar(getApplication())
            } else {
                ModoService.detener(getApplication())
            }
        }
    }
}
