package io.github.javisala23.norompaslaspelotas.ui.inicio

import android.content.Context
import android.content.Intent

data class AppLanzable(val paquete: String, val nombre: String)

fun appsLanzables(context: Context): List<AppLanzable> {
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    return context.packageManager.queryIntentActivities(intent, 0)
        .map { AppLanzable(it.activityInfo.packageName, it.loadLabel(context.packageManager).toString()) }
        .distinctBy { it.paquete }
        .sortedBy { it.nombre.lowercase() }
}

fun abrirApp(context: Context, paquete: String) {
    context.packageManager.getLaunchIntentForPackage(paquete)?.let(context::startActivity)
}
