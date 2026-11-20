package io.github.javisala23.norompaslaspelotas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadoModoTest {
    @Test
    fun `prender activa el estado y el servicio`() {
        val resultado = EstadoModo.cambiar(actual = false, solicitado = true)

        assertTrue(resultado.activo)
        assertTrue(resultado.debeEjecutarServicio)
    }

    @Test
    fun `apagar desactiva el estado y el servicio`() {
        val resultado = EstadoModo.cambiar(actual = true, solicitado = false)

        assertFalse(resultado.activo)
        assertFalse(resultado.debeEjecutarServicio)
    }

    @Test
    fun `repetir el estado es idempotente`() {
        assertEquals(
            ResultadoEstado(activo = true, debeEjecutarServicio = true),
            EstadoModo.cambiar(actual = true, solicitado = true),
        )
    }

    @Test
    fun `el arranque restaura solamente un modo guardado como activo`() {
        assertTrue(EstadoModo.restaurarAlArrancar(true).debeEjecutarServicio)
        assertFalse(EstadoModo.restaurarAlArrancar(false).debeEjecutarServicio)
    }
}
