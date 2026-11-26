package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.app.Activity
import android.content.pm.LauncherApps
import android.os.Bundle
import android.widget.Toast
import io.github.javisala23.norompaslaspelotas.R

/**
 * Recibe los pedidos de "agregar a la pantalla de inicio" de otras apps (Chrome: instalar una
 * página o agregarla al inicio). Sin esta pantalla, Android avisa que el inicio no acepta íconos
 * y Chrome esconde esas opciones. Acepta el atajo y queda en la lista de apps.
 */
class FijarAtajoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pedido = runCatching { getSystemService(LauncherApps::class.java)?.getPinItemRequest(intent) }.getOrNull()
        if (pedido != null && pedido.isValid && pedido.requestType == LauncherApps.PinItemRequest.REQUEST_TYPE_SHORTCUT) {
            val nombre = pedido.shortcutInfo?.shortLabel ?: ""
            if (runCatching { pedido.accept() }.getOrDefault(false)) {
                Toast.makeText(this, getString(R.string.shortcut_added, nombre), Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }
}
