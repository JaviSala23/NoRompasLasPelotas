# NoRompasLasPelotas

NoRompasLasPelotas es una app Android libre para bajar el ruido del teléfono durante un
rato. En esta primera versión, un interruptor guarda el modo elegido y mantiene
un servicio visible mientras está activo, incluso después de reiniciar el celu.

## Principios

- Todo queda en el teléfono: la app no pide acceso a Internet.
- Sin cuentas, anuncios, analytics ni Google Play Services.
- Sin rachas, urgencias inventadas ni diálogos insistentes.
- Interfaz breve, tranquila y accesible, en claro y oscuro.
- Código libre bajo GPL-3.0.

## Estado actual

Están implementadas las etapas E0 y E1 del plan: estructura Gradle, interfaz
Compose, estado persistente, servicio en primer plano, recuperación al arrancar
y guía de permisos. El filtrado real de llamadas y notificaciones, el modo DND
y el launcher tranquilo pertenecen a etapas posteriores y todavía no están
implementados.

Límites conocidos del plan completo:

- Las llamadas de WhatsApp se podrán silenciar, pero no rechazar para quien llama.
- WhatsApp no expone números; el filtro previsto usa el nombre agendado.
- El launcher se tendrá que elegir una vez, a mano, desde Ajustes.

## Compilar

Requisitos: JDK 17 y Android SDK con la plataforma 36 instalada.

El repositorio no incluye `gradle-wrapper.jar`. Generá o copiá ese archivo del
wrapper de Gradle 8.14.3 y después ejecutá:

```bash
./gradlew assembleDebug testDebugUnitTest lint
```

El APK queda en `app/build/outputs/apk/debug/app-debug.apk`.
