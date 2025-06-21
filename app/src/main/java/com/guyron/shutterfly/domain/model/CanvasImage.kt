package com.guyron.shutterfly.domain.model


import androidx.compose.ui.geometry.Offset
import com.guyron.shutterfly.constants.AppConstants
import java.util.UUID

data class CanvasImage(
    val id: String = UUID.randomUUID().toString(),
    val resourceId: Int,
    val position: Offset = Offset.Zero,
    val scale: Float = 1f,
    val zIndex: Int = 0,
    val isSelected: Boolean = false
) {
    fun updatePosition(newPosition: Offset): CanvasImage = copy(position = newPosition)
    fun updateScale(newScale: Float): CanvasImage = copy(
        scale = newScale.coerceIn(
            AppConstants.MagicNumbers.MIN_ZOOM,
            AppConstants.MagicNumbers.MAX_ZOOM
        )
    )

    fun deselect(): CanvasImage = copy(isSelected = false)
}