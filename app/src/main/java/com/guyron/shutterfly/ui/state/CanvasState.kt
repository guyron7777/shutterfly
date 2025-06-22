package com.guyron.shutterfly.ui.state

import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.domain.model.CanvasImage

data class CanvasState(
    val images: List<CanvasImage> = emptyList(),
    val selectedImageId: String? = null,
    val canvasSize: androidx.compose.ui.unit.IntSize = androidx.compose.ui.unit.IntSize.Zero
) {
    private val zIndexDelta = AppConstants.MagicNumbers.Z_INDEX
    private var maxZIndex = images.maxOfOrNull { it.zIndex } ?: 0
    fun addImage(image: CanvasImage): CanvasState = copy(
        images = images.map { it.deselect() } + image.copy(
            zIndex = maxZIndex + zIndexDelta,
            isSelected = true
        ),
        selectedImageId = image.id
    )

    fun updateImage(imageId: String, transform: (CanvasImage) -> CanvasImage): CanvasState {
        return copy(
            images = images.map { if (it.id == imageId) transform(it) else it }
        )
    }

    fun selectImage(imageId: String?): CanvasState {
        return copy(
            images = images.map { image ->
                when {
                    image.id == imageId -> image.copy(
                        isSelected = true,
                        zIndex = maxZIndex + zIndexDelta
                    )

                    else -> image.copy(isSelected = false)
                }
            },
            selectedImageId = imageId
        )
    }

    fun deselectAll(): CanvasState = copy(
        images = images.map { it.deselect() },
        selectedImageId = null
    )
}