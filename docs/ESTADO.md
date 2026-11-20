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
