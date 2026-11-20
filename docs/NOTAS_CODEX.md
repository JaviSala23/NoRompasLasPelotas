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
