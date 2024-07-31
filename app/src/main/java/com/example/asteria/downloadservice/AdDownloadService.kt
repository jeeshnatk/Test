package com.example.asteria.downloadservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import com.example.asteria.player.PlayerManagerSingleton
import com.example.asteria.utils.CloudLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


//https://developer.android.com/media/media3/exoplayer/downloading-media
//https://medium.com/@eguven

@UnstableApi
class AdDownloadService : DownloadService(FOREGROUND_NOTIFICATION_ID) {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(
            FOREGROUND_NOTIFICATION_ID,
            createNotification("Ad Download Service is running")
        )

    }

    private fun createNotification(content: String): Notification {

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Ad Download Service")
            .setContentText(content)
            // .setSmallIcon(R.) // Replace with your app's icon
            .build()
    }

    @OptIn(UnstableApi::class)
    override fun getDownloadManager(): DownloadManager {
        return DownloadUtil.getDownloadManager(this)
    }

    override fun getScheduler(): PlatformScheduler? {
        CloudLogger.devicelog("PlatformScheduler. What is this for ? ")
        //todo: implement scheduler
        return DownloadUtil.getScheduler(this)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        TODO("Not yet implemented")
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Ad Download Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startDownloadingAndAddingMedia()
        return START_NOT_STICKY
    }

    private fun startDownloadingAndAddingMedia() {
        CoroutineScope(Dispatchers.IO).launch {
            val mediaList = fetchMediaList()
            mediaList?.forEach { media ->
                if (!DownloadUtil.isMediaCached(
                        this@AdDownloadService,
                        media.localConfiguration?.uri.toString()
                    )
                ) {
                    DownloadUtil.downloadMedia(
                        this@AdDownloadService,
                        media.localConfiguration?.uri.toString()
                    )

                    CloudLogger.devicelog("downloaded media: " + media.localConfiguration?.uri.toString())
                } else {
                    CloudLogger.devicelog("media already cached")
                }


                //Update the Player Via the PlayerManager
//                withContext(Dispatchers.Main) {
//                    CloudLogger.devicelog("downloaded media ")
//                    val mediaUri = media.localConfiguration?.uri.toString()
//                    CloudLogger.devicelog("Downloaded media: $mediaUri")
//
//                    // Add the downloaded media to the player
//                    PlayerManagerSingleton.playerManager?.updateMedialist(media)
//
//                    CloudLogger.devicelog("updateMedialist done : $mediaUri")
//                }
            }

            mediaList?.forEach { media ->

                //Update the Player Via the PlayerManager
                withContext(Dispatchers.Main) {
                    CloudLogger.devicelog("downloaded media ")
                    val mediaUri = media.localConfiguration?.uri.toString()
                    CloudLogger.devicelog("Downloaded media: $mediaUri")

                    // Add the downloaded media to the player
                    PlayerManagerSingleton.playerManager?.updateMedialist(media)

                    CloudLogger.devicelog("updateMedialist done : $mediaUri")
                }
            }
        }
    }

//    fun getMediaItemUri(mediaItem: MediaItem): Uri {
//        return mediaItem.localConfiguration.uri
//    }

    private fun fetchMediaList(): List<MediaItem> {
        return listOf(
           // MediaItem.Builder()
               // .setUri("https://adtest-2.s3.us-east-2.amazonaws.com/folder1/movies/10.mp4")
              //  .build()
            // MediaItem.Builder().setUri("https://adtest-2.s3.us-east-2.amazonaws.com/folder1/movies/11.mp4").build(),

//            MediaItem.Builder()
//                .setUri("https://adtest-2.s3.us-east-2.amazonaws.com/folder1/movies/2.mp4")
//                .setMediaId(UUID.randomUUID().toString())
//                .build(),
            MediaItem.Builder()
                .setUri("https://d3txy5owctt379.cloudfront.net/70d0ec20-e518-11ee-86e4-a5c61fb0080a.mp4")
                .setMediaId(UUID.randomUUID().toString())
                .build(),
             MediaItem.Builder()
                 .setUri("https://d3txy5owctt379.cloudfront.net/6cb36250-06a5-11ee-a93d-57a30e290e0d.mp4")
                 .setMediaId(UUID.randomUUID().toString())
                 .build()
        )
    }


    companion object {
        private const val FOREGROUND_NOTIFICATION_ID = 1
        const val CHANNEL_ID = "download_channel"

        fun start(context: Context) {

            val intent = Intent(context, AdDownloadService::class.java)
            Util.startForegroundService(context, intent)
        }
    }
}
