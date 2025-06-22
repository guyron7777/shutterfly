package com.guyron.shutterfly.constants

import androidx.compose.ui.unit.dp
import com.guyron.shutterfly.R

object AppConstants {

    object MagicNumbers {
        const val Z_INDEX = 1500
        const val MAX_HISTORY_SIZE = 50
        const val MIN_ZOOM = 0.9f
        const val MAX_ZOOM = 4f
        val IMAGE_SIZE_DP = 80.dp
        const val IMAGE_SIZE_F = 80F
    }

    object Gestures {
        val SWIPE_UP_THRESHOLD = 30.dp
    }

    object Resources {
        val SAMPLE_IMAGES = listOf(
            R.drawable.sample1, R.drawable.sample2, R.drawable.sample3,
            R.drawable.sample4, R.drawable.sample5, R.drawable.sample6,
            R.drawable.sample7, R.drawable.sample8
        )
    }
}