package com.chy.image.region.loader.impl

import android.content.Context
import android.net.Uri
import com.chy.image.region.loader.inter.BitmapFetchCallBack
import com.chy.image.region.loader.inter.BitmapFetchInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

internal class FileImageFetchImpl : BitmapFetchInterface {

    override fun recycle() {
        bitmapFetchCallBacks.clear()
    }

    private val bitmapFetchCallBacks: MutableList<BitmapFetchCallBack> by lazy {
        mutableListOf<BitmapFetchCallBack>()
    }

    override fun fetchBitmap(context: Context, uri: Uri) {
        val file = File(uri.toString())
        if (!file.exists()) {
            for (bitmapFetchCallBack in bitmapFetchCallBacks) {
                bitmapFetchCallBack.fail(uri, FileNotFoundException("file not exist"))
            }
            return
        }
        val inputStream: FileInputStream
        try {
            inputStream = file.inputStream()
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