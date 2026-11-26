package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.os.Process

/**
 * Algo que se abre desde el inicio: una app, o un atajo fijado por otra app (por ejemplo
 * "Agregar a la pantalla principal" de Chrome), que lleva [atajo] con su id.
 */
data class AppLanzable(val paquete: String, val nombre: String, val atajo: String? = null) {
    val clave: String get() = if (atajo == null) paquete else "$paquete/$atajo"
}

fun appsLanzables(context: Context): List<AppLanzable> {
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return context.packageManager.queryIntentActivities(intent, 0)
        .map { AppLanzable(it.activityInfo.packageName, it.loadLabel(context.packageManager).toString()) }
        .distinctBy { it.paquete }
        .sortedBy { it.nombre.lowercase() }
}

/** Atajos que otras apps fijaron en este inicio. Sólo se pueden leer siendo el inicio predeterminado. */
fun atajosFijados(context: Context): List<AppLanzable> {
    val launcherApps = context.getSystemService(LauncherApps::class.java) ?: return emptyList()
    return runCatching {
        if (!launcherApps.hasShortcutHostPermission()) return emptyList()
        val consulta = LauncherApps.ShortcutQuery().setQueryFlags(LauncherApps.ShortcutQuery.FLAG_MATCH_PINNED)
        launcherApps.getShortcuts(consulta, Process.myUserHandle()).orEmpty()
            .filter { it.isEnabled }
            .map { AppLanzable(it.`package`, (it.shortLabel ?: it.longLabel ?: it.`package`).toString(), it.id) }
    }.getOrDefault(emptyList())
}

fun abrirApp(context: Context, app: AppLanzable) {
    if (app.atajo != null) {
        runCatching {
            context.getSystemService(LauncherApps::class.java)
                ?.startShortcut(app.paquete, app.atajo, null, null, Process.myUserHandle())
        }
        return
    }
    context.packageManager.getLaunchIntentForPackage(app.paquete)?.let(context::startActivity)
}
