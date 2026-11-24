package io.github.javisala23.norompaslaspelotas.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telecom.TelecomManager
import io.github.javisala23.norompaslaspelotas.data.Bloqueo
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.RegistroRepository
import io.github.javisala23.norompaslaspelotas.data.TipoBloqueo
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.data.Arranque
import io.github.javisala23.norompaslaspelotas.domain.DecisionNotificacion
import io.github.javisala23.norompaslaspelotas.domain.decidirNotificacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FiltroNotificacionesService : NotificationListenerService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    override fun onListenerConnected() {
        super.onListenerConnected()
        scope.launch {
            Arranque.listo.await()
            var anterior = false
            EstadoCache.activo.collectLatest { activo ->
                if (activo && !anterior) activeNotifications?.forEach(::procesar)
                anterior = activo
            }
        }
    }
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (Arranque.listo.isCompleted) procesar(sbn) else scope.launch { Arranque.listo.await(); procesar(sbn) }
    }

    private fun procesar(sbn: StatusBarNotification) {
        if (!EstadoCache.activo.value || protegido(sbn.packageName)) return
        val n = sbn.notification
        // Las alarmas nunca se tocan, aunque la app del reloj esté silenciada.
        if (n.category == Notification.CATEGORY_ALARM) return
        val resumen = n.flags and Notification.FLAG_GROUP_SUMMARY != 0
        val extras = n.extras
        val mensajes = if (android.os.Build.VERSION.SDK_INT >= 30) {
            Notification.MessagingStyle.Message.getMessagesFromBundleArray(
                extras.getParcelableArray(Notification.EXTRA_MESSAGES),
            )
        } else emptyList()
        val remitente = mensajes.lastOrNull()?.senderPerson?.name?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val config = ConfigCache.config.value
        val decision = decidirNotificacion(
            sbn.packageName, remitente,
            extras.getBoolean(Notification.EXTRA_IS_GROUP_CONVERSATION), resumen,
            config.appsSilenciadas, config.permitidos.map { it.nombre }, config.whatsappSoloPermitidos,
        )
        if (decision == DecisionNotificacion.CANCELA) {
            cancelNotification(sbn.key)
            scope.launch {
                RegistroRepository(this@FiltroNotificacionesService).agregar(
                    Bloqueo(TipoBloqueo.NOTIFICACION, remitente.orEmpty(), System.currentTimeMillis(), sbn.packageName, sbn.key),
                )
            }
        }
    }
    private fun protegido(paquete: String): Boolean {
        val dialer = getSystemService(TelecomManager::class.java).defaultDialerPackage
        return paquete == packageName || paquete == "android" || paquete == "com.android.systemui" || paquete == dialer
    }
}
