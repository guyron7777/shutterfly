package com.guyron.shutterfly.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.guyron.shutterfly.R
import com.guyron.shutterfly.ui.state.ImageManipulatorAction


@Composable
fun ImageCarousel(
    images: List<Int>,
    onAction: (ImageManipulatorAction) -> Unit
) {
    Column {
        Text(
            stringResource(R.string.image_gallery),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(images) { resourceId ->
                CarouselImage(
                    resourceId = resourceId,
                    onAction = onAction
                )
            }
        }
    }
}