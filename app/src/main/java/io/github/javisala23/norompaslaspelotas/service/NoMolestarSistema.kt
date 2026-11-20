package io.github.javisala23.norompaslaspelotas.service

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import io.github.javisala23.norompaslaspelotas.data.DndRepository
import io.github.javisala23.norompaslaspelotas.data.aEstado

object NoMolestarSistema {
    suspend fun activar(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (!manager.isNotificationPolicyAccessGranted) return
        val repository = DndRepository(context)
        if (repository.leer() == null) repository.guardar(manager.notificationPolicy.aEstado(manager.currentInterruptionFilter))
        val categorias = NotificationManager.Policy.PRIORITY_CATEGORY_CALLS or
            NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS or
            NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS or
            NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA
        val politica = if (Build.VERSION.SDK_INT >= 30) {
            NotificationManager.Policy(
                categorias,
                NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                NotificationManager.Policy.PRIORITY_SENDERS_STARRED, // ignorado: MESSAGES no está en las categorías
                0,
                NotificationManager.Policy.CONVERSATION_SENDERS_NONE,
            )
        } else {
            @Suppress("DEPRECATION")
            NotificationManager.Policy(
                categorias,
                NotificationManager.Policy.PRIORITY_SENDERS_ANY,
                NotificationManager.Policy.PRIORITY_SENDERS_STARRED, // ignorado: MESSAGES no está en las categorías
                0,
            )
        }
        manager.notificationPolicy = politica
        manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
    }

    suspend fun desactivar(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (!manager.isNotificationPolicyAccessGranted) return
        val repository = DndRepository(context)
        val estado = repository.leer() ?: return
        val politica = if (Build.VERSION.SDK_INT >= 30) NotificationManager.Policy(
            estado.categorias, estado.llamadas, estado.mensajes, estado.efectosSuprimidos, estado.conversaciones,
        ) else {
            @Suppress("DEPRECATION")
            NotificationManager.Policy(estado.categorias, estado.llamadas, estado.mensajes, estado.efectosSuprimidos)
        }
        manager.notificationPolicy = politica
        manager.setInterruptionFilter(estado.filtro)
        repository.borrar()
    }
}
