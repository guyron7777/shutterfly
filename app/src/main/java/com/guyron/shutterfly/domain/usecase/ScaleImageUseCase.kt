package com.guyron.shutterfly.domain.usecase

import com.guyron.shutterfly.domain.model.CanvasState

class ScaleImageUseCase {
    operator fun invoke(
        currentState: CanvasState,
        imageId: String,
        scaleFactor: Float
    ): CanvasState {
        return currentState.updateImage(imageId) { image ->
            image.updateScale(image.scale * scaleFactor)
        }
    }
}