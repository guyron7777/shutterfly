package com.guyron.shutterfly.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.ui.state.ImageManipulatorAction

@Composable
fun CarouselImage(
    resourceId: Int,
    onAction: (ImageManipulatorAction) -> Unit
) {
    val initialFingerPosition = remember { mutableStateOf(Offset.Zero) }
    val currentFingerPosition = remember { mutableStateOf(Offset.Zero) }
    val isDragging = remember { mutableStateOf(false) }
    val imageScreenPosition = remember { mutableStateOf(Offset.Zero) }

    val animatedScale = animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "carouselScale"
    )

    Box(
        modifier = Modifier
            .size(AppConstants.MagicNumbers.IMAGE_SIZE_DP)
            .onGloballyPositioned { coordinates ->
                imageScreenPosition.value = coordinates.positionInRoot()
            }) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = animatedScale.value,
                    scaleY = animatedScale.value
                )
                .pointerInput("carousel_drag_$resourceId") {
                    detectDragGesturesAfterLongPress(
                        onDragStart = { startOffset ->
                            initialFingerPosition.value = imageScreenPosition.value + startOffset
                            currentFingerPosition.value = initialFingerPosition.value
                            onAction(
                                ImageManipulatorAction.StartGlobalDrag(
                                    resourceId = resourceId,
                                    startPosition = initialFingerPosition.value
                                )
                            )
                        },
                        onDragEnd = {
                            isDragging.value = false
                            onAction(ImageManipulatorAction.EndGlobalDrag(currentFingerPosition.value))
                        }
                    ) { _, dragAmount ->
                        currentFingerPosition.value += dragAmount
                        onAction(ImageManipulatorAction.UpdateGlobalDrag(currentFingerPosition.value))
                    }
                }
                .pointerInput("carousel_global_swipe_$resourceId") {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var swipeStarted = false
                            var globalDragActive = false
                            var finalGlobalPosition = Offset.Zero

                            val globalDownPosition = imageScreenPosition.value + down.position
                            var lastPointerPosition = down.position  // Track last LOCAL position

                            do {
                                val event = awaitPointerEvent()
                                val pointer = event.changes.first()

                                if (pointer.pressed) {
                                    val pointerMovement = pointer.position - lastPointerPosition
                                    val globalCurrentPosition =
                                        imageScreenPosition.value + pointer.position
                                    val totalMovement = globalCurrentPosition - globalDownPosition
                                    val upwardDistance = -totalMovement.y
                                    val horizontalDistance = kotlin.math.abs(totalMovement.x)

                                    if (!isDragging.value) {
                                        if (!swipeStarted &&
                                            upwardDistance >= AppConstants.Gestures.SWIPE_UP_THRESHOLD.toPx() &&
                                            upwardDistance > horizontalDistance * 2f
                                        ) {
                                            swipeStarted = true
                                            globalDragActive = true
                                            isDragging.value = true

                                            initialFingerPosition.value = globalCurrentPosition
                                            currentFingerPosition.value = globalCurrentPosition
                                            finalGlobalPosition = globalCurrentPosition

                                            pointer.consume()
                                            onAction(
                                                ImageManipulatorAction.StartGlobalDrag(
                                                    resourceId = resourceId,
                                                    startPosition = currentFingerPosition.value
                                                )
                                            )
                                        }

                                        if (globalDragActive) {
                                            currentFingerPosition.value += pointerMovement
                                            finalGlobalPosition = currentFingerPosition.value

                                            pointer.consume()
                                            onAction(
                                                ImageManipulatorAction.UpdateGlobalDrag(
                                                    currentFingerPosition.value
                                                )
                                            )
                                        }
                                    }

                                    if (isDragging.value && globalDragActive) {
                                        currentFingerPosition.value += pointerMovement
                                        finalGlobalPosition = currentFingerPosition.value

                                        pointer.consume()
                                        onAction(
                                            ImageManipulatorAction.UpdateGlobalDrag(
                                                currentFingerPosition.value
                                            )
                                        )
                                    }
                                    lastPointerPosition = pointer.position
                                }
                            } while (event.changes.any { it.pressed })

                            if (globalDragActive) {
                                isDragging.value = false
                                onAction(ImageManipulatorAction.EndGlobalDrag(finalGlobalPosition))
                            }
                        }
                    }
                },
            contentScale = ContentScale.Crop
        )
    }
}