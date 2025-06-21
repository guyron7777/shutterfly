package com.guyron.shutterfly.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.guyron.shutterfly.R
import com.guyron.shutterfly.ui.state.ImageManipulatorAction


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageManipulatorTopBar(
    canUndo: Boolean,
    canRedo: Boolean,
    onAction: (ImageManipulatorAction) -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.application_title)) },
        actions = {
            IconButton(
                onClick = { onAction(ImageManipulatorAction.Undo) },
                enabled = canUndo
            ) {
                Icon(Icons.AutoMirrored.Default.Undo, contentDescription = stringResource(R.string.undo))
            }

            IconButton(
                onClick = { onAction(ImageManipulatorAction.Redo) },
                enabled = canRedo
            ) {
                Icon(Icons.AutoMirrored.Default.Redo, contentDescription = stringResource(R.string.redo))
            }
        }
    )
}