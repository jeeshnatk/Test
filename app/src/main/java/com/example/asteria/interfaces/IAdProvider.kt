package com.example.asteria.interfaces

import androidx.media3.common.MediaItem

interface IAdProvider {
    fun getMediaItems(): Array <MediaItem>
}