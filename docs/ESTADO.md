# Estado — Calma

## 2026-11-20
- Decidido: nombre **Calma**, gratis, GPL-3.0, repo público (todavía sin crear en GitHub).
- Celu de Javi: Android 14. Llamadas: **sólo pasa una lista de permitidos**.
- Plan escrito en `docs/PLAN.md`.
- Máquina: no hay Java ni Android SDK; se instalan en `~/android-tools` (sin sudo).
- Siguiente: E0 + E1 con Codex.
- Cambio: la app se llama **No Molestar** (paquete io.github.javisala23.nomolestar).
  Codex arrancó E0/E1 con el nombre viejo "Calma": renombrar al auditar.
- Tiene que andar en todos los celus nuevos: minSdk 29, targetSdk/compileSdk 36.
- Confirmado: llamadas = sólo pasa la lista de permitidos, se bloquea el resto.
- Cambio 2: la app se llama **NoRompasLasPelotas** (paquete io.github.javisala23.norompaslaspelotas).
  Codex está haciendo E0/E1 con "No Molestar": renombrar al auditar (nombre visible, paquete,
  rootProject, carpeta del repo).
- Si se corta la cuota de Claude o Codex: seguir con OpenCode / modelo local (ollama) leyendo este archivo y PLAN.md.
- Avances a Javi por Telegram con `kiro-avisar "texto"` (~/.local/bin, usa el bot de Kiro).

## 2026-11-20 (tarde)
- E0+E1 HECHOS y compilando (commit e952dd0). Renombrado a NoRompasLasPelotas.
- Build: `JAVA_HOME=~/android-tools/jdk ANDROID_HOME=~/android-tools/sdk ./gradlew assembleDebug testDebugUnitTest lint`
- E2-E4 encargados a Codex: especificación en docs/CODEX_E2_E4.md.
- Firma de release: keystore en ~/android-tools/claves/ (FUERA del repo, con clave.txt).
  ¡Hacer backup! Si se pierde, las actualizaciones no se pueden instalar encima.
- Pendiente E5: build release firmado, crear repo público JaviSala23/NoRompasLasPelotas,
  GitHub Release con el APK, avisar link por kiro-avisar.
