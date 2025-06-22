package com.guyron.shutterfly.di

import com.guyron.shutterfly.data.repoaitory.ImageRepositoryImpl
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.ui.viewmodel.ImageManipulatorViewModel

object DependencyContainer {
    private val imageRepository: ImageRepository by lazy { ImageRepositoryImpl() }

    fun provideImageManipulatorViewModel(): ImageManipulatorViewModel {
        return ImageManipulatorViewModel(imageRepository)
    }
}