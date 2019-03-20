package com.chy.image.region.loader.model

import com.chy.image.region.loader.inter.ImageRegionDecoder
import android.content.Context
import android.graphics.Rect
import android.net.Uri

data class BitmapRegionModel(
    val context: Context,
    val uri: Uri,
    var decoder: ImageRegionDecoder,
    var rect: Rect,
    var sampleSize: Int
)