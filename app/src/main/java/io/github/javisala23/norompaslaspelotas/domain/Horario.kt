package io.github.javisala23.norompaslaspelotas.domain

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

/**
 * Horario automático. [dias] usa DayOfWeek ISO (1 = lunes … 7 = domingo) y marca el día en que
 * EMPIEZA cada franja. [inicio] y [fin] son minutos desde medianoche; si fin <= inicio la franja
 * termina al día siguiente (fin == inicio = 24 horas).
 */
@Serializable
data class Horario(
    val activo: Boolean = false,
    val dias: Set<Int> = setOf(6, 7),
    val inicio: Int = 22 * 60,
    val fin: Int = 8 * 60,
) {
    val duracionMinutos: Int get() = if (fin > inicio) fin - inicio else fin + 24 * 60 - inicio
    val terminaAlDiaSiguiente: Boolean get() = fin <= inicio
    /** Todos los días, 24 horas: nunca se apaga solo. */
    val siempre: Boolean get() = dias.size == 7 && duracionMinutos == 24 * 60
}

data class CambioHorario(val momento: LocalDateTime, val activar: Boolean)

/** Franjas alrededor de [ahora], ordenadas y con las pegadas/superpuestas unidas en una sola. */
private fun franjas(h: Horario, ahora: LocalDateTime): List<Pair<LocalDateTime, LocalDateTime>> {
    val sueltas = (-1L..15L).map { ahora.toLocalDate().plusDays(it) }
        .filter { it.dayOfWeek.value in h.dias }
        .map { dia ->
            val desde = dia.atStartOfDay().plusMinutes(h.inicio.toLong())
            desde to desde.plusMinutes(h.duracionMinutos.toLong())
        }
    val unidas = mutableListOf<Pair<LocalDateTime, LocalDateTime>>()
    for (franja in sueltas) {
        val ultima = unidas.lastOrNull()
        if (ultima != null && !franja.first.isAfter(ultima.second)) {
            unidas[unidas.lastIndex] = ultima.first to maxOf(ultima.second, franja.second)
        } else unidas += franja
    }
    return unidas
}

/** Próximo momento (estrictamente después de [ahora]) en que el horario prende o apaga. */
fun proximoCambio(h: Horario, ahora: LocalDateTime): CambioHorario? {
    if (!h.activo || h.dias.isEmpty() || h.siempre) return null
    return franjas(h, ahora)
        .flatMap { (desde, hasta) -> listOf(CambioHorario(desde, true), CambioHorario(hasta, false)) }
        .filter { it.momento.isAfter(ahora) }
        .minByOrNull { it.momento }
}

fun dentroDelHorario(h: Horario, momento: LocalDateTime): Boolean =
    h.activo && (h.siempre || franjas(h, momento).any { (desde, hasta) -> !momento.isBefore(desde) && momento.isBefore(hasta) })
