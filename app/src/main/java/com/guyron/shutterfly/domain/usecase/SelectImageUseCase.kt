package com.guyron.shutterfly.domain.usecase

import com.guyron.shutterfly.domain.model.CanvasState

class SelectImageUseCase {
    operator fun invoke(
        currentState: CanvasState,
        imageId: String?
    ): CanvasState {
        return currentState.selectImage(imageId)
    }
}