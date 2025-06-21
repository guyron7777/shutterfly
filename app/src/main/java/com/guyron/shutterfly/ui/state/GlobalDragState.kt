package com.guyron.shutterfly.ui.state

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

data class GlobalDragState(
    val isDragging: Boolean = false,
    val resourceId: Int? = null,
    val currentPosition: Offset = Offset.Zero,
    val startPosition: Offset = Offset.Zero,
    val canvasScreenPosition: Offset = Offset.Zero,
    val canvasScreenSize: IntSize = IntSize.Zero
)
