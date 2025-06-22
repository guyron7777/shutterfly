package com.guyron.shutterfly.ui.state

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class DragStateManagerTest {

    private lateinit var dragStateManager: DragStateManager
    private lateinit var mockContext: Context

    @Before
    fun setUp() {
        dragStateManager = DragStateManager()
        mockContext = Mockito.mock(Context::class.java)
        Mockito.`when`(mockContext.resources).thenReturn(null)
    }

    @Test
    fun `startDrag sets correct state`() {
        val resourceId = 123
        val startPosition = Offset(10f, 20f)
        dragStateManager.startDrag(resourceId, startPosition)
        val state = dragStateManager.currentDragState
        assertThat(state.isDragging).isTrue()
        assertThat(state.resourceId).isEqualTo(resourceId)
        assertThat(state.currentPosition).isEqualTo(startPosition)
        assertThat(state.startPosition).isEqualTo(startPosition)
    }

    @Test
    fun `updateDragPosition updates current position`() {
        dragStateManager.startDrag(1, Offset(0f, 0f))
        dragStateManager.updateDragPosition(Offset(5f, 5f))
        assertThat(dragStateManager.currentDragState.currentPosition).isEqualTo(Offset(5f, 5f))
    }

    @Test
    fun `updateCanvasBounds updates canvas position and size`() {
        val pos = Offset(1f, 2f)
        val size = IntSize(100, 200)
        dragStateManager.updateCanvasBounds(pos, size)
        val state = dragStateManager.currentDragState
        assertThat(state.canvasScreenPosition).isEqualTo(pos)
        assertThat(state.canvasScreenSize).isEqualTo(size)
    }

    @Test
    fun `endDrag returns Invalid if not dragging`() {
        val result = dragStateManager.endDrag(mockContext)
        assertThat(result).isInstanceOf(DragResult.Invalid::class.java)
    }

    @Test
    fun `cancelDrag resets state`() {
        dragStateManager.startDrag(1, Offset(0f, 0f))
        dragStateManager.cancelDrag()
        val state = dragStateManager.currentDragState
        assertThat(state.isDragging).isFalse()
        assertThat(state.resourceId).isNull()
    }
} 