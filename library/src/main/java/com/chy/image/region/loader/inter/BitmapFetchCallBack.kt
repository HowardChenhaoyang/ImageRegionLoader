package com.chy.image.region.loader.inter

import android.net.Uri
import java.io.InputStream

interface BitmapFetchCallBack {
    fun success(uri: Uri, inputStream: InputStream)
    fun fail(uri: Uri, exception: Throwable)
}