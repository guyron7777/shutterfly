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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.ui.state.ImageManipulatorAction

/**
 * the images inside the bottom carousel, can be square, crop, for better view.
 * the image will be with the correct dim when the user drag it
 * the drag is global drag so the user can drag the image above it parent's box
*/
@Composable
fun CarouselImage(
    resourceId: Int,
    onAction: (ImageManipulatorAction) -> Unit
) {
    val context = LocalContext.current
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
                //make the long click enabled, after the user drag the image more then 30.dp up
                //the awaitPointerEventScope will start
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
                            onAction(ImageManipulatorAction.EndGlobalDrag(context = context))
                        }
                    ) { _, dragAmount ->
                        currentFingerPosition.value += dragAmount
                        onAction(ImageManipulatorAction.UpdateGlobalDrag(currentFingerPosition.value))
                    }
                }
                //awaitPointerEventScope - check if the user drag to the top more then 30.dp,
                // if so, stop scrolling and drag the global image
                .pointerInput("carousel_global_swipe_$resourceId") {
                    awaitPointerEventScope {
                        while (true) {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            var swipeStarted = false
                            var globalDragActive = false

                            val globalDownPosition = imageScreenPosition.value + down.position
                            var lastPointerPosition = down.position

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
                                    // start dragging
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

                                            pointer.consume()
                                            onAction(
                                                ImageManipulatorAction.UpdateGlobalDrag(
                                                    currentFingerPosition.value
                                                )
                                            )
                                        }
                                    }
                                    //continues dragging
                                    if (isDragging.value && globalDragActive) {
                                        currentFingerPosition.value += pointerMovement

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
                            //end of dragging
                            if (globalDragActive) {
                                isDragging.value = false
                                onAction(ImageManipulatorAction.EndGlobalDrag(context))
                            }
                        }
                    }
                },
            contentScale = ContentScale.Crop
        )
    }
}