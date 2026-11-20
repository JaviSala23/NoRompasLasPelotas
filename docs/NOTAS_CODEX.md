# Notas de Codex — E2, E3 y E4

Fecha: 2026-11-20

Se implementaron configuración JSON en DataStore y cache en memoria, filtrado de
llamadas con regla de insistencia, filtrado de notificaciones/WhatsApp, resguardo y
restauración de No molestar, coordinación central en `ModoControl`, Tile, launcher
tranquilo y las pantallas de configuración y permisos.

También se deshabilitó backup y transferencia, se agregaron queries acotadas para
apps lanzables, se actualizó el README y se sumaron tests JVM para teléfonos,
decisiones de llamada, nombres y notificaciones.

Validación manual pendiente en un teléfono: rol de llamadas, variantes de
notificación de WhatsApp, restauración DND y elección del launcher.

No se pudo ejecutar Gradle dentro del sandbox: aun con caché local escribible,
`--offline` y `--no-daemon`, Gradle 8.14.3 no inicia su manejador de locks porque
el entorno no expone una IP wildcard utilizable. `git diff --check` sí quedó limpio.

## v0.2.0 — arreglos y rediseño

Se corrigió la solicitud del rol de filtro de llamadas usando un launcher que recibe
el resultado y ofrece una ruta alternativa a Apps predeterminadas. La tarjeta de
acceso a notificaciones ahora explica cómo habilitar “Configuración restringida” en
Android 13+, y abre directamente la información de la app. Borrar una persona usa
un botón dedicado y permite deshacer desde un Snackbar; las keys de la lista ya no
dependen sólo del nombre. La notificación fija resume personas y apps silenciadas.

La interfaz se reorganizó en un archivo por pantalla, con tarjetas planas, cabeceras
Material 3, botón central animado, saludo por horario y estado resumido. Se agregó
onboarding de tres pasos persistido en `Config`, sugerencias iniciales de redes
sociales, buscadores e iconos reales en las listas de apps, pantalla de permisos con
progreso y una sección Acerca de.

El launcher tranquilo ahora diferencia con claridad el modo activo y el normal: el
primero prioriza reloj, fecha, frase calma y apps elegidas; el segundo incorpora
búsqueda, secciones alfabéticas e iconos desaturados. La búsqueda se limpia al volver.
También se agregó icono adaptativo con capa monocroma, traducción inglesa completa,
versionCode 2 y versionName 0.2.0.

Por indicación de la tarea no se ejecutó Gradle ni se compiló. Queda pendiente la
compilación y prueba en dispositivo de los flujos de roles, Ajustes y launcher.
