package com.chy.image.region.loader.inter

import android.content.Context
import android.net.Uri
import com.chy.image.region.loader.model.BitmapInfo
import com.chy.image.region.loader.model.BitmapRegionModel

interface LongImageSlicerInterface {
    fun split(context: Context, uri: Uri, bitmapInfo: BitmapInfo): List<BitmapRegionModel>
    fun recycle()
}