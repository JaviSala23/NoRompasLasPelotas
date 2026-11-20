package io.github.javisala23.norompaslaspelotas.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.data.Arranque
import io.github.javisala23.norompaslaspelotas.data.IntentosRepository
import io.github.javisala23.norompaslaspelotas.domain.DecisionLlamada
import io.github.javisala23.norompaslaspelotas.domain.decidirLlamada
import io.github.javisala23.norompaslaspelotas.domain.digitos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FiltroLlamadasService : CallScreeningService() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override fun onScreenCall(details: Call.Details) {
        if (details.callDirection != Call.Details.DIRECTION_INCOMING) {
            respondToCall(details, CallResponse.Builder().build()); return
        }
        scope.launch {
            Arranque.listo.await()
            if (!EstadoCache.activo.value) { respondToCall(details, CallResponse.Builder().build()); return@launch }
            val numero = details.handle?.schemeSpecificPart
            val config = ConfigCache.config.value
            val repo = IntentosRepository(this@FiltroLlamadasService)
            val intentos = repo.leer()
            val pais = getSystemService(TelephonyManager::class.java).networkCountryIso.ifBlank { "AR" }.uppercase()
            val permitidoAndroid = numero != null && config.permitidos.any { contacto ->
                contacto.numeros.any { mismoNumeroAndroid(numero, it, pais) }
            }
            val decision = if (permitidoAndroid) DecisionLlamada.PASA
                else decidirLlamada(numero, config, intentos, System.currentTimeMillis(), pais)
            if (decision == DecisionLlamada.BLOQUEA && numero != null) repo.guardar(digitos(numero), System.currentTimeMillis())
            respondToCall(details, if (decision == DecisionLlamada.PASA) CallResponse.Builder().build() else
                CallResponse.Builder().setDisallowCall(true).setRejectCall(true)
                    .setSkipNotification(true).setSkipCallLog(false).build())
        }
    }
}

private fun mismoNumeroAndroid(a: String, b: String, pais: String): Boolean =
    if (android.os.Build.VERSION.SDK_INT >= 31) PhoneNumberUtils.areSamePhoneNumber(a, b, pais)
    else @Suppress("DEPRECATION") PhoneNumberUtils.compare(a, b)
