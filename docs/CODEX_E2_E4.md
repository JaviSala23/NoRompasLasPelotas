# Tarea para Codex — E2, E3 y E4

Leé CLAUDE.md y docs/PLAN.md. La app se llama **NoRompasLasPelotas**, paquete
`io.github.javisala23.norompaslaspelotas`. El proyecto ya compila (E0+E1 hechos):
respetá la estructura existente (EstadoRepository, ModoService, EstadoModo, AppPrincipal).
No tenés red: no agregues dependencias nuevas (ya están Compose, Material3, DataStore,
kotlinx-serialization, lifecycle). No hagas commits. Al final actualizá docs/NOTAS_CODEX.md.

## Configuración (data/ConfigRepository.kt)
DataStore + JSON (kotlinx.serialization). Modelo `Config`:
- `permitidos: List<Contacto>` — `Contacto(nombre: String, numeros: List<String>)`.
- `appsSilenciadas: Set<String>` (package names).
- `whatsappSoloPermitidos: Boolean = true`.
- `dejarPasarSiInsiste: Boolean = true` (mismo número 2 veces en 15 min pasa).
- `usarNoMolestarDelSistema: Boolean = true`.
- `appsInicio: List<String>` (packages visibles en la pantalla tranquila, en orden).
Exponer `Flow<Config>` y además un `StateFlow` en memoria (objeto `ConfigCache` iniciado
desde una clase `App : Application`) para que los servicios lean sin esperar.

## E2 — Llamadas (service/FiltroLlamadasService.kt)
- `CallScreeningService` declarado con permiso `BIND_SCREENING_SERVICE` e intent-filter
  `android.telecom.CallScreeningService`.
- Si el modo NO está activo: responder dejando pasar todo.
- Si está activo y la llamada es entrante: pasa sólo si el número coincide con algún
  número permitido. Si no: `setDisallowCall(true)`, `setRejectCall(true)`,
  `setSkipNotification(true)`, `setSkipCallLog(false)`.
- Número oculto/nulo con modo activo: se bloquea.
- Regla "insiste": guardar en DataStore el último intento bloqueado por número normalizado;
  si vuelve a llamar dentro de 15 min y la opción está activa, pasa.
- Lógica pura y testeada en `domain/Telefonos.kt`:
  `mismoNumero(a, b, paisIso)`: quitar todo lo que no sea dígito; si alguno tiene < 7 dígitos,
  comparar exacto; si no, coinciden si los últimos 8 dígitos son iguales (cubre +54 9 11…,
  011 15…, 11…). En el service, además aceptar `PhoneNumberUtils.areSamePhoneNumber` con el país
  de la red. Tests JUnit: "+54 9 11 1234-5678" vs "011 15 1234-5678" vs "1112345678" → iguales;
  "+54 9 351 555-1234" vs "0351 15 555-1234" → iguales; números distintos → no; cortos tipo "911".
  `decidirLlamada(numero, config, intentosPrevios, ahora)` → PASA / BLOQUEA (testeada).
- Pedir el rol con `RoleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)`.

## E3 — Notificaciones (service/FiltroNotificacionesService.kt)
- `NotificationListenerService` (permiso `BIND_NOTIFICATION_LISTENER_SERVICE`).
- Con el modo activo, en `onNotificationPosted` y al activarse (barrer `activeNotifications`):
  - Nunca tocar notificaciones propias, de `android`, `com.android.systemui`, ni el dialer.
  - No cancelar resúmenes de grupo (`FLAG_GROUP_SUMMARY`).
  - Si el package está en `appsSilenciadas` → `cancelNotification(key)`.
  - WhatsApp (`com.whatsapp`, `com.whatsapp.w4b`) con `whatsappSoloPermitidos`: pasa sólo si
    el remitente coincide con el nombre de un contacto permitido. Remitente: último mensaje de
    `EXTRA_MESSAGES` (sender), si no `EXTRA_CONVERSATION_TITLE`, si no `EXTRA_TITLE`.
    Si es grupo (`EXTRA_IS_GROUP_CONVERSATION`) → cancelar. Notificaciones `CATEGORY_CALL`
    de no permitidos → cancelar.
  - Lógica pura testeada en `domain/FiltroNotif.kt`: `mismoNombre` (sin mayúsculas, sin
    acentos, espacios colapsados, ignora emojis) y `decidirNotificacion(...)`.
