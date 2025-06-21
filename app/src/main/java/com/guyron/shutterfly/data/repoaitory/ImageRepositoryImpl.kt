package com.guyron.shutterfly.data.repoaitory

import com.guyron.shutterfly.constants.AppConstants
import com.guyron.shutterfly.domain.repository.ImageRepository

class ImageRepositoryImpl : ImageRepository {
    override fun getSampleImages(): List<Int> = AppConstants.Resources.SAMPLE_IMAGES
}