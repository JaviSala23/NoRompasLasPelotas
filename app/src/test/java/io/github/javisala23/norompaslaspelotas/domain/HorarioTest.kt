package io.github.javisala23.norompaslaspelotas.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class HorarioTest {
    // 2026-11-21 es sábado.
    private val sabado = LocalDateTime.of(2026, 11, 21, 12, 0)
    private val finde = Horario(activo = true, dias = setOf(6, 7), inicio = 22 * 60, fin = 8 * 60)

    @Test fun apagadoNoTieneCambios() {
        assertNull(proximoCambio(finde.copy(activo = false), sabado))
        assertNull(proximoCambio(finde.copy(dias = emptySet()), sabado))
    }

    @Test fun prendeElSabadoALasDiez() {
        assertEquals(CambioHorario(LocalDateTime.of(2026, 11, 21, 22, 0), true), proximoCambio(finde, sabado))
    }

    @Test fun cruzaMedianoche() {
        val noche = LocalDateTime.of(2026, 11, 21, 23, 0)
        assertTrue(dentroDelHorario(finde, noche))
        assertEquals(CambioHorario(LocalDateTime.of(2026, 11, 22, 8, 0), false), proximoCambio(finde, noche))
        // El domingo a la madrugada sigue adentro por la franja del sábado.
        assertTrue(dentroDelHorario(finde, LocalDateTime.of(2026, 11, 22, 3, 0)))
        // El lunes a la madrugada sigue adentro por la franja del domingo.
        assertTrue(dentroDelHorario(finde, LocalDateTime.of(2026, 11, 23, 7, 59)))
        assertFalse(dentroDelHorario(finde, LocalDateTime.of(2026, 11, 23, 8, 0)))
    }

    @Test fun mismoDia() {
        val siesta = Horario(activo = true, dias = setOf(7), inicio = 14 * 60, fin = 17 * 60)
        assertEquals(CambioHorario(LocalDateTime.of(2026, 11, 22, 14, 0), true), proximoCambio(siesta, sabado))
        assertEquals(
            CambioHorario(LocalDateTime.of(2026, 11, 22, 17, 0), false),
            proximoCambio(siesta, LocalDateTime.of(2026, 11, 22, 14, 0)),
        )
    }

    @Test fun franjasDe24HorasPegadasNoApaganEnElMedio() {
        val todoElFinde = Horario(activo = true, dias = setOf(6, 7), inicio = 0, fin = 0)
        val cambio = proximoCambio(todoElFinde, sabado)
        assertEquals(CambioHorario(LocalDateTime.of(2026, 11, 23, 0, 0), false), cambio)
    }
}
