package io.github.javisala23.norompaslaspelotas.domain

import io.github.javisala23.norompaslaspelotas.data.Config

enum class DecisionLlamada { PASA, BLOQUEA }

fun digitos(numero: String): String = numero.filter(Char::isDigit)

fun mismoNumero(a: String, b: String, paisIso: String = "AR"): Boolean {
    val uno = digitos(a)
    val dos = digitos(b)
    if (uno.isEmpty() || dos.isEmpty()) return false
    if (uno.length < 7 || dos.length < 7) return uno == dos
    val nacionalUno = nacionalArgentino(uno)
    val nacionalDos = nacionalArgentino(dos)
    if (nacionalUno.length == 10 && nacionalDos.length == 10) return nacionalUno == nacionalDos
    return uno.takeLast(7) == dos.takeLast(7)
}

/** Lleva variantes argentinas (+54 9, 0, 15) al formato nacional de 10 dígitos cuando se puede. */
fun nacionalArgentino(soloDigitos: String): String {
    var d = soloDigitos
    if (d.startsWith("54") && d.length >= 12) d = d.drop(2)
    if (d.startsWith("9") && d.length == 11) d = d.drop(1)
    if (d.startsWith("0")) d = d.drop(1)
    if (d.length == 12) {
        for (area in 2..4) {
            if (d.substring(area, area + 2) == "15") return d.removeRange(area, area + 2)
        }
    }
    return d
}

fun decidirLlamada(
    numero: String?,
    config: Config,
    intentosPrevios: Map<String, Long>,
    ahora: Long,
    paisIso: String = "AR",
): DecisionLlamada {
    if (numero.isNullOrBlank()) return DecisionLlamada.BLOQUEA
    if (config.permitidos.any { contacto -> contacto.numeros.any { mismoNumero(numero, it, paisIso) } }) {
        return DecisionLlamada.PASA
    }
    if (config.dejarPasarSiInsiste) {
        val anterior = intentosPrevios.entries.firstOrNull { mismoNumero(numero, it.key, paisIso) }?.value
        if (anterior != null && ahora - anterior in 0..QUINCE_MINUTOS) return DecisionLlamada.PASA
    }
    return DecisionLlamada.BLOQUEA
}

const val QUINCE_MINUTOS = 15 * 60 * 1000L
