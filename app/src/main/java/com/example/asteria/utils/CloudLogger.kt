package com.example.asteria.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.asteria.utils.CloudLogger.logDetails


object CloudLogger {
    fun initialize() {
        // Initialize cloud logging
    }

    fun addAwsLogs(tag: String, data: Map<String, Any>) {
        // Add logs to AWS
    }

    fun devicelog(message: String) {
        // Log the message
        Log.d("Asteria_Device:", message)
    }

    fun MediaItem?.logDetails(className: String) {
        if (this != null) {
            val mediaUri = this.localConfiguration?.uri.toString()
            val mediaId = this.mediaId
            val mimeType = this.localConfiguration?.mimeType
            val title = this.mediaMetadata.title
            val artist = this.mediaMetadata.artist

            CloudLogger.devicelog("$className: MediaItem Details:")
            CloudLogger.devicelog("$className: URI: $mediaUri")
            CloudLogger.devicelog("$className: Media ID: $mediaId")
            CloudLogger.devicelog("$className: MIME Type: $mimeType")
            CloudLogger.devicelog("$className: Title: $title")
        } else {
            CloudLogger.devicelog("$className: MediaItem is null")
        }
    }

    @OptIn(UnstableApi::class)
    fun ConcatenatingMediaSource?.logDetails(className: String) {
        if (this != null) {
            val totalMediaItems = this.size
            CloudLogger.devicelog("Total number of media items in the ConcatenatingMediaSource : $totalMediaItems")

            for (i in 0 until totalMediaItems) {
                val mediaSourceHolder = this.getMediaSource(i) as? ProgressiveMediaSource
                val mediaItem = mediaSourceHolder?.mediaItem
                if(mediaItem == null)
                     CloudLogger.devicelog("mediaSourceHolder?.mediaItem is null")
                mediaItem.logDetails(className)
            }
        } else {
            CloudLogger.devicelog("$className: ConcatenatingMediaSource is null")
        }
    }

}
