package com.guyron.shutterfly

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.guyron.shutterfly.di.DependencyContainer
import com.guyron.shutterfly.ui.component.CanvasArea
import com.guyron.shutterfly.ui.component.GlobalDragOverlay
import com.guyron.shutterfly.ui.component.ImageCarousel
import com.guyron.shutterfly.ui.component.ImageManipulatorTopBar
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import com.guyron.shutterfly.ui.theme.ImageManipulatorTheme
import com.guyron.shutterfly.ui.viewmodel.ImageManipulatorViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageManipulatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ImageManipulatorScreen(context = LocalContext.current)
                }
            }
        }
    }
}

@Composable
fun ImageManipulatorScreen(
    context: Context,
    viewModel: ImageManipulatorViewModel = viewModel {
        DependencyContainer.provideImageManipulatorViewModel(context = context)
    }
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImageManipulatorTopBar(
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
