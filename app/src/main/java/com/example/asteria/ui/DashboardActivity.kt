package com.example.asteria

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.ui.PlayerView

import com.example.asteria.utils.CloudLogger
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.asteria.downloadservice.AdDownloadService
import com.example.asteria.downloadservice.DownloadUtil
import com.example.asteria.player.PlayerManager
import com.example.asteria.player.PlayerManagerSingleton



@UnstableApi
class DashboardActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var playerManager: PlayerManager
    private lateinit var player: ExoPlayer

    @OptIn(UnstableApi::class)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()
        AdDownloadService.start(this)
        // Initialize the CloudLogger
        CloudLogger.initialize()
        setContentView(R.layout.activity_dashboard)
        // setContentView(R.layout.activity_main)
        hideSystemUI()
        DownloadUtil.initialize(this)


        //set the Player Manager with the Player view and the AdProviders
        playerView = findViewById(R.id.video_view)
        playerManager = PlayerManager(this, playerView,player)
        PlayerManagerSingleton.playerManager = playerManager
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerManager.playdefaultMedia( MediaItem.fromUri("https://adtest-2.s3.us-east-2.amazonaws.com/folder1/movies/2.mp4"))


    }



    private  fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.close()
    }

}

