package com.chy.image.region.loader.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.InputStream

class InputStreamFactory {
    companion object {
        fun createInputStreamFromAssets(context: Context, uri: Uri): InputStream? {
            try {
                val assetFileName = uri.toString().substring(ImageRegionDecoderFactory.ASSET_PREFIX.length)
                return context.resources.assets.open(assetFileName)
            } catch (e: Exception) {
                Log.e("InputStreamFactory", e.message, e)
            }
            return null
        }

        fun createInputStreamFromFile(context: Context, uri: Uri): InputStream? {
            try {
                val filePath = uri.toString().substring(ImageRegionDecoderFactory.FILE_PREFIX.length)
                return File(filePath).inputStream()
            } catch (e: Exception) {
                Log.e("InputStreamFactory", e.message, e)
            }
            return null
        }

        fun createInputStreamFromContent(context: Context, uri: Uri): InputStream? {
            try {
                return context.contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                Log.e("InputStreamFactory", e.message, e)
            }
            return null
        }

        fun createInputStreamFromServer(context: Context, uri: Uri): InputStream? {
            try {
                val callClient = OkHttpInstance.getOkHttpClient(context)
                val builder = okhttp3.Request.Builder().url(uri.toString())
                val call = callClient.newCall(builder.build())
                return call.execute().body()?.byteStream()
            } catch (e: Exception) {
                Log.e("InputStreamFactory", e.message, e)
            }
            return null
        }
    }
}