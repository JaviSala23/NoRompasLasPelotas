package io.github.javisala23.norompaslaspelotas.service

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.data.Arranque
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ModoTileService : TileService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onStartListening() { super.onStartListening(); actualizar() }
    override fun onClick() {
        super.onClick()
        scope.launch { Arranque.listo.await(); ModoControl.cambiar(this@ModoTileService, !EstadoCache.activo.value); actualizar() }
    }
    private fun actualizar() {
        qsTile?.state = if (EstadoCache.activo.value) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile?.label = getString(io.github.javisala23.norompaslaspelotas.R.string.quiet_mode)
        qsTile?.updateTile()
    }
}
