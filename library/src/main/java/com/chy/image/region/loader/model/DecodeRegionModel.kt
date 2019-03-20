package com.chy.image.region.loader.model

import android.content.Context
import android.graphics.Rect
import android.net.Uri

data class DecodeRegionModel(
    val context: Context,
    val uri: Uri,
    val rect: Rect,
    val sampleSize: Int
)