package com.guyron.shutterfly.data.repoaitory

import com.guyron.shutterfly.constants.AppConstants
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ImageRepositoryImplTest {
    @Test
    fun `getSampleImages returns correct images`() {
        val repo = ImageRepositoryImpl()
        val images = repo.getSampleImages()
        assertThat(images).isEqualTo(AppConstants.Resources.SAMPLE_IMAGES)
    }
} 