package com.chy.image.region.loader.impl

import com.chy.image.region.loader.inter.ImageRegionDecoder
import android.content.Context
import android.graphics.Rect
import android.net.Uri
import com.chy.image.region.loader.inter.LongImageSlicerInterface
import com.chy.image.region.loader.model.BitmapInfo
import com.chy.image.region.loader.model.BitmapRegionModel

internal class DefaultLongImageSlicerImpl : LongImageSlicerInterface {

    private var splitCount = 1
    private val decoders = mutableMapOf<String, ImageRegionDecoder>()

    override fun split(context: Context, uri: Uri, bitmapInfo: BitmapInfo): List<BitmapRegionModel> {

        val decoder =
            if (decoders[uri.toString()] != null) {
                decoders[uri.toString()]!!
            } else {
                if (uri.toString().startsWith("http")) OkHttpImageRegionDecoder() else FileBitmapRegionDecoder()
            }

        val (bitmapWidth, bitmapHeight, requestWidth, _) = bitmapInfo

        val itemHeight = context.resources.displayMetrics.heightPixels / 4

        while (itemHeight * splitCount < bitmapHeight) {
            splitCount++
        }


        val bitmapRegionModels = mutableListOf<BitmapRegionModel>()

        var sampleSize = 1
        while (requestWidth * sampleSize < bitmapWidth) {
            sampleSize *= 2
        }

        for (i in (0 until splitCount)) {
            val sRect = Rect(
                0,
                i * itemHeight,
                bitmapWidth,
                if (i == splitCount - 1) bitmapHeight else (i + 1) * itemHeight
            )
            bitmapRegionModels.add(
                BitmapRegionModel(
                    context = context,
                    uri = uri,
                    decoder = decoder,
                    rect = sRect,
                    sampleSize = sampleSize
                )
            )
        }
        decoders[uri.toString()] = decoder
        return bitmapRegionModels
    }

    override fun recycle() {
        decoders.forEach {
            it.value.recycle()
        }
        decoders.clear()
    }
}