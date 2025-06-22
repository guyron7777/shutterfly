package com.guyron.shutterfly.domain.usecase

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.ui.state.CanvasState

class MoveImageUseCase {
    operator fun invoke(
        currentState: CanvasState,
        imageId: String,
        newPosition: Offset,
        canvasSize: IntSize
    ): CanvasState {
        return currentState.updateImage(imageId) { image ->
            val imageSize = AppConstants.MagicNumbers.IMAGE_SIZE_F * image.scale
            val constrainedPosition = Offset(
                x = newPosition.x.coerceIn(0f, canvasSize.width - imageSize),
                y = newPosition.y.coerceIn(0f, canvasSize.height - imageSize)
            )
            image.updatePosition(constrainedPosition)
        }
    }
}