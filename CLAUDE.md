# NoRompasLasPelotas (antes "Calma" y "No Molestar")

App Android libre (GPL-3.0) para días de descanso: filtra llamadas, notificaciones
y deja una pantalla de inicio tranquila. Plan completo en `docs/PLAN.md`, estado en
`docs/ESTADO.md` (leer ambos antes de tocar nada).

## Reglas
- Claude planifica y audita; Codex implementa. Nombrar a Javi en cada respuesta.
- Nunca agregar el permiso `INTERNET` ni librerías de tracking/Play Services.
- Nada de dark patterns (ver Principios en PLAN.md).
- Repo público: nunca commitear keystores, `local.properties` ni secretos.
- Codex no tiene red: no puede bajar dependencias de Gradle. Los builds y tests
  los corre Claude afuera del sandbox.
- Debe andar en Android 10+ y en las versiones más nuevas (targetSdk 36).
