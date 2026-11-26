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

## 2026-11-20 (cierre)
- E2-E4 hechos (Codex) + auditoría Claude: arreglado matching de números AR (+54 9 / 0 / 15),
  carrera al arrancar en frío (Arranque.listo), guardas de API 29/30, FGS que no rompe el filtrado.
- Carpeta renombrada a ~/Documentos/code/NoRompasLasPelotas.
- Repo público: https://github.com/JaviSala23/NoRompasLasPelotas
- Release v0.1.0 (prerelease, APK firmado 2.4 MB con R8).
- Release local: NRLP_KEYSTORE=~/android-tools/claves/norompas-release.jks NRLP_PASSWORD=$(cat ~/android-tools/claves/clave.txt) ./gradlew assembleRelease
- SIN probar en dispositivo (no hay KVM para emulador). Pendiente: prueba real de Javi
  (llamada común permitida/no permitida, insistencia, WhatsApp mensaje y llamada, launcher, tile).

## 2026-11-20 (v0.2.0)
- Javi instaló 0.1.0 (Play Protect la frenaba: README explica cómo). Bug: rol de filtro de llamadas
  se pedía con startActivity → arreglado con ActivityResult + ayuda a Apps predeterminadas.
- v0.2.0 publicada: rediseño (Codex, spec docs/CODEX_V0_2.md) + arreglos de auditoría (modifier que
  descartaba clickable, recurso del manifest con variante en inglés).
- Pendiente: que Javi pruebe 0.2.0 en el celu (rol de llamadas, WhatsApp, launcher).

## 2026-11-23 (v0.2.1)
- Ya no hay Codex: Claude implementa directo.
- Arreglo: el filtro de notificaciones nunca cancela CATEGORY_ALARM (aunque el Reloj esté silenciado).
  El DND del sistema ya dejaba pasar alarmas. Sin probar en dispositivo.
- v0.2.1 publicada: https://github.com/JaviSala23/NoRompasLasPelotas/releases/tag/v0.2.1

## 2026-11-23 (v0.3.0 en curso — UI/UX)
Javi pidió las 4: (1) Inicio más claro: tarjeta de estado grande que cambia de color, qué está
pasando en palabras, filas con el valor actual, grupos. (2) Registro "Lo que frené" (local, 7 días,
DataStore registro_bloqueos). (3) Horario: días + desde/hasta, AlarmManager inexacto
(setAndAllowWhileIdle, sin permiso de alarmas exactas), HorarioReceiver; al empezar prende, al
terminar apaga. (4) Pulir: apps cargan en segundo plano con secciones, personas con vacío mejor.
- HECHO y publicada como pre-release v0.3.0 (code 4): Inicio nuevo (tarjeta de estado,
  grupos con valores), RegistroScreen, HorarioScreen + Programador/HorarioReceiver, paleta completa
  (antes M3 metía lila en las tarjetas), apps en segundo plano. Build+tests (HorarioTest) +lint OK.
  Sin probar en dispositivo. Pendiente: que Javi lo pruebe.

## 2026-11-24 (v0.3.1 — pantalla tranquila "detox")
- Pedido de Javi: pantalla negra con apps en texto blanco y efectos; referencia: launcher minimalista
  de yankodesign (hora + pocas apps, deslizar a todas, ocultar adictivas, color de fondo).
- Hecho en InicioActivity: fondo liso elegible (Negro/Grafito/Bosque/Arena, en Opciones), cascada al
  entrar, punto que respira, deslizar hacia arriba → todas las apps; con el modo activo las silenciadas
  se ocultan y sólo aparecen buscándolas, con pausa de 5 s ("Respirá") antes de abrirlas.
- Publicada pre-release v0.3.1. Sin probar en dispositivo.

## 2026-11-26 (v0.3.2 — aceptar íconos de otras apps)
- Problema de Javi: con NoRompasLasPelotas como inicio predeterminado, Chrome no ofrecía "Instalar app"
  ni "Agregar a la pantalla principal" (la PWA de AGL Balanza). Causa: el inicio no tenía actividad para
  CONFIRM_PIN_SHORTCUT, entonces isRequestPinShortcutSupported() = false y Chrome esconde esas opciones.
- FijarAtajoActivity (translúcida) acepta los pedidos de atajos (no widgets) con un toast. InicioActivity
  lista también los atajos fijados (LauncherApps FLAG_MATCH_PINNED) y los abre con startShortcut. Las
  WebAPK de Chrome ya aparecían como apps normales.
- Pendiente: quitar un atajo fijado desde el inicio (hoy no se puede). Sin probar en dispositivo.
- Publicada pre-release v0.3.2: https://github.com/JaviSala23/NoRompasLasPelotas/releases/tag/v0.3.2
- Javi lo probó en su celu: con 0.3.2 Chrome ya ofrece instalar la PWA. Confirmado.
