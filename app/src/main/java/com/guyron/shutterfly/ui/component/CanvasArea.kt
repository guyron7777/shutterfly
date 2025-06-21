package com.guyron.shutterfly.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guyron.shutterfly.R
import com.guyron.shutterfly.domain.model.CanvasImage
import com.guyron.shutterfly.ui.state.ImageManipulatorAction

@Composable
fun CanvasArea(
    modifier: Modifier = Modifier,
    images: List<CanvasImage>,
    onAction: (ImageManipulatorAction) -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Color.Gray.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .border(
                2.dp,
                Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .clip(RoundedCornerShape(8.dp))
            .onGloballyPositioned { coordinates ->
                val position = coordinates.positionInRoot()
                val size = coordinates.size
                onAction(ImageManipulatorAction.UpdateCanvasSize(size))
                onAction(
                    ImageManipulatorAction.UpdateCanvasBounds(
                        position = position,
                        size = size
                    )
                )
            }
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    onAction(ImageManipulatorAction.DeselectAll)
                }
            }
    ) {
        images.forEach { image ->
            DraggableCanvasImage(
                canvasImage = image,
                onAction = onAction
            )
        }

        if (images.isEmpty()) {
            Text(
                stringResource(R.string.empty_canvas_title),
                modifier = Modifier.align(Alignment.Center),
                color = Color.Gray
            )
        }
    }
}