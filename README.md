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

La primera vez, la app guía en tres pasos: permisos, personas que pueden llamar y
apps para silenciar. Cada paso se puede saltear y completar más tarde.

1. En **Permisos**, habilitá notificaciones, filtro de llamadas, acceso a
   notificaciones, No molestar y la pantalla de inicio. La pantalla muestra cuántos
   accesos ya están listos y explica para qué sirve cada uno.
2. En **Personas**, agregá desde contactos o escribí nombre y número. Sólo esas
   personas podrán llamarte mientras el modo esté activo.
3. En **Apps silenciadas**, buscá y marcá las apps que no querés que interrumpan.
   La primera vez se sugieren las redes sociales conocidas que estén instaladas.
4. En **Pantalla tranquila**, elegí las pocas apps que querés tener visibles.
5. Tocá el botón circular **Prender**. Para salir, tocá **Activo** o usá
   “Volver al modo normal” desde la pantalla tranquila.

Si Android 13 o posterior muestra “Configuración restringida” al habilitar el
acceso a notificaciones, desplegá la ayuda de esa tarjeta: incluye los pasos y un
acceso directo a la información de la app.

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
