package com.guyron.shutterfly.domain.repository

interface ImageRepository {
    fun getSampleImages(): List<Int>
}