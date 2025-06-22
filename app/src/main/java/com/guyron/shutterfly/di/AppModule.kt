package com.guyron.shutterfly.di

import com.guyron.shutterfly.data.repoaitory.ImageRepositoryImpl
import com.guyron.shutterfly.domain.repository.ImageRepository
import com.guyron.shutterfly.domain.usecase.AddImageToCanvasUseCase
import com.guyron.shutterfly.domain.usecase.MoveImageUseCase
import com.guyron.shutterfly.domain.usecase.ScaleImageUseCase
import com.guyron.shutterfly.domain.usecase.SelectImageUseCase
import com.guyron.shutterfly.ui.state.DragStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideImageRepository(): ImageRepository {
        return ImageRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideAddImageToCanvasUseCase(): AddImageToCanvasUseCase {
        return AddImageToCanvasUseCase()
    }

    @Provides
    @Singleton
    fun provideMoveImageUseCase(): MoveImageUseCase {
        return MoveImageUseCase()
    }

    @Provides
    @Singleton
    fun provideScaleImageUseCase(): ScaleImageUseCase {
        return ScaleImageUseCase()
    }

    @Provides
    @Singleton
    fun provideSelectImageUseCase(): SelectImageUseCase {
        return SelectImageUseCase()
    }

    @Provides
    @Singleton
    fun provideDragStateManager(): DragStateManager {
        return DragStateManager()
    }
} 