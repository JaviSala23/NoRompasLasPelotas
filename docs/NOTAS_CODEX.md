# Notas de Codex — E0 y E1

Fecha: 2026-11-20

Se implementaron únicamente E0 y E1 con el nombre visible **No Molestar**, el
proyecto Gradle **NoMolestar** y el paquete
`io.github.javisala23.nomolestar`. No se agregó el permiso `INTERNET`.

## Archivos creados

- `.github/workflows/build.yml`
- `.gitignore`
- `LICENSE`
- `README.md`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.properties`
- `gradlew`
- `gradlew.bat`
- `app/build.gradle.kts`
- `app/proguard-rules.pro`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/io/github/javisala23/nomolestar/MainActivity.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/MainViewModel.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/data/EstadoRepository.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/domain/EstadoModo.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/service/BootReceiver.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/service/ModoService.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/ui/NoMolestarApp.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/ui/theme/Color.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/ui/theme/Theme.kt`
- `app/src/main/java/io/github/javisala23/nomolestar/ui/theme/Type.kt`
- `app/src/main/res/drawable/ic_stat_no_molestar.xml`
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-night/themes.xml`
- `app/src/test/java/io/github/javisala23/nomolestar/domain/EstadoModoTest.kt`
- `docs/NOTAS_CODEX.md`

## Verificación y dudas

- No se ejecutaron Gradle, tests ni lint porque el entorno no tiene red y, por
  pedido, no se generó `gradle-wrapper.jar`.
- Falta que Javi genere o agregue localmente
  `gradle/wrapper/gradle-wrapper.jar` antes de usar el wrapper o el workflow.
- En Android 13 o posterior, la pantalla de permisos abre directamente los
  Ajustes de notificaciones y no dispara diálogos. Conviene validar en el celu
  de referencia que ese flujo resulte claro antes de sumar los permisos de
  E2–E4.
- No quedaron dudas de producto para E0–E1.
