package io.github.javisala23.norompaslaspelotas.domain
import io.github.javisala23.norompaslaspelotas.data.*
import org.junit.Assert.*
import org.junit.Test
class TelefonosTest {
 @Test fun `variantes portenas coinciden`() { assertTrue(mismoNumero("+54 9 11 1234-5678", "011 15 1234-5678")); assertTrue(mismoNumero("011 15 1234-5678", "1112345678")) }
 @Test fun `variantes cordobesas coinciden`() = assertTrue(mismoNumero("+54 9 351 555-1234", "0351 15 555-1234"))
 @Test fun `distintos y cortos`() { assertFalse(mismoNumero("3515551234", "3515559999")); assertTrue(mismoNumero("911", "911")); assertFalse(mismoNumero("911", "0911")) }
 @Test fun `decide permitidos insistencia y bloqueos`() { val c = Config(permitidos=listOf(Contacto("Ana", listOf("3515551234"))))
  assertEquals(DecisionLlamada.PASA, decidirLlamada("+54 9 351 5551234", c, emptyMap(), 1000))
  assertEquals(DecisionLlamada.PASA, decidirLlamada("3514440000", c, mapOf("03514440000" to 1000), 14*60*1000L))
  assertEquals(DecisionLlamada.BLOQUEA, decidirLlamada(null, Config(), emptyMap(), 0))
  assertEquals(DecisionLlamada.BLOQUEA, decidirLlamada("3514440000", Config(), emptyMap(), 0)) }
 @Test fun `misma terminacion distinta zona no coincide`() = assertFalse(mismoNumero("3515551234", "2615551234"))
}
