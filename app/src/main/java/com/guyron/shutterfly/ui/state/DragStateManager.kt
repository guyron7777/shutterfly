package com.guyron.shutterfly.ui.state

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.constants.dpToPx

/**
 * Manages drag state and logic for global drag operations
 */
class DragStateManager {
    private var _dragState = GlobalDragState()
    val currentDragState: GlobalDragState get() = _dragState

    fun startDrag(resourceId: Int, startPosition: Offset) {
        _dragState = _dragState.copy(
            isDragging = true,
            resourceId = resourceId,
            currentPosition = startPosition,
            startPosition = startPosition
        )
    }

    fun updateDragPosition(position: Offset) {
        if (_dragState.isDragging) {
            _dragState = _dragState.copy(currentPosition = position)
        }
    }

    fun updateCanvasBounds(position: Offset, size: IntSize) {
        _dragState = _dragState.copy(
            canvasScreenPosition = position,
            canvasScreenSize = size
        )
    }

    fun endDrag(context: Context): DragResult {
        if (!_dragState.isDragging || _dragState.resourceId == null) {
            _dragState = _dragState.copy(isDragging = false)
            return DragResult.Invalid
        }

        val hasValidCanvasBounds = _dragState.canvasScreenSize.width > 0 &&
                _dragState.canvasScreenSize.height > 0

        if (!hasValidCanvasBounds) {
            _dragState = _dragState.copy(isDragging = false)
            return DragResult.Invalid
        }

        val isInsideCanvas = isPositionInCanvas(_dragState.currentPosition)
        if (isInsideCanvas) {
            val resourceId = _dragState.resourceId!!
            val dropPosition = calculateDropPosition(context)
            _dragState = GlobalDragState()
            return DragResult.Valid(
                resourceId = resourceId,
                dropPosition = dropPosition
            )
        } else {
            _dragState = _dragState.copy(isDragging = false)
            return DragResult.Invalid
        }
    }

    fun cancelDrag() {
        _dragState = GlobalDragState()
    }

    private fun isPositionInCanvas(position: Offset): Boolean {
        val canvasLeft = _dragState.canvasScreenPosition.x
        val canvasTop = _dragState.canvasScreenPosition.y
        val canvasRight = canvasLeft + _dragState.canvasScreenSize.width
        val canvasBottom = canvasTop + _dragState.canvasScreenSize.height

        return position.x in canvasLeft..canvasRight &&
                position.y >= canvasTop &&
                position.y <= canvasBottom
    }

    private fun calculateDropPosition(context: Context): Offset {
        val imageSize = context.dpToPx(AppConstants.MagicNumbers.IMAGE_SIZE_F)
        val canvasRelativeFingerPosition = _dragState.currentPosition - _dragState.canvasScreenPosition
        
        return Offset(
            x = canvasRelativeFingerPosition.x - (imageSize / 2),
            y = canvasRelativeFingerPosition.y - (imageSize / 2)
        )
    }
}

/**
 * Sealed class to represent drag end results
 */
sealed class DragResult {
    object Invalid : DragResult()
    data class Valid(
        val resourceId: Int,
        val dropPosition: Offset
    ) : DragResult() {
        val isValidDrop: Boolean = true
    }
} 