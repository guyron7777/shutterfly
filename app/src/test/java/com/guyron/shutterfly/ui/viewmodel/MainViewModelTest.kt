package com.guyron.shutterfly.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.domain.usecase.*
import com.guyron.shutterfly.ui.state.DragStateManager
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private lateinit var repo: ImageRepository

    @Before
    fun setup() {
        repo = Mockito.mock(ImageRepository::class.java)
        Mockito.`when`(repo.getSampleImages()).thenReturn(listOf(1, 2, 3))
        viewModel = MainViewModel(
            repo,
            AddImageToCanvasUseCase(),
            MoveImageUseCase(),
            ScaleImageUseCase(),
            SelectImageUseCase(),
            DragStateManager()
        )
    }

    @Test
    fun `AddImage action adds image to state`() = runTest {
        val position = Offset(5f, 5f)
        viewModel.handleAction(ImageManipulatorAction.AddImage(1, position))
        val state = viewModel.state.value
        assertThat(state.canvasState.images).hasSize(1)
        assertThat(state.canvasState.images[0].resourceId).isEqualTo(1)
        assertThat(state.canvasState.images[0].position).isEqualTo(position)
    }
} 