package com.example.asteria.downloadservice

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.offline.DefaultDownloaderFactory
import com.example.asteria.utils.CloudLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@UnstableApi
object DownloadUtil {
    private var downloadManager: DownloadManager? = null
    private var scheduler: PlatformScheduler? = null
    private var cache: SimpleCache? = null
    private var httpDataSourceFactory: DefaultHttpDataSource.Factory? = null
    private lateinit var cacheDirectory: File

    fun initialize(context: Context) {
        cacheDirectory = File(context.cacheDir, "romy_downloads")
    }

    fun getDownloadManager(context: Context): DownloadManager {
        if (downloadManager == null) {

           // cacheDirectory = getCacheDirectory(context)

            val databaseProvider = StandaloneDatabaseProvider(context)
            val downloadCache = getCache(context)
            //  httpDataSourceFactory = DefaultDownloaderFactory.Factory()
            val cacheDataSourceFactory = getCacheDataSourceFactory(context)

            downloadManager = DownloadManager(
                context,
                databaseProvider,
                downloadCache,
                cacheDataSourceFactory,
                Executors.newFixedThreadPool(1)
            )
        }
        return downloadManager!!
    }

    fun getScheduler(context: Context): PlatformScheduler? {
        if (scheduler == null) {
            scheduler = PlatformScheduler(context, 1)
        }
        return scheduler
    }


//    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
//
//        httpDataSourceFactory = DefaultHttpDataSource.Factory()
//        return CacheDataSource.Factory().setCache(getCache(context))
//            .setUpstreamDataSourceFactory(httpDataSourceFactory)
//            .setCacheWriteDataSinkFactory(null) // Disable writing.
//    }

    fun getCacheDataSourceFactory(context: Context): CacheDataSource.Factory {
        val dataSourceFactory = DefaultDataSource.Factory(context)
        return CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(getCache(context)))
    }

    fun getCachedMediaUri(context: Context, url: String): String? {
        val fileName = getFileNameFromUrl(url)
        val cacheFile = File(cacheDirectory, fileName)
        return if (cacheFile.exists()) {
            cacheFile.absolutePath
        } else {
            null
        }
    }

    private fun getFileNameFromUrl(url: String): String {
        val uri = URL(url)
        val path = uri.path
        return File(path).name
    }
    fun getCache(context: Context): SimpleCache {
        if (cache == null) {
            CloudLogger.devicelog("Cache Directory: ${context.cacheDir}")
           // val downloadContentDirectory = File(context.cacheDir, "romy_downloads")
            //.getExternalFilesDir(null), "romy_downloads")
            // val cacheSize = 1000 * 1024 * 1024L // 1000 MB
            // val cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)
            cache = SimpleCache(
                cacheDirectory,
                NoOpCacheEvictor(),
                StandaloneDatabaseProvider(context)
            )
        }
        return cache!!
    }

    suspend fun downloadMedia(context: Context, url: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        val fileName = getFileNameFromUrl(url)
        val cacheFile = File(cacheDirectory, fileName)

        //  withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful)
                throw IOException("Unexpected code $response")

           // val file = File(cacheDirectory, "/${url.substringAfterLast("/")}")

            //val file = File(context.getExternalFilesDir(null), getLocalStorageFileName(context, url))

            response.body?.byteStream()?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            //   }
        }
    }

    fun isMediaCached(context: Context, url: String): Boolean {
        try {
            val fileName = getFileNameFromUrl(url)
            return File(cacheDirectory, fileName).exists()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
