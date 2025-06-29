package com.guyron.shutterfly.domain.history

import com.guyron.shutterfly.constants.AppConstants

/**
 * manage the canvas history for the undo redo buttons.
 */
class HistoryManager<T> {
    private val history = mutableListOf<T>()
    private var currentIndex = -1
    private val maxHistorySize = AppConstants.MagicNumbers.MAX_HISTORY_SIZE
    //save the canvas state on every manipulation.
    fun saveState(state: T) {
        if (currentIndex < history.size - 1) {
            history.subList(currentIndex + 1, history.size).clear()
        }

        history.add(state)
        currentIndex++

        if (history.size > maxHistorySize) {
            history.removeAt(0)
            currentIndex--
        }
    }

    fun canUndo(): Boolean = currentIndex > 0
    fun canRedo(): Boolean = currentIndex < history.size - 1

    fun undo(): T? = if (canUndo()) {
        currentIndex--
        history[currentIndex]
    } else null

    fun redo(): T? = if (canRedo()) {
        currentIndex++
        history[currentIndex]
    } else null
}