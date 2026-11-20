package io.github.javisala23.norompaslaspelotas.domain
import org.junit.Assert.*
import org.junit.Test
class FiltroNotifTest {
 @Test fun `normaliza nombres`() = assertTrue(mismoNombre("  JOSÉ  Pérez 😊", "jose perez"))
 @Test fun `whatsapp permitido y grupo`() { assertEquals(DecisionNotificacion.PASA, decidirNotificacion("com.whatsapp","Ana",false,false,emptySet(),listOf("ana"),true)); assertEquals(DecisionNotificacion.CANCELA, decidirNotificacion("com.whatsapp","Ana",true,false,emptySet(),listOf("Ana"),true)) }
 @Test fun `silenciada y resumen`() { assertEquals(DecisionNotificacion.CANCELA, decidirNotificacion("x",null,false,false,setOf("x"),emptyList(),false)); assertEquals(DecisionNotificacion.PASA, decidirNotificacion("x",null,false,true,setOf("x"),emptyList(),false)) }
}