- La app escucha cambios del modo (ConfigCache/EstadoRepository) y barre al activarse.

## E3 — No molestar del sistema (service/NoMolestarSistema.kt)
- Requiere `ACCESS_NOTIFICATION_POLICY` + `isNotificationPolicyAccessGranted`.
- Al activar el modo (si `usarNoMolestarDelSistema` y hay acceso): guardar en DataStore el
  `currentInterruptionFilter` y la `notificationPolicy` previos; poner política:
  categorías CALLS + REPEAT_CALLERS + ALARMS + MEDIA, `priorityCallSenders = ANY`
  (las llamadas comunes ya vienen filtradas), mensajes NONE, conversaciones NONE;
  y `setInterruptionFilter(INTERRUPTION_FILTER_PRIORITY)`.
- Al desactivar: restaurar exactamente lo guardado.
- La activación/desactivación de llamadas+notificaciones+DND se orquesta desde un único
  lugar (`ModoControl.activar/desactivar(context)`), usado por la UI, el Tile y el BootReceiver.

## E4 — Pantalla tranquila (ui/inicio/)
- `InicioActivity` con intent-filter MAIN + HOME + DEFAULT (no LAUNCHER). Mantener
  MainActivity como la app de configuración (con LAUNCHER).
- Modo activo: fondo liso, hora y fecha grandes, debajo la lista de `appsInicio` como texto
  (sin íconos de colores, sin badges, sin contadores). Abajo, discreto: "Volver al modo normal"
  (un toque, sin confirmaciones culposas) y "Ajustes".
- Modo inactivo: hora + lista alfabética de todas las apps lanzables en texto, con buscador.
- Listar apps con `<queries>` (intent MAIN/LAUNCHER) — NO usar QUERY_ALL_PACKAGES.
- Back no hace nada en la pantalla de inicio (BackHandler), compatible con back predictivo.

## Pantallas de configuración (en MainActivity, navegación simple sin librerías nuevas)
- **Personas que pueden llamarte**: agregar desde contactos con
  `Intent(ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)` (no pedir
  READ_CONTACTS; leer nombre y número del resultado), o escribir nombre y número a mano.
  Si ya existe el nombre, sumar el número. Borrar con un toque.
- **Apps silenciadas**: lista de apps lanzables con checkbox.
- **Apps en la pantalla tranquila**: lista con checkbox.
- **Opciones**: los tres booleanos con una línea de explicación cada uno.
- **Permisos**: notificaciones, filtro de llamadas (rol), acceso a notificaciones, acceso a
  No molestar, pantalla de inicio predeterminada (abrir `Settings.ACTION_HOME_SETTINGS`).
  Cada uno muestra si está listo (✓) y un botón para ir a Ajustes. Refrescar en onResume.

## Extras
- Tile de Ajustes rápidos (`TileService`) "Modo tranquilo" que activa/desactiva con ModoControl.
- `allowBackup="false"` y `dataExtractionRules` que excluyan todo (la lista de personas no
  tiene que salir del celu).
- Textos en español rioplatense, cortos, sin culpa ni urgencia.
- Actualizá README.md: cómo usarla (pasos de permisos), límites conocidos (WhatsApp sólo se
  silencia, filtra por nombre agendado; puede sonar un instante una llamada de WhatsApp de
  alguien no permitido antes de cortarse).
- Tests JUnit para toda la lógica pura nueva.
