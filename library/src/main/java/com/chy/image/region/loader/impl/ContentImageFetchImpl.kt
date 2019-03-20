package com.chy.image.region.loader.impl

import android.content.Context
import android.net.Uri
import com.chy.image.region.loader.inter.BitmapFetchCallBack
import com.chy.image.region.loader.inter.BitmapFetchInterface
import java.io.FileNotFoundException
import java.io.InputStream

class ContentImageFetchImpl : BitmapFetchInterface {

    override fun recycle() {
        bitmapFetchCallBacks.clear()
    }

    private val bitmapFetchCallBacks: MutableList<BitmapFetchCallBack> by lazy {
        mutableListOf<BitmapFetchCallBack>()
    }

    override fun fetchBitmap(context: Context, uri: Uri) {
        var inputStream: InputStream?
        try {
            inputStream = context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            for (bitmapFetchCallBack in bitmapFetchCallBacks) {
                bitmapFetchCallBack.fail(uri, e)
            }
            return
        }
        if (inputStream == null) {
            for (bitmapFetchCallBack in bitmapFetchCallBacks) {
                bitmapFetchCallBack.fail(uri, FileNotFoundException("uri is $uri"))
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