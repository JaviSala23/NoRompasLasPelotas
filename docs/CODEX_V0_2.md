# Tarea para Codex — v0.2.0: arreglos y rediseño

Leé CLAUDE.md, docs/PLAN.md y el código actual. No toques la lógica de filtrado
(domain/, service/Filtro*, ModoControl, NoMolestarSistema) salvo lo pedido. No hagas commits.
No podés compilar (sin red): Claude compila después. Podés agregar al catálogo
`androidx.compose.material:material-icons-extended` (sin versión, la da el BOM).
Escribí código legible: un archivo por pantalla en `ui/`, nada de varias sentencias por línea.

## Bugs (prioridad 1)
1. **El filtro de llamadas no se puede activar**: el intent de `RoleManager.createRequestRoleIntent`
   se lanza con `startActivity`, y Android lo ignora (necesita resultado). Usar
   `rememberLauncherForActivityResult(StartActivityForResult())`. Si después del resultado el rol
   sigue sin estar, mostrar un texto de ayuda con botón a `Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS`:
   "Elegí NoRompasLasPelotas en «App de identificador de llamadas y spam»".
2. **Acceso a notificaciones en apps instaladas fuera de Play Store**: en Android 13+ aparece
   "Configuración restringida". En esa tarjeta agregar una ayuda plegable con los pasos:
   Ajustes → Apps → NoRompasLasPelotas → ⋮ → Permitir configuración restringida, con botón que abra
   `Settings.ACTION_APPLICATION_DETAILS_SETTINGS` de la app.
3. **Borrar persona con un toque es peligroso**: reemplazar por ícono de borrar + Snackbar "Deshacer".
4. Persona: key de la lista por nombre puede repetirse si difiere en mayúsculas; usar índice+nombre.
5. Notificación fija del servicio: el texto debe resumir ("Pasan 3 personas · 5 apps en silencio").

## Rediseño (prioridad 2)
Estética: calma, cálida, mucho aire, nada de colores de alerta. Material 3 con la paleta actual
(arena / salvia / grafito; noche). Esquinas redondeadas grandes (24dp), tarjetas planas con
`surfaceContainer`, sin sombras fuertes. Tipografía del sistema con jerarquía clara.
Animaciones suaves y cortas (crossfade / animateColorAsState), nada que rebote.
Todo con `contentDescription` y áreas táctiles de 48dp. Probado mentalmente en claro y oscuro
y en pantallas chicas (360dp de ancho) y con fuente grande.

- **Ícono de la app**: adaptive icon (foreground vectorial: una luna creciente simple o una
  campana tachada minimalista, color salvia sobre fondo arena) + capa `monochrome` (Android 13+).
- **Primera vez (onboarding)**: si nunca se configuró, mostrar 3 pasos con indicador de progreso:
  (1) Permisos, (2) Quién puede llamarte, (3) Qué apps silenciar. Cada paso se puede saltear.
  Guardar `onboardingHecho` en Config.
- **Pantalla principal**:
  - Arriba: nombre de la app pequeño y un saludo tranquilo según la hora ("Buenas tardes").
  - Centro: un botón circular grande (~200dp) para prender/apagar, que cambia suave de color
    (apagado: superficie con borde; prendido: salvia lleno) con ícono de luna y texto
    "Prender" / "Activo". Debajo, una línea de estado: "Pasan 3 personas · 5 apps en silencio".
  - Si falta algún permiso: una tarjeta suave "Te falta 1 permiso" que lleva a Permisos (sin rojo).
  - Abajo: tarjetas/filas con ícono para Personas, Apps silenciadas, Pantalla tranquila,
    Opciones, Permisos, y "Acerca de".
- **Cabeceras**: TopAppBar con flecha de volver (no un botón "Volver").
- **Personas**: FAB "Agregar" que abre un bottom sheet con dos opciones: "Elegir de contactos"
  o escribir nombre + número (teclado de teléfono). Lista en tarjetas con inicial en círculo,
  nombre, números y botón de borrar. Estado vacío amable.
- **Listas de apps (silenciadas / pantalla tranquila)**: buscador arriba, ícono real de cada app,
  nombre, checkbox; las marcadas arriba. Para silenciadas, sugerir marcadas por defecto las redes
  sociales conocidas si están instaladas (Instagram, Facebook, TikTok, X/Twitter, Telegram,
  Messenger, Snapchat) sólo la primera vez, con texto explicándolo.
- **Permisos**: indicador "3 de 5 listos", cada permiso en tarjeta con: ícono, título,
  una línea de por qué, estado (✓ Listo en salvia / "Falta" neutro) y botón "Activar".
- **Opciones**: igual que ahora pero con tarjetas y textos más claros.
- **Acerca de**: versión, "Libre y gratis, GPL-3.0", "No usa Internet", link al repo
  https://github.com/JaviSala23/NoRompasLasPelotas (abre el navegador con un intent).
- **Pantalla tranquila (InicioActivity)**:
  - Modo activo: reloj grande centrado y fino, fecha, una frase calma fija; las apps elegidas
    como texto grande y espaciado; abajo, discreto, "Volver al modo normal" y un ícono de ajustes.
  - Modo normal: reloj más chico, buscador, lista alfabética con ícono pequeño en escala de grises
    (ColorMatrix saturación 0) y nombre; letras de sección (A, B, C…).
  - Al volver a la pantalla (onResume) limpiar el buscador.
- Strings: mover todos los textos a `res/values/strings.xml` (español) para poder traducir.
  Agregar `res/values-en/strings.xml` con la traducción al inglés.
- versionCode 2, versionName "0.2.0".
- Actualizar README (sección de uso) y docs/NOTAS_CODEX.md.
