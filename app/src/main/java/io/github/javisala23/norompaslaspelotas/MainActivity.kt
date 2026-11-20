package io.github.javisala23.norompaslaspelotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.javisala23.norompaslaspelotas.ui.AppPrincipal
import io.github.javisala23.norompaslaspelotas.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private var refreshPermisos by mutableIntStateOf(0)

    override fun onResume() { super.onResume(); refreshPermisos++ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val activo by viewModel.activo.collectAsStateWithLifecycle()
                val config by viewModel.config.collectAsStateWithLifecycle()
                AppPrincipal(
                    activo = activo,
                    config = config,
                    refreshPermisos = refreshPermisos,
                    onActivoChange = viewModel::cambiarEstado,
                    onConfigChange = viewModel::guardarConfig,
                )
            }
        }
    }
}
