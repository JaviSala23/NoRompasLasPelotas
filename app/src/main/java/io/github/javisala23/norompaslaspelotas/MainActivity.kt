package io.github.javisala23.norompaslaspelotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.javisala23.norompaslaspelotas.ui.AppPrincipal
import io.github.javisala23.norompaslaspelotas.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val activo by viewModel.activo.collectAsStateWithLifecycle()
                AppPrincipal(
                    activo = activo,
                    onActivoChange = viewModel::cambiarEstado,
                )
            }
        }
    }
}
