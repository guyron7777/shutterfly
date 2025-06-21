package com.guyron.shutterfly.ui.viewmodel

import android.content.Context
import android.util.TypedValue
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guyron.shutterfly.domain.history.HistoryManager
import com.guyron.shutterfly.domain.model.CanvasState
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.domain.usecase.AddImageToCanvasUseCase
import com.guyron.shutterfly.domain.usecase.MoveImageUseCase
import com.guyron.shutterfly.domain.usecase.ScaleImageUseCase
import com.guyron.shutterfly.domain.usecase.SelectImageUseCase
import com.guyron.shutterfly.ui.state.GlobalDragState
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import com.guyron.shutterfly.ui.state.ImageManipulatorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageManipulatorViewModel(
    imageRepository: ImageRepository,
    context: Context,
    private val addImageUseCase: AddImageToCanvasUseCase = AddImageToCanvasUseCase(),
    private val moveImageUseCase: MoveImageUseCase = MoveImageUseCase(),
    private val scaleImageUseCase: ScaleImageUseCase = ScaleImageUseCase(),
    private val selectImageUseCase: SelectImageUseCase = SelectImageUseCase()
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
                is ImageManipulatorAction.StartGlobalDrag -> startGlobalDrag(
                    action.resourceId,
                    action.startPosition
                )

                is ImageManipulatorAction.UpdateGlobalDrag -> updateGlobalDrag(action.position)
                is ImageManipulatorAction.EndGlobalDrag -> endGlobalDrag()
                is ImageManipulatorAction.CancelGlobalDrag -> cancelGlobalDrag()
                is ImageManipulatorAction.UpdateCanvasBounds -> updateCanvasBounds(
                    action.position,
                    action.size
                )
            }
        }
    }

    private fun updateCanvasBounds(
        position: Offset,
        size: androidx.compose.ui.unit.IntSize
    ) {
        val currentState = _state.value.globalDragState
        _state.value = _state.value.copy(
            globalDragState = currentState.copy(
                canvasScreenPosition = position,
                canvasScreenSize = size
            )
        )
    }

    private fun startGlobalDrag(
        resourceId: Int,
        startPosition: Offset
    ) {
        _state.value = _state.value.copy(
            globalDragState = _state.value.globalDragState.copy(
                isDragging = true,
                resourceId = resourceId,
                currentPosition = startPosition,
                startPosition = startPosition
            )
        )
    }

    private fun updateGlobalDrag(position: Offset) {
        if (_state.value.globalDragState.isDragging) {
            _state.value = _state.value.copy(
                globalDragState = _state.value.globalDragState.copy(
                    currentPosition = position
                )
            )
        }
    }

    private val imageSizeInPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        80f,
        context.resources.displayMetrics
    )

    private fun calculateActualImageSize(): Float {
        return imageSizeInPx
    }

    private fun endGlobalDrag() {
        val dragState = _state.value.globalDragState
        if (dragState.isDragging && dragState.resourceId != null) {
            val hasValidCanvasBounds = dragState.canvasScreenSize.width > 0 &&
                    dragState.canvasScreenSize.height > 0

            if (hasValidCanvasBounds) {
                val isInsideCanvas = isPositionInCanvas(dragState.currentPosition, dragState)
                if (isInsideCanvas) {
                    val canvasRelativeFingerPosition =
                        dragState.currentPosition - dragState.canvasScreenPosition
                    val topLeftPosition = Offset(
                        x = canvasRelativeFingerPosition.x - (calculateActualImageSize() / 2),
                        y = canvasRelativeFingerPosition.y - (calculateActualImageSize() / 2)
                    )
                    selectImage(dragState.resourceId.toString())
                    addImage(dragState.resourceId, topLeftPosition)
                    _state.value = _state.value.copy(
                        globalDragState = GlobalDragState()
                    )

                    return
                } else {
                    _state.value = _state.value.copy(
                        globalDragState = _state.value.globalDragState.copy(
                            isDragging = false
                        )
                    )
                }
            }
        }

        _state.value = _state.value.copy(
            globalDragState = _state.value.globalDragState.copy(
                isDragging = false
            )
        )
    }

    private fun isPositionInCanvas(
        position: Offset,
        dragState: GlobalDragState
    ): Boolean {
        val canvasLeft = dragState.canvasScreenPosition.x
        val canvasTop = dragState.canvasScreenPosition.y
        val canvasRight = canvasLeft + dragState.canvasScreenSize.width
        val canvasBottom = canvasTop + dragState.canvasScreenSize.height

        return position.x in canvasLeft..canvasRight &&
                position.y >= canvasTop &&
                position.y <= canvasBottom
    }

    private fun cancelGlobalDrag() {
        _state.value = _state.value.copy(
            globalDragState = GlobalDragState()
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

    private fun updateCanvasSize(size: androidx.compose.ui.unit.IntSize) {
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