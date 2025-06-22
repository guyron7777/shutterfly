package com.guyron.shutterfly.ui.state

data class ImageManipulatorState(
    val canvasState: CanvasState = CanvasState(),
    val globalDragState: GlobalDragState = GlobalDragState(),
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val isLoading: Boolean = false
)