package com.guyron.shutterfly.domain.usecase

import com.guyron.shutterfly.ui.state.CanvasState

class SelectImageUseCase {
    operator fun invoke(
        currentState: CanvasState,
        imageId: String?
    ): CanvasState {
        return currentState.selectImage(imageId)
    }
}