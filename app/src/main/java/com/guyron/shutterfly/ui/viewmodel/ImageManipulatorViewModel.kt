package com.guyron.shutterfly.ui.viewmodel

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guyron.shutterfly.domain.history.HistoryManager
import com.guyron.shutterfly.domain.model.CanvasState
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.domain.usecase.AddImageToCanvasUseCase
import com.guyron.shutterfly.domain.usecase.MoveImageUseCase
import com.guyron.shutterfly.domain.usecase.ScaleImageUseCase
import com.guyron.shutterfly.domain.usecase.SelectImageUseCase
import com.guyron.shutterfly.ui.state.DragResult
import com.guyron.shutterfly.ui.state.DragStateManager
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import com.guyron.shutterfly.ui.state.ImageManipulatorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageManipulatorViewModel @Inject constructor(
    imageRepository: ImageRepository,
    private val addImageUseCase: AddImageToCanvasUseCase,
    private val moveImageUseCase: MoveImageUseCase,
    private val scaleImageUseCase: ScaleImageUseCase,
    private val selectImageUseCase: SelectImageUseCase,
    private val dragManager: DragStateManager
) : ViewModel() {

    private val historyManager = HistoryManager<CanvasState>()

    private val _state = MutableStateFlow(ImageManipulatorState())
    val state: StateFlow<ImageManipulatorState> = _state.asStateFlow()

    val sampleImages: List<Int> = imageRepository.getSampleImages()

    init {
        historyManager.saveState(CanvasState())
        updateHistoryFlags()
    }

    fun handleAction(action: ImageManipulatorAction) {
        viewModelScope.launch {
            when (action) {
                is ImageManipulatorAction.AddImage -> addImage(action.resourceId, action.position)
                is ImageManipulatorAction.MoveImage -> moveImage(action.imageId, action.position)
                is ImageManipulatorAction.ScaleImage -> scaleImage(
                    action.imageId,
                    action.scaleFactor,
                    action.saveState
                )
                is ImageManipulatorAction.SelectImage -> selectImage(action.imageId)
                is ImageManipulatorAction.DeselectAll -> deselectAll()
                is ImageManipulatorAction.Undo -> undo()
                is ImageManipulatorAction.Redo -> redo()
                is ImageManipulatorAction.UpdateCanvasSize -> updateCanvasSize(action.size)
                is ImageManipulatorAction.StartGlobalDrag -> dragManager.startDrag(
                    action.resourceId,
                    action.startPosition
                )
                is ImageManipulatorAction.UpdateGlobalDrag -> dragManager.updateDragPosition(action.position)
                is ImageManipulatorAction.EndGlobalDrag -> handleDragEnd(action.context)
                is ImageManipulatorAction.CancelGlobalDrag -> dragManager.cancelDrag()
                is ImageManipulatorAction.UpdateCanvasBounds -> dragManager.updateCanvasBounds(
                    action.position,
                    action.size
                )
            }
            updateDragState()
        }
    }

    private fun handleDragEnd(context: Context) {
        when (val dragResult = dragManager.endDrag(context)) {
            is DragResult.Valid -> {
                selectImage(dragResult.resourceId.toString())
                addImage(dragResult.resourceId, dragResult.dropPosition)
            }
            DragResult.Invalid -> {
                // Optionally handle invalid drag (e.g., show feedback or do nothing)
            }
        }
    }

    private fun updateDragState() {
        _state.value = _state.value.copy(
            globalDragState = dragManager.currentDragState
        )
    }

    private fun addImage(resourceId: Int, position: Offset) {
        val newCanvasState = addImageUseCase(
            currentState = _state.value.canvasState,
            resourceId = resourceId,
            position = position
        )
        updateCanvasState(newCanvasState, saveToHistory = true)
    }

    private fun moveImage(imageId: String, position: Offset) {
        val newCanvasState = moveImageUseCase(
            _state.value.canvasState,
            imageId,
            position,
            _state.value.canvasState.canvasSize
        )
        updateCanvasState(newCanvasState, saveToHistory = true)
    }

    private fun scaleImage(imageId: String, scaleFactor: Float, rememberState: Boolean) {
        val newCanvasState = scaleImageUseCase(
            _state.value.canvasState,
            imageId,
            scaleFactor
        )
        updateCanvasState(newCanvasState, saveToHistory = rememberState)
    }

    private fun selectImage(imageId: String?) {
        val newCanvasState = selectImageUseCase(_state.value.canvasState, imageId)
        updateCanvasState(newCanvasState, saveToHistory = false)
    }

    private fun deselectAll() {
        val newCanvasState = _state.value.canvasState.deselectAll()
        updateCanvasState(newCanvasState, saveToHistory = false)
    }

    private fun undo() {
        historyManager.undo()?.let { previousState ->
            updateCanvasState(previousState, saveToHistory = false)
        }
    }

    private fun redo() {
        historyManager.redo()?.let { nextState ->
            updateCanvasState(nextState, saveToHistory = false)
        }
    }

    private fun updateCanvasSize(size: IntSize) {
        val newCanvasState = _state.value.canvasState.copy(canvasSize = size)
        updateCanvasState(newCanvasState, saveToHistory = false)
    }

    private fun updateCanvasState(newState: CanvasState, saveToHistory: Boolean) {
        if (saveToHistory) {
            historyManager.saveState(newState)
        }

        _state.value = _state.value.copy(canvasState = newState)
        updateHistoryFlags()
    }

    private fun updateHistoryFlags() {
        _state.value = _state.value.copy(
            canUndo = historyManager.canUndo(),
            canRedo = historyManager.canRedo()
        )
    }
}