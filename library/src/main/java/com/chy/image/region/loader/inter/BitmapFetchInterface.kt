package com.chy.image.region.loader.inter

import android.content.Context
import android.net.Uri

interface BitmapFetchInterface {
    fun fetchBitmap(context: Context, uri: Uri)
    fun recycle()
    fun registerBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack)
    fun unregisterBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack)
}