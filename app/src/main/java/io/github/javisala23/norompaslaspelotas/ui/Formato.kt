package io.github.javisala23.norompaslaspelotas.ui

import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

private val formatoHora = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

fun horaTexto(minutos: Int): String = LocalTime.of(minutos / 60, minutos % 60).format(formatoHora)

fun horaTexto(hora: LocalTime): String = hora.format(formatoHora)

fun diaCorto(dia: Int): String = DayOfWeek.of(dia).getDisplayName(TextStyle.SHORT, Locale.getDefault()).trimEnd('.')

fun diaLargo(dia: Int): String = DayOfWeek.of(dia).getDisplayName(TextStyle.FULL, Locale.getDefault())

fun diaInicial(dia: Int): String = DayOfWeek.of(dia).getDisplayName(TextStyle.NARROW, Locale.getDefault()).uppercase()

/** "lun a vie", "sáb y dom" o la lista corta separada por comas. */
fun diasTexto(dias: Set<Int>): String {
    val ordenados = dias.sorted()
    return when {
        ordenados.size == 7 -> "${diaCorto(1)} – ${diaCorto(7)}"
        ordenados.size >= 3 && ordenados.last() - ordenados.first() == ordenados.size - 1 ->
            "${diaCorto(ordenados.first())} – ${diaCorto(ordenados.last())}"
        else -> ordenados.joinToString(", ") { diaCorto(it) }
    }
}
