package com.chy.image.region.loader.inter

import android.graphics.Bitmap
import com.chy.image.region.loader.model.DecodeRegionModel

interface ImageRegionDecodeCallBack {
    fun success(bitmap: Bitmap)
    fun fail(exception: Throwable)
}

interface ImageRegionDecodeWorker {
    fun cancel()
}

interface ImageRegionDecoder {

    fun decodeRegion(
        decodeRegionModel: DecodeRegionModel,
        imageRegionDecodeCallBack: ImageRegionDecodeCallBack
    ): ImageRegionDecodeWorker

    fun recycle()
}