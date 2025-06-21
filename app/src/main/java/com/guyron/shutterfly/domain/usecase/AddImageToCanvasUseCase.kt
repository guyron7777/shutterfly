package com.guyron.shutterfly.domain.usecase

import androidx.compose.ui.geometry.Offset
import com.guyron.shutterfly.domain.model.CanvasImage
import com.guyron.shutterfly.domain.model.CanvasState

class AddImageToCanvasUseCase {
    operator fun invoke(
        currentState: CanvasState,
        resourceId: Int,
        position: Offset = Offset.Zero
    ): CanvasState {
        val newImage = CanvasImage(
            resourceId = resourceId,
            position = position
        )
        return currentState.addImage(newImage)
    }
}