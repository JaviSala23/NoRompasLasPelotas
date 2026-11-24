package io.github.javisala23.norompaslaspelotas.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.ConfigRepository
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.domain.Horario
import io.github.javisala23.norompaslaspelotas.domain.dentroDelHorario
import io.github.javisala23.norompaslaspelotas.domain.proximoCambio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Programa el próximo cambio del horario con una alarma inexacta (no hace falta el permiso de
 * alarmas exactas; puede demorar unos minutos). Al empezar la franja prende, al terminar apaga.
 */
object Programador {
    fun reprogramar(context: Context, horario: Horario = ConfigCache.config.value.horario) {
        val alarmas = context.getSystemService(AlarmManager::class.java)
        val cambio = proximoCambio(horario, LocalDateTime.now())
        if (cambio == null) {
            alarmas.cancel(intent(context, false))
            alarmas.cancel(intent(context, true))
            return
        }
        alarmas.cancel(intent(context, !cambio.activar))
        val millis = cambio.momento.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarmas.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, intent(context, cambio.activar))
    }

    /** Al prender el horario estando dentro de la franja, se prende ya. */
    suspend fun aplicarSiCorresponde(context: Context, horario: Horario) {
        if (dentroDelHorario(horario, LocalDateTime.now()) && !EstadoCache.activo.value) ModoControl.activar(context)
    }

    private fun intent(context: Context, activar: Boolean): PendingIntent = PendingIntent.getBroadcast(
        context,
        if (activar) 1 else 0,
        Intent(context, HorarioReceiver::class.java).putExtra(EXTRA_ACTIVAR, activar),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    const val EXTRA_ACTIVAR = "activar"
}

class HorarioReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendiente = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ConfigRepository(context).cargarCache()
                val horario = ConfigCache.config.value.horario
                if (horario.activo) {
                    if (intent.getBooleanExtra(Programador.EXTRA_ACTIVAR, false)) ModoControl.activar(context)
                    else ModoControl.desactivar(context)
                }
                Programador.reprogramar(context, horario)
            } finally {
                pendiente.finish()
            }
        }
    }
}
