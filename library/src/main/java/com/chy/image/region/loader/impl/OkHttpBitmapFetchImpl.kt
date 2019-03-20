package com.chy.image.region.loader.impl

import com.chy.image.region.loader.inter.BitmapFetchCallBack
import com.chy.image.region.loader.inter.BitmapFetchInterface
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import okhttp3.Call
import com.chy.image.region.loader.util.DecodeRegionExecutor
import com.chy.image.region.loader.util.OkHttpInstance
import java.io.IOException

internal class OkHttpBitmapFetchImpl : BitmapFetchInterface {

    override fun recycle() {
        this.call?.cancel()
        bitmapFetchCallBacks.clear()
    }

    private val bitmapFetchCallBacks: MutableList<BitmapFetchCallBack> by lazy {
        mutableListOf<BitmapFetchCallBack>()
    }

    private var call: Call? = null

    override fun fetchBitmap(context: Context, uri: Uri) {
        if (this.call != null) {
            throw RuntimeException("该方法仅仅可调用一次")
        }
        DecodeRegionExecutor.execute(Runnable {
            val callClient = OkHttpInstance.getOkHttpClient(context)
            val builder = okhttp3.Request.Builder().url(uri.toString())
            val call = callClient.newCall(builder.build())
            this.call = call
            val response = call.execute()
            val responseCode = response.code()
            if (responseCode >= 300) {
                response.body()?.close()
                for (bitmapFetchCallBack in bitmapFetchCallBacks.toTypedArray()) {
                    bitmapFetchCallBack.fail(
                        uri, IOException(
                            "OkHttpRegionDecoder: " + responseCode + " "
                                    + response.message()
                        )
                    )
                }
                return@Runnable
            }
            val inputStream = response.body()?.byteStream()
            if (inputStream == null) {
                for (bitmapFetchCallBack in bitmapFetchCallBacks.toTypedArray()) {
                    bitmapFetchCallBack.fail(
                        uri, IOException(
                            "OkHttpRegionDecoder: " + responseCode + " "
                                    + response.message()
                        )
                    )
                }
                return@Runnable
            }
            MainUIHandler.post {
                for (bitmapFetchCallBack in bitmapFetchCallBacks.toTypedArray()) {
                    bitmapFetchCallBack.success(uri, inputStream)
                }
            }
        })
    }

    @UiThread
    override fun registerBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack) {
        if (!bitmapFetchCallBacks.contains(bitmapFetchCallBack)) {
            bitmapFetchCallBacks.add(bitmapFetchCallBack)
        }
    }

    @UiThread
    override fun unregisterBitmapFetchCallBack(bitmapFetchCallBack: BitmapFetchCallBack) {
        bitmapFetchCallBacks.remove(bitmapFetchCallBack)
    }

    companion object {
        private val MainUIHandler = Handler(Looper.getMainLooper())
    }
}