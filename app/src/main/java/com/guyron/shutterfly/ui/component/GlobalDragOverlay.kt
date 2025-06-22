package com.guyron.shutterfly.ui.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.ui.state.GlobalDragState
import kotlin.math.roundToInt

/**
 * global drag, so the user can drag images from the bottom carousel to the canvas view.
 */
@Composable
fun GlobalDragOverlay(
    modifier: Modifier = Modifier,
    globalDragState: GlobalDragState,
    onAnimationComplete: () -> Unit = {},
) {
    if (globalDragState.isDragging && globalDragState.resourceId != null) {
        val density = LocalDensity.current
        val imageSize = AppConstants.MagicNumbers.IMAGE_SIZE_DP
        val imageSizePx = with(density) { imageSize.toPx() }
        val offsetX = (globalDragState.currentPosition.x - imageSizePx / 2).roundToInt()
        val offsetY = (globalDragState.currentPosition.y - imageSizePx / 2).roundToInt()
        Box(modifier = modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = globalDragState.resourceId),
                contentDescription = null,
                modifier = Modifier
                    .size(imageSize)
                    .offset {
                        IntOffset(x = offsetX, y = offsetY)
                    }
                    .zIndex(999999f),
                contentScale = ContentScale.Inside
            )
        }
    } else if (!globalDragState.isDragging && globalDragState.resourceId != null) {
        animateOffsetAsState(
            targetValue = globalDragState.startPosition,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            ),
            finishedListener = { _ ->
                onAnimationComplete()
            },
            label = "returnToCarousel"
        )
    }
}