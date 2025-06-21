package com.guyron.shutterfly.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.domain.model.CanvasImage
import com.guyron.shutterfly.ui.state.ImageManipulatorAction
import kotlin.math.roundToInt

@Composable
fun DraggableCanvasImage(
    canvasImage: CanvasImage,
    onAction: (ImageManipulatorAction) -> Unit
) {
    val density = LocalDensity.current
    val isDragging = remember { mutableStateOf(false) }
    val currentDragPosition = remember { mutableStateOf(canvasImage.position) }
    var zoomChange = 1f
    LaunchedEffect(canvasImage.position) {
        if (!isDragging.value) {
            currentDragPosition.value = canvasImage.position
        }
    }

    val animatedPosition = animateOffsetAsState(
        targetValue = if (isDragging.value) currentDragPosition.value else canvasImage.position,
        animationSpec = if (isDragging.value) {
            tween(durationMillis = 16, easing = LinearEasing)
        } else {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "imagePosition"
    )

    val animatedScale = animateFloatAsState(
        targetValue = if (isDragging.value) canvasImage.scale * 1f else canvasImage.scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "imageScale"
    )
    val baseSize = AppConstants.MagicNumbers.IMAGE_SIZE_DP
    val scale = canvasImage.scale

    val touchAreaScale = 1f + (scale - 1f) * 1.5f
    val actualTouchSize = baseSize * touchAreaScale

    val touchAreaDiff = actualTouchSize - baseSize
    val offsetAdjustment = touchAreaDiff / 2f
    Box(
        modifier = Modifier
            .size(actualTouchSize)
            .offset {
                val adjustedX = animatedPosition.value.x - with(density) { offsetAdjustment.toPx() }
                val adjustedY = animatedPosition.value.y - with(density) { offsetAdjustment.toPx() }
                IntOffset(
                    x = adjustedX.roundToInt(),
                    y = adjustedY.roundToInt()
                )
            }
            .zIndex(canvasImage.zIndex.toFloat())
            .pointerInput("gestures_${canvasImage.id}") {
                awaitPointerEventScope {
                    while (true) {
                        var transformStarted = false
                        var dragStarted = false
                        do {
                            val event = awaitPointerEvent()
                            val canceled = event.changes.any { it.isConsumed }

                            if (!canceled) {
                                zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()

                                when {
                                    zoomChange != 1.0f && kotlin.math.abs(zoomChange - 1.0f) > 0.01f -> {
                                        if (!transformStarted) {
                                            transformStarted = true
                                            onAction(ImageManipulatorAction.SelectImage(canvasImage.id))
                                        }
                                        onAction(
                                            ImageManipulatorAction.ScaleImage(
                                                canvasImage.id,
                                                zoomChange
                                            )
                                        )
                                        event.changes.forEach { it.consume() }
                                    }

                                    panChange != Offset.Zero && !transformStarted -> {
                                        if (!dragStarted) {
                                            dragStarted = true
                                            isDragging.value = true
                                            onAction(ImageManipulatorAction.SelectImage(canvasImage.id))
                                        }
                                        currentDragPosition.value += panChange
                                        event.changes.forEach { it.consume() }
                                    }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                        if (transformStarted) {
                            onAction(
                                ImageManipulatorAction.ScaleImage(
                                    canvasImage.id,
                                    zoomChange,
                                    true
                                )
                            )
                        }
                        if (dragStarted) {
                            isDragging.value = false
                            onAction(
                                ImageManipulatorAction.MoveImage(
                                    canvasImage.id,
                                    currentDragPosition.value
                                )
                            )
                        } else if (!transformStarted) {
                            onAction(ImageManipulatorAction.SelectImage(canvasImage.id))
                        }
                    }
                }
            }
    ) {
        Image(
            painter = painterResource(id = canvasImage.resourceId),
            contentDescription = null,
            modifier = Modifier
                .height(baseSize)
                .wrapContentWidth()
                .size(baseSize)
                .align(Alignment.Center)
                .graphicsLayer(
                    scaleX = animatedScale.value,
                    scaleY = animatedScale.value,
                ),
            contentScale = ContentScale.Inside
        )
    }
}