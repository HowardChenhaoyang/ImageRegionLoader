package com.chy.image.region.loader.util

import android.net.Uri
import com.chy.image.region.loader.impl.*
import com.chy.image.region.loader.inter.ImageRegionDecoder

class ImageRegionDecoderFactory {
    companion object {

        const val FILE_PREFIX = "file://"
        const val ASSET_PREFIX = "$FILE_PREFIX/android_asset/"
        const val HTTP_PREFIX = "http"

        fun createImageRegionDecoder(uri: Uri): ImageRegionDecoder {
            val uriString = uri.toString()
            return when {
                uriString.startsWith(ASSET_PREFIX) -> {
                    AssetImageRegionDecoder()
                }
                uriString.startsWith(FILE_PREFIX) -> {
                    FileImageRegionDecoder()
                }
                uriString.startsWith(HTTP_PREFIX) -> {
                    OkHttpImageRegionDecoder()
                }
                else -> ContentImageRegionDecoder()
            }
        }
    }
}