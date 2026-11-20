# NoRompasLasPelotas — plan

App Android libre y gratuita (GPL-3.0). Un botón: al prenderla, el celu entra en
"modo tranquilo"; al apagarla, vuelve todo como estaba.

## Principios (no negociables)
- Sin permiso `INTERNET`. Nada sale del celu. Sin cuentas, sin anuncios, sin analytics.
- Sin dark patterns: sin rachas, sin contadores, sin badges, sin colores de alerta,
  sin notificaciones "motivacionales". Textos cortos y amables.
- Todo se apaga limpio: al salir del modo calma se restaura lo que había (DND, etc.).
- Dependencias mínimas (AndroidX + Compose). Nada de Google Play Services
  (para poder entrar a F-Droid).

## Técnica
- Kotlin + Jetpack Compose, Material 3 con paleta propia apagada (sin dynamic color).
- `applicationId`: `io.github.javisala23.norompaslaspelotas`. minSdk 29 (Android 10 en adelante), targetSdk y compileSdk 36 (Android 16); probar también en la última versión disponible.
- Celus de referencia: Android 14 (Javi) y emulador con la última versión.
- Persistencia: DataStore (Preferences) + JSON simple con kotlinx.serialization.
- Build y APK de release por GitHub Actions (gratis en repo público).

## Comportamiento en modo calma
1. **Llamadas comunes** — `CallScreeningService` (rol `ROLE_CALL_SCREENING`).
   Pasa sólo si el número está en la lista de permitidos
   (`PhoneNumberUtils.areSamePhoneNumber` con país "AR"). El resto: `disallowCall`,
   `rejectCall`, `skipNotification`; queda en el registro de llamadas.
   Opción (activada por defecto): si el mismo número insiste 2 veces en 15 min,
   pasa (por si es una urgencia). Los números de emergencia el sistema no los filtra.
2. **Notificaciones** — `NotificationListenerService`. Cancela las notificaciones de
   las apps marcadas como "silenciadas". Para WhatsApp/WhatsApp Business: deja pasar
   sólo si el título coincide con el nombre de un contacto permitido.
   Las notificaciones de llamada de WhatsApp (`CATEGORY_CALL`) de no permitidos se cancelan.
3. **No molestar** — con `ACCESS_NOTIFICATION_POLICY`: guarda el estado previo y
   activa prioridad (alarmas sí; llamadas sólo de favoritos con estrella; repetidas sí).
   **A verificar en el celu (Etapa 3):** si una llamada de WhatsApp de un permitido suena
   con DND; si no, documentar que hay que marcarlo con estrella.
4. **Inicio calmo** — Calma se registra como launcher (HOME). Modo calma: fondo liso,
   hora, y sólo las apps que Javi eligió, en texto, sin íconos de colores ni badges.
   Modo normal: lista simple de todas las apps (o botón para abrir el launcher anterior).
5. **Por detrás** — servicio en primer plano con notificación fija y discreta
   ("Calma activa"), y `BOOT_COMPLETED` para volver a encenderse si estaba activa.

## Límites conocidos (van al README)
- Llamadas de WhatsApp: se silencian, no se rechazan (al otro le suena).
- WhatsApp no expone números: el filtro es por nombre agendado.
- El launcher se elige una vez a mano en Ajustes.

## Etapas
- **E0 — Esqueleto:** proyecto Gradle (Kotlin DSL, version catalog), Compose, tema calmo,
  LICENSE GPL-3.0, README, workflow de GitHub Actions que compila el APK debug.
  ✔ Aceptación: `./gradlew assembleDebug lint` en verde; manifest sin `INTERNET`.
- **E1 — Interruptor y servicio:** botón grande on/off, estado en DataStore,
  servicio en primer plano, arranque al boot, pantalla de permisos (guía paso a paso,
  sin insistir). ✔ Aceptación: tests unitarios del estado; sobrevive reinicio.
- **E2 — Llamadas:** lista de permitidos (elegir desde contactos o escribir número),
  `CallScreeningService`, regla de "insiste 2 veces". ✔ Aceptación: tests unitarios
  de la decisión (números con/sin +54, 9, 0, 15); prueba real en el celu.
- **E3 — Notificaciones y DND:** elegir apps a silenciar, filtro de WhatsApp por
  contacto, DND con restauración. ✔ Aceptación: tests de la regla de filtrado;
  prueba real con WhatsApp.
- **E4 — Inicio calmo:** launcher, elegir apps visibles. ✔ Aceptación: prueba real.
- **E5 — Publicación:** firma de release (keystore fuera del repo, en secrets),
  GitHub Release con APK, README con capturas, metadatos para F-Droid.
