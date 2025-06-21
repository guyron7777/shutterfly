package com.guyron.shutterfly.di

import android.content.Context
import com.guyron.shutterfly.data.repoaitory.ImageRepositoryImpl
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.ui.viewmodel.ImageManipulatorViewModel

object DependencyContainer {
    private val imageRepository: ImageRepository by lazy { ImageRepositoryImpl() }

    fun provideImageManipulatorViewModel(context: Context): ImageManipulatorViewModel {
        return ImageManipulatorViewModel(imageRepository, context)
    }
}