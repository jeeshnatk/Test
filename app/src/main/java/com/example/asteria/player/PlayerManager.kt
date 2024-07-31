package com.example.asteria.player

import android.content.Context
import android.view.View
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.asteria.downloadservice.DownloadUtil
import com.example.asteria.utils.CloudLogger
import com.example.asteria.utils.CloudLogger.logDetails

@UnstableApi
object PlayerManagerSingleton {
    var playerManager: PlayerManager? = null
}

@UnstableApi
class PlayerManager(
    private val context: Context,
    private val playerView: PlayerView,
    private  val player: ExoPlayer,
) {


    private val concatenatingMediaSource = ConcatenatingMediaSource()
    private var playerEventListener: PlayerEventListener? = null//PlayerEventListener(player)

    init {
        playerView.player = player
        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.playWhenReady = true
        playerEventListener = PlayerEventListener(player!!)
        playerEventListener!!.setupListeners()
    }

    fun updateMedialist(mediaItem: MediaItem) {
        CloudLogger.devicelog("Media Item received for update. Logging b4 update")
        mediaItem.logDetails(this.javaClass.name)

        val dataSourceFactory = DownloadUtil.getCacheDataSourceFactory(context)
        val cachedUri =
            DownloadUtil.getCachedMediaUri(context, mediaItem.localConfiguration?.uri.toString())
        val uriToUse = cachedUri ?: mediaItem.localConfiguration?.uri.toString()
        val updatedMediaItem = MediaItem.Builder()
            .setUri(uriToUse)
            .setTag(mediaItem.mediaMetadata.title) // or any other tag
            .build()

        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(updatedMediaItem)

        concatenatingMediaSource.addMediaSource(mediaSource)
        player!!.setMediaSource(concatenatingMediaSource)
        player!!.prepare()
        CloudLogger.devicelog("Media Item received for update. Logging Af8r update")
        mediaItem.logDetails(this.javaClass.name)
        concatenatingMediaSource.logDetails(this.javaClass.name)
        CloudLogger.devicelog("player manager concatenated media item")
    }

    fun playdefaultMedia(mediaItem: MediaItem) {
        updateMedialist(mediaItem)
        CloudLogger.devicelog("play default Media done")
    }

    fun close() {
        player?.release()
    }
}
