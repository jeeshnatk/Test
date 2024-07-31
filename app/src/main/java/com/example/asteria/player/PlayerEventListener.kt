package com.example.asteria.player

import androidx.media3.common.Player
import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.exoplayer.ExoPlayer
import com.example.asteria.utils.CloudLogger

class PlayerEventListener(private val player: ExoPlayer) : Player.Listener {
    fun setupListeners() {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> Log.d("ExoPlayer", "Player is idle")
                    Player.STATE_BUFFERING -> Log.d("ExoPlayer", "Player is buffering")
                    Player.STATE_READY -> {




                    }
                    Player.STATE_ENDED -> Log.d("ExoPlayer", "Playback ended")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                CloudLogger.devicelog("Player error: ${error.errorCodeName} - ${error.message}")
                error.cause?.let {
                    Log.e("Asteria_Device", "Cause: ${it.message}", it)
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                CloudLogger.devicelog("Is playing changed: $isPlaying")
                if (isPlaying) {
                    val currentMediaItem = player.currentMediaItem
                    val mediaUri = currentMediaItem?.localConfiguration?.uri.toString()
                    Log.d("PlayerManager", "Now playing: $mediaUri")
                    CloudLogger.devicelog("Now playing: $mediaUri")
                }
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                CloudLogger.devicelog("Is loading changed: $isLoading")
                if (isLoading) {
                    val currentMediaItem = player.currentMediaItem
                    val mediaUri = currentMediaItem?.localConfiguration?.uri.toString()
                    CloudLogger.devicelog("Now loading: $mediaUri")
                }
            }
        })
    }
}
