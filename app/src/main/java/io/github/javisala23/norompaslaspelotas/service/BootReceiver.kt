package io.github.javisala23.norompaslaspelotas.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.javisala23.norompaslaspelotas.data.EstadoRepository
import io.github.javisala23.norompaslaspelotas.data.ConfigRepository
import io.github.javisala23.norompaslaspelotas.domain.EstadoModo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in ACCIONES) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ConfigRepository(context).cargarCache()
                if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
                    val activo = EstadoRepository(context).activo.first()
                    if (EstadoModo.restaurarAlArrancar(activo).debeEjecutarServicio) {
                        ModoControl.activar(context)
                    }
                }
                Programador.reprogramar(context)
            } finally {
                pendingResult.finish()
            }
        }
    }

    private companion object {
        val ACCIONES = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
        )
    }
}
