package com.guyron.shutterfly.domain.usecase

import androidx.compose.ui.geometry.Offset
import com.guyron.shutterfly.domain.model.CanvasState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class AddImageToCanvasUseCaseTest {
    @Test
    fun `invoke adds image to canvas state`() {
        val useCase = AddImageToCanvasUseCase()
        val initialState = CanvasState()
        val resourceId = 123
        val position = Offset(10f, 20f)

        val result = useCase(initialState, resourceId, position)

        assertThat(result.images).hasSize(1)
        assertThat(result.images[0].resourceId).isEqualTo(resourceId)
        assertThat(result.images[0].position).isEqualTo(position)
    }
} 