# NoRompasLasPelotas

NoRompasLasPelotas es una app Android libre para bajar el ruido del teléfono durante un
rato. Filtra llamadas y notificaciones, configura No molestar y ofrece una pantalla
de inicio tranquila. Todo se puede apagar con un toque.

## Descargar

Bajá el último `.apk` desde
[Releases](https://github.com/JaviSala23/NoRompasLasPelotas/releases/latest),
abrilo en el celu y aceptá "instalar apps desconocidas" para el navegador.
Funciona en Android 10 o más nuevo. Es gratis y lo va a ser siempre.

### Si Google Play Protect la bloquea

Play Protect frena las apps que no vienen de Play Store y que filtran llamadas o
leen notificaciones (justo lo que hace esta, sin Internet y con el código a la vista).

1. Play Store → tu foto → **Play Protect** → ⚙️ → desactivá
   **"Analizar apps con Play Protect"**.
2. Instalá el `.apk`.
3. Volvé a activar el análisis.

Alternativa desde una compu: `adb install NoRompasLasPelotas-*.apk` con el celu
conectado por USB y la depuración USB activada.

### Si Android dice "Configuración restringida"

Al dar acceso a notificaciones: Ajustes → Apps → NoRompasLasPelotas → menú ⋮ →
**"Permitir configuración restringida"**, y volvé a intentarlo.

## Principios

- Todo queda en el teléfono: la app no pide acceso a Internet.
- Sin cuentas, anuncios, analytics ni Google Play Services.
- Sin rachas, urgencias inventadas ni diálogos insistentes.
- Interfaz breve, tranquila y accesible, en claro y oscuro.
- Código libre bajo GPL-3.0.

## Cómo usarla

1. Abrí **Permisos** y habilitá notificaciones, filtro de llamadas, acceso a
   notificaciones y No molestar.
2. Elegí NoRompasLasPelotas como pantalla de inicio predeterminada.
3. Agregá las personas permitidas y elegí las apps a silenciar o mostrar.
4. Activá el modo desde la app o desde el Tile de Ajustes rápidos.

Límites conocidos:

- Las llamadas de WhatsApp sólo se silencian; al otro teléfono le siguen sonando.
- WhatsApp no expone números: el filtro usa el nombre agendado y una llamada no
  permitida puede sonar un instante antes de silenciarse.
- La pantalla de inicio se elige una vez, a mano, desde Ajustes.
- Si una persona no permitida llama dos veces en 15 minutos, la segunda pasa
  (por si es una urgencia). Se puede apagar en Opciones.

## Compilar

Requisitos: JDK 17 y Android SDK con la plataforma 36 instalada.

Con el JDK y el SDK configurados, ejecutá:

```bash
./gradlew assembleDebug testDebugUnitTest lint
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.
