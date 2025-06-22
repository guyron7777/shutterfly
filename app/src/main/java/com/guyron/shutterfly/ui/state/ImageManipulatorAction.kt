package com.guyron.shutterfly.ui.state

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

/**
 * manage the images manipulations actions
 */
sealed class ImageManipulatorAction {
    data class AddImage(val resourceId: Int, val position: Offset) : ImageManipulatorAction()
    data class MoveImage(val imageId: String, val position: Offset) : ImageManipulatorAction()
    data class ScaleImage(
        val imageId: String,
        val scaleFactor: Float,
        val saveState: Boolean = false
    ) : ImageManipulatorAction()

    data class SelectImage(val imageId: String?) : ImageManipulatorAction()
    data object DeselectAll : ImageManipulatorAction()
    data object Undo : ImageManipulatorAction()
    data object Redo : ImageManipulatorAction()
    data class UpdateCanvasSize(val size: IntSize) : ImageManipulatorAction()
    data class StartGlobalDrag(val resourceId: Int, val startPosition: Offset) :
        ImageManipulatorAction()

    data class UpdateGlobalDrag(val position: Offset) : ImageManipulatorAction()
    data class EndGlobalDrag(val context: Context) : ImageManipulatorAction()
    data object CancelGlobalDrag : ImageManipulatorAction()
    data class UpdateCanvasBounds(
        val position: Offset,
        val size: IntSize
    ) : ImageManipulatorAction()
}
