package io.github.javisala23.norompaslaspelotas.domain

import java.text.Normalizer

enum class DecisionNotificacion { PASA, CANCELA }

fun normalizarNombre(nombre: String): String = Normalizer.normalize(nombre, Normalizer.Form.NFD)
    .replace(Regex("\\p{M}+"), "")
    .lowercase()
    .replace(Regex("[^\\p{L}\\p{N}\\s]"), " ")
    .trim()
    .replace(Regex("\\s+"), " ")

fun mismoNombre(a: String?, b: String?): Boolean =
    !a.isNullOrBlank() && !b.isNullOrBlank() && normalizarNombre(a) == normalizarNombre(b)

fun decidirNotificacion(
    paquete: String,
    remitente: String?,
    esGrupo: Boolean,
    esResumenGrupo: Boolean,
    appsSilenciadas: Set<String>,
    nombresPermitidos: Collection<String>,
    whatsappSoloPermitidos: Boolean,
): DecisionNotificacion {
    if (esResumenGrupo) return DecisionNotificacion.PASA
    if (paquete in appsSilenciadas) return DecisionNotificacion.CANCELA
    if (whatsappSoloPermitidos && paquete in PAQUETES_WHATSAPP) {
        if (esGrupo) return DecisionNotificacion.CANCELA
        return if (nombresPermitidos.any { mismoNombre(remitente, it) }) {
            DecisionNotificacion.PASA
        } else DecisionNotificacion.CANCELA
    }
    return DecisionNotificacion.PASA
}

val PAQUETES_WHATSAPP = setOf("com.whatsapp", "com.whatsapp.w4b")
