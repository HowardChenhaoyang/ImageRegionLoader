package com.chy.image.region.loader.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.InputStream

class BitmapUtil {

    companion object {

        fun getBitmapSize(context: Context, uri: Uri): BitmapFactory.Options? {
            val uriString = uri.toString()
            val inputStream: InputStream?
            inputStream = when {
                uriString.startsWith(ImageRegionDecoderFactory.ASSET_PREFIX) -> {
                    InputStreamFactory.createInputStreamFromAssets(context, uri)
                }
                uriString.startsWith(ImageRegionDecoderFactory.FILE_PREFIX) -> {
                    InputStreamFactory.createInputStreamFromFile(context, uri)
                }
                uriString.startsWith(ImageRegionDecoderFactory.HTTP_PREFIX) -> {
                    InputStreamFactory.createInputStreamFromServer(context, uri)
                }
                else -> InputStreamFactory.createInputStreamFromContent(context, uri)
            }
            if (inputStream != null) {
                val decodeOptions = BitmapFactory.Options()
                decodeOptions.inJustDecodeBounds = true
                try {
                    BitmapFactory.decodeStream(inputStream, null, decodeOptions)
                } catch (e: Exception) {
                    Log.e("BitmapUtil", e.message, e)
                }
                return decodeOptions
            }
            return null
        }
    }
}