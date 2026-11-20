package io.github.javisala23.norompaslaspelotas.domain

/** Lógica de estado pura: no depende de Android y se prueba en la JVM. */
data class ResultadoEstado(
    val activo: Boolean,
    val debeEjecutarServicio: Boolean,
)

object EstadoModo {
    fun cambiar(actual: Boolean, solicitado: Boolean): ResultadoEstado =
        if (actual == solicitado) {
            ResultadoEstado(activo = actual, debeEjecutarServicio = actual)
        } else {
            ResultadoEstado(activo = solicitado, debeEjecutarServicio = solicitado)
        }

    fun restaurarAlArrancar(activoGuardado: Boolean): ResultadoEstado =
        ResultadoEstado(
            activo = activoGuardado,
            debeEjecutarServicio = activoGuardado,
        )
}
