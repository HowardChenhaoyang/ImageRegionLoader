package com.chy.image.region.loader.impl

import android.content.Context
import android.net.Uri
import com.chy.image.region.loader.inter.BitmapFetchCallBack
import com.chy.image.region.loader.inter.BitmapFetchInterface
import com.chy.image.region.loader.util.ImageRegionDecoderFactory.Companion.ASSET_PREFIX
import java.io.InputStream

internal class AssetImageFetchImpl : BitmapFetchInterface {

    override fun recycle() {
        bitmapFetchCallBacks.clear()
    }

    private val bitmapFetchCallBacks: MutableList<BitmapFetchCallBack> by lazy {
        mutableListOf<BitmapFetchCallBack>()
    }

    override fun fetchBitmap(context: Context, uri: Uri) {
        val inputStream: InputStream
        try {
            val assetFileName = uri.toString().substring(ASSET_PREFIX.length)
            inputStream = context.resources.assets.open(assetFileName)
        } catch (e: Exception) {
            for (bitmapFetchCallBack in bitmapFetchCallBacks) {
                bitmapFetchCallBack.fail(uri, e)
            }
            return
        }
        for (bitmapFetchCallBack in bitmapFetchCallBacks) {
            bitmapFetchCallBack.success(uri, inputStream)
        }
    }

    override fun registerBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack) {
        if (!bitmapFetchCallBacks.contains(bitmapFetchCallBack)) {
            bitmapFetchCallBacks.add(bitmapFetchCallBack)
        }
    }

    override fun unregisterBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack) {
        bitmapFetchCallBacks.remove(bitmapFetchCallBack)
    }
}