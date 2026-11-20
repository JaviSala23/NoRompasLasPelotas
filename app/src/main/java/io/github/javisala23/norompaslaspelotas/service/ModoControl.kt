package io.github.javisala23.norompaslaspelotas.service

import android.content.Context
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.EstadoRepository

object ModoControl {
    suspend fun activar(context: Context) {
        EstadoRepository(context).guardarActivo(true)
        ModoService.iniciar(context)
        if (ConfigCache.config.value.usarNoMolestarDelSistema) NoMolestarSistema.activar(context)
    }
    suspend fun desactivar(context: Context) {
        EstadoRepository(context).guardarActivo(false)
        ModoService.detener(context)
        NoMolestarSistema.desactivar(context)
    }
    suspend fun cambiar(context: Context, activo: Boolean) = if (activo) activar(context) else desactivar(context)
}
