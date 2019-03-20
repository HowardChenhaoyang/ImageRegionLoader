package com.chy.image.region.loader.util

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit

internal object OkHttpInstance {

    private var okHttpClient: OkHttpClient? = null

    @Synchronized
    fun getOkHttpClient(context: Context): OkHttpClient {
        if (this.okHttpClient != null) {
            return this.okHttpClient!!
        }
        val okHttpClient = createOkHttpClient(context.applicationContext)
        this.okHttpClient = okHttpClient
        return okHttpClient
    }

    private fun createOkHttpClient(context: Context): OkHttpClient {
        //缓存文件夹
        val cacheFile = File(context.externalCacheDir.toString(), "bitmapRegionCache")
        //缓存大小为10M
        val cacheSize = 10 * 1024L * 1024L
        //创建缓存对象
        val cache = Cache(cacheFile, cacheSize)
        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(20_000, TimeUnit.MILLISECONDS)
            .readTimeout(20_000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
}