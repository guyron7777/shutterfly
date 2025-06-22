package com.guyron.shutterfly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guyron.shutterfly.ui.component.CanvasArea
import com.guyron.shutterfly.ui.component.GlobalDragOverlay
import com.guyron.shutterfly.ui.component.ImageCarousel
import com.guyron.shutterfly.ui.component.ShutterflyTopBar
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import com.guyron.shutterfly.ui.theme.ShutterflyThemeMain
import com.guyron.shutterfly.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShutterflyThemeMain {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageManipulatorScreen()
                }
            }
        }
    }
}

@Composable
fun ImageManipulatorScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ShutterflyTopBar(
                canUndo = state.value.canUndo,
                canRedo = state.value.canRedo,
                onAction = viewModel::handleAction
            )

            CanvasArea(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                images = state.value.canvasState.images,
                onAction = viewModel::handleAction
            )

            ImageCarousel(
                images = viewModel.sampleImages,
                onAction = viewModel::handleAction
            )
        }
        GlobalDragOverlay(
            globalDragState = state.value.globalDragState,
            onAnimationComplete = {
                viewModel.handleAction(ImageManipulatorAction.CancelGlobalDrag)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
