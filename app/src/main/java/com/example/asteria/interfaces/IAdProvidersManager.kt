package com.example.asteria.interfaces

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.source.MediaSource

interface IAdProvidersManager {
    fun registerAdProvider(adProvider: IAdProvider)
    fun fetchMediaItems(): MutableList<MediaItem>
    fun getMediaSource(): MediaSource
}