package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.javisala23.norompaslaspelotas.MainActivity
import io.github.javisala23.norompaslaspelotas.R
import io.github.javisala23.norompaslaspelotas.data.ConfigCache
import io.github.javisala23.norompaslaspelotas.data.EstadoCache
import io.github.javisala23.norompaslaspelotas.data.FondoInicio
import io.github.javisala23.norompaslaspelotas.service.ModoControl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Date

/**
 * Pantalla de inicio. Con el modo activo es un "detox": hora, pocas apps elegidas y nada más.
 * Deslizando hacia arriba están todas, menos las silenciadas; si buscás una de ésas, pide una pausa.
 */
class InicioActivity : ComponentActivity() {
    private var reanudaciones by mutableIntStateOf(0)

    override fun onResume() {
        super.onResume()
        reanudaciones++
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        aplicarBarras(paletaDe(ConfigCache.config.value.fondoInicio))
        setContent {
            val config by ConfigCache.config.collectAsStateWithLifecycle()
            val paleta = paletaDe(config.fondoInicio)
            LaunchedEffect(paleta) { aplicarBarras(paleta) }
            CompositionLocalProvider(LocalPaleta provides paleta) { InicioTranquilo(reanudaciones) }
        }
    }

    private fun aplicarBarras(paleta: Paleta) {
        val estilo = if (paleta.oscura) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
        enableEdgeToEdge(statusBarStyle = estilo, navigationBarStyle = estilo)
        window.setBackgroundDrawable(ColorDrawable(paleta.fondo.toArgb()))
    }
}

data class Paleta(val fondo: Color, val texto: Color, val gris: Color, val linea: Color, val oscura: Boolean)

fun paletaDe(fondo: FondoInicio): Paleta = when (fondo) {
    FondoInicio.NEGRO -> Paleta(Color.Black, Color(0xFFF5F5F2), Color(0xFF8A8A85), Color(0xFF2E2E2B), true)
    FondoInicio.GRAFITO -> Paleta(Color(0xFF1F201E), Color(0xFFEDEDE9), Color(0xFF90918B), Color(0xFF3A3B38), true)
    FondoInicio.BOSQUE -> Paleta(Color(0xFF15201A), Color(0xFFE8EEE5), Color(0xFF8DA192), Color(0xFF2C3A31), true)
    FondoInicio.ARENA -> Paleta(Color(0xFFEFE9DC), Color(0xFF22231F), Color(0xFF77736A), Color(0xFFD5CEBE), false)
}

private val LocalPaleta = staticCompositionLocalOf { paletaDe(FondoInicio.NEGRO) }

@Composable
private fun InicioTranquilo(reanudaciones: Int) {
    val activo by EstadoCache.activo.collectAsStateWithLifecycle()
    val config by ConfigCache.config.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val paleta = LocalPaleta.current
    var todas by remember { mutableStateOf(emptyList<AppLanzable>()) }
    var buscar by rememberSaveable { mutableStateOf("") }
    var listaAbierta by rememberSaveable { mutableStateOf(false) }
    var pausaPara by remember { mutableStateOf<AppLanzable?>(null) }
    var ahora by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            ahora = Date()
            delay(1_000L * (60 - (System.currentTimeMillis() / 1000) % 60).coerceAtLeast(1))
        }
    }
    LaunchedEffect(reanudaciones) {
        // Volver al inicio deja todo como al principio.
        buscar = ""
        listaAbierta = false
        pausaPara = null
        ahora = Date()
        todas = withContext(Dispatchers.IO) {
            (appsLanzables(context).filter { it.paquete != context.packageName } + atajosFijados(context))
                .sortedBy { it.nombre.lowercase() }
        }
    }
    BackHandler {
        when {
            pausaPara != null -> pausaPara = null
            listaAbierta -> { listaAbierta = false; buscar = "" }
        }
    }
    val abrir = { app: AppLanzable -> abrirApp(context, app) }

    Box(Modifier.fillMaxSize().background(paleta.fondo)) {
        if (activo) {
            ModoActivo(
                ahora,
                config.appsInicio.mapNotNull { paquete -> todas.find { it.paquete == paquete && it.atajo == null } },
                reanudaciones,
                abrir,
            ) { listaAbierta = true }
            AnimatedVisibility(
                listaAbierta,
                enter = slideInVertically(tween(380, easing = FastOutSlowInEasing)) { it / 4 } + fadeIn(tween(300)),
                exit = slideOutVertically(tween(280)) { it / 4 } + fadeOut(tween(220)),
            ) {
                Box(Modifier.fillMaxSize().background(paleta.fondo)) {
                    ListaApps(
                        encabezado = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(R.string.all_apps), color = paleta.gris, fontSize = 16.sp, modifier = Modifier.weight(1f))
                                IconButton(onClick = { listaAbierta = false; buscar = "" }) {
                                    Icon(Icons.Rounded.KeyboardArrowDown, stringResource(R.string.back), tint = paleta.gris)
                                }
                            }
                        },
                        todas = todas,
                        ocultas = config.appsSilenciadas,
                        buscar = buscar,
                        cambiarBusqueda = { buscar = it },
                        entrada = if (listaAbierta) reanudaciones * 2 + 1 else reanudaciones * 2,
                        abrir = abrir,
                        pedirPausa = { pausaPara = it },
                    )
                }
            }
            pausaPara?.let { app ->
                Pausa(app, cancelar = { pausaPara = null }) { pausaPara = null; abrir(app) }
            }
        } else {
            ListaApps(
                encabezado = {
                    Column {
                        Spacer(Modifier.height(16.dp))
                        Reloj(ahora, 64.sp)
                        Fecha(ahora, 16.sp)
                    }
                },
                todas = todas,
                ocultas = emptySet(),
                buscar = buscar,
                cambiarBusqueda = { buscar = it },
                entrada = reanudaciones,
                abrir = abrir,
                pedirPausa = {},
            )
        }
    }
}

@Composable
private fun ModoActivo(
    ahora: Date,
    favoritas: List<AppLanzable>,
    entrada: Int,
    abrir: (AppLanzable) -> Unit,
    abrirLista: () -> Unit,
) {
    val context = LocalContext.current
    val paleta = LocalPaleta.current
    val scope = rememberCoroutineScope()
    val umbral = with(androidx.compose.ui.platform.LocalDensity.current) { 90.dp.toPx() }
    Column(
        Modifier.fillMaxSize()
            .pointerInput(Unit) {
                var recorrido = 0f
                detectVerticalDragGestures(
                    onDragStart = { recorrido = 0f },
                    onDragEnd = { if (recorrido < -umbral) abrirLista() },
                ) { _, delta -> recorrido += delta }
            }
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = 32.dp),
    ) {
        Spacer(Modifier.weight(0.45f))
        Aparecer(0, entrada) { Reloj(ahora, 84.sp) }
        Aparecer(1, entrada) { Fecha(ahora, 17.sp) }
        Spacer(Modifier.height(18.dp))
        Aparecer(2, entrada) { FraseQueRespira(stringResource(R.string.calm_phrase)) }
        Spacer(Modifier.height(44.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            favoritas.forEachIndexed { i, app ->
                Aparecer(3 + i, entrada) { TextoTocable(app.nombre, 32.sp) { abrir(app) } }
            }
        }
        Aparecer(3 + favoritas.size, entrada) {
            Column(
                Modifier.fillMaxWidth().clickable(onClick = abrirLista, role = Role.Button).padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlechaQueFlota()
                Text(stringResource(R.string.swipe_hint), color = paleta.gris, fontSize = 13.sp)
            }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextoTocable(stringResource(R.string.return_normal), 15.sp, color = paleta.gris, modifier = Modifier.weight(1f)) {
                scope.launch { ModoControl.desactivar(context) }
            }
            IconButton(onClick = { context.startActivity(Intent(context, MainActivity::class.java)) }) {
                Icon(Icons.Rounded.Settings, stringResource(R.string.settings), tint = paleta.gris)
            }
        }
    }
}

@Composable
private fun ListaApps(
    encabezado: @Composable () -> Unit,
    todas: List<AppLanzable>,
    ocultas: Set<String>,
    buscar: String,
    cambiarBusqueda: (String) -> Unit,
    entrada: Int,
    abrir: (AppLanzable) -> Unit,
    pedirPausa: (AppLanzable) -> Unit,
) {
    val paleta = LocalPaleta.current
    val texto = buscar.trim()
    // Las ocultas sólo aparecen si las buscás a propósito, y apagadas.
    val visibles = remember(todas, ocultas, texto) {
        todas.filter { app ->
            val coincide = app.nombre.contains(texto, ignoreCase = true)
            if (app.paquete in ocultas) texto.isNotEmpty() && coincide else coincide
        }
    }
    val tocar = { app: AppLanzable -> if (app.paquete in ocultas) pedirPausa(app) else abrir(app) }
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(horizontal = 32.dp)) {
        Aparecer(0, entrada) { encabezado() }
        Spacer(Modifier.height(20.dp))
        Aparecer(1, entrada) {
            Buscador(buscar, cambiarBusqueda) { visibles.singleOrNull()?.let(tocar) }
        }
        Spacer(Modifier.height(12.dp))
        if (visibles.isEmpty() && texto.isNotEmpty()) {
            Text(stringResource(R.string.no_results), color = paleta.gris, fontSize = 16.sp, modifier = Modifier.padding(vertical = 16.dp))
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 24.dp)) {
            itemsIndexed(visibles, key = { _, app -> app.clave }) { i, app ->
                Aparecer(2 + i, entrada) {
                    if (app.paquete in ocultas) {
                        Column(Modifier.padding(vertical = 6.dp)) {
                            TextoTocable(app.nombre, 24.sp, color = paleta.gris) { tocar(app) }
                            Text(stringResource(R.string.hidden_app_note), color = paleta.gris.copy(alpha = 0.7f), fontSize = 12.sp)
                        }
                    } else {
                        TextoTocable(app.nombre, 24.sp, modifier = Modifier.padding(vertical = 6.dp)) { tocar(app) }
                    }
                }
            }
        }
    }
}

/** Pausa antes de abrir una app oculta: un círculo que respira y una cuenta de 5 segundos. */
@Composable
private fun Pausa(app: AppLanzable, cancelar: () -> Unit, abrirIgual: () -> Unit) {
    val paleta = LocalPaleta.current
    val sinOnda = remember { MutableInteractionSource() }
    var faltan by remember(app) { mutableIntStateOf(5) }
    LaunchedEffect(app) {
        while (faltan > 0) { delay(1_000); faltan-- }
    }
    val respiracion = rememberInfiniteTransition(label = "pausa")
    val escala by respiracion.animateFloat(
        0.55f, 1f,
        infiniteRepeatable(tween(4_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "escala",
    )
    Column(
        Modifier.fillMaxSize().background(paleta.fondo)
            .clickable(sinOnda, indication = null) {}
            .windowInsetsPadding(WindowInsets.safeDrawing).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.fillMaxSize()
                    .graphicsLayer { scaleX = escala; scaleY = escala; alpha = 0.15f + escala * 0.2f }
                    .background(paleta.texto, CircleShape),
            )
            Text(stringResource(R.string.pause_title), color = paleta.texto, fontSize = 28.sp, fontWeight = FontWeight.Light)
        }
        Spacer(Modifier.height(40.dp))
        Text(
            stringResource(R.string.pause_body, app.nombre),
            color = paleta.texto,
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(48.dp))
        TextoTocable(stringResource(R.string.better_not), 26.sp, onClickLabel = null, alTocar = cancelar)
        Spacer(Modifier.height(16.dp))
        if (faltan > 0) {
            Text(stringResource(R.string.pause_wait, faltan), color = paleta.gris, fontSize = 16.sp, modifier = Modifier.heightIn(min = 48.dp).wrapContentHeight())
        } else {
            TextoTocable(stringResource(R.string.open_anyway), 16.sp, color = paleta.gris, alTocar = abrirIgual)
        }
    }
}

@Composable
private fun Reloj(ahora: Date, tamano: TextUnit) {
    val context = LocalContext.current
    TextoTocable(DateFormat.getTimeInstance(DateFormat.SHORT).format(ahora), tamano, peso = FontWeight.Thin) {
        // Tocar la hora abre las alarmas.
        runCatching { context.startActivity(Intent(AlarmClock.ACTION_SHOW_ALARMS)) }
    }
}

@Composable
private fun Fecha(ahora: Date, tamano: TextUnit) {
    Text(
        DateFormat.getDateInstance(DateFormat.FULL).format(ahora).replaceFirstChar { it.uppercase() },
        color = LocalPaleta.current.gris,
        fontSize = tamano,
    )
}

/** Texto tocable: al apretarlo se achica y se apaga un poco, sin ondas. */
@Composable
private fun TextoTocable(
    texto: String,
    tamano: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = LocalPaleta.current.texto,
    peso: FontWeight = FontWeight.Light,
    onClickLabel: String? = null,
    alTocar: () -> Unit,
) {
    val interaccion = remember { MutableInteractionSource() }
    val apretado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (apretado) 0.96f else 1f, spring(stiffness = Spring.StiffnessMediumLow), label = "escala")
    val opacidad by animateFloatAsState(if (apretado) 0.45f else 1f, tween(120), label = "opacidad")
    Text(
        texto,
        color = color,
        fontSize = tamano,
        fontWeight = peso,
        letterSpacing = if (peso == FontWeight.Thin) (-2).sp else 0.2.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .graphicsLayer {
                scaleX = escala; scaleY = escala; alpha = opacidad
                transformOrigin = TransformOrigin(0f, 0.5f)
            }
            .clickable(interaccion, indication = null, role = Role.Button, onClickLabel = onClickLabel, onClick = alTocar)
            .heightIn(min = 48.dp)
            .wrapContentHeight(Alignment.CenterVertically),
    )
}

/** Un punto que late despacio, como una respiración, al lado de la frase. */
@Composable
private fun FraseQueRespira(texto: String) {
    val paleta = LocalPaleta.current
    val respiracion = rememberInfiniteTransition(label = "respiracion")
    val valor by respiracion.animateFloat(
        0.25f, 1f,
        infiniteRepeatable(tween(4_000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "valor",
    )
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(8.dp)
                .graphicsLayer { scaleX = 0.7f + valor * 0.6f; scaleY = 0.7f + valor * 0.6f; alpha = valor }
                .background(paleta.texto, CircleShape),
        )
        Text(texto, color = paleta.gris, fontSize = 16.sp, modifier = Modifier.alpha(0.6f + valor * 0.4f))
    }
}

/** Flechita que sube y baja apenas, para invitar a deslizar. */
@Composable
private fun FlechaQueFlota() {
    val flota = rememberInfiniteTransition(label = "flecha")
    val y by flota.animateFloat(
        0f, -6f,
        infiniteRepeatable(tween(1_400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "y",
    )
    Icon(
        Icons.Rounded.KeyboardArrowUp,
        contentDescription = null,
        tint = LocalPaleta.current.gris,
        modifier = Modifier.graphicsLayer { translationY = y.dp.toPx() },
    )
}

@Composable
private fun Buscador(texto: String, cambiar: (String) -> Unit, abrirUnica: () -> Unit) {
    val paleta = LocalPaleta.current
    BasicTextField(
        texto,
        cambiar,
        singleLine = true,
        textStyle = TextStyle(color = paleta.texto, fontSize = 20.sp, fontWeight = FontWeight.Light),
        cursorBrush = SolidColor(paleta.texto),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { abrirUnica() }),
        decorationBox = { campo ->
            Column {
                Box(Modifier.padding(vertical = 10.dp)) {
                    if (texto.isEmpty()) Text(stringResource(R.string.search_apps), color = paleta.gris.copy(alpha = 0.7f), fontSize = 20.sp, fontWeight = FontWeight.Light)
                    campo()
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(paleta.linea))
            }
        },
        modifier = Modifier.fillMaxWidth(),
    )
}

/** Aparece subiendo y aclarándose, un poco después que el anterior (cascada). */
@Composable
private fun Aparecer(indice: Int, entrada: Int, contenido: @Composable () -> Unit) {
    val progreso = remember(entrada) { Animatable(0f) }
    LaunchedEffect(entrada) {
        // Cascada sólo al entrar; lo que aparece al desplazar se aclara sin esperar.
        if (indice < 15) delay(indice * 45L)
        progreso.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
    }
    Box(Modifier.graphicsLayer { alpha = progreso.value; translationY = (1f - progreso.value) * 28.dp.toPx() }) {
        contenido()
    }
}
