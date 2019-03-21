package com.chy.image.region.loader.impl

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.annotation.UiThread
import com.chy.image.region.loader.inter.*
import com.chy.image.region.loader.model.DecodeRegionModel
import com.chy.image.region.loader.util.DecodeRegionExecutor
import java.io.InputStream
import java.util.concurrent.Future

internal open class DefaultImageRegionDecoder<T : ImageFetchInterface>(bitmapFetchInterfaceClazz: Class<T>) :
    ImageRegionDecoder {

    private var mDecodeUrl: String? = null
    private var mImageFetch: ImageFetchInterface = bitmapFetchInterfaceClazz.newInstance()
    private var mBitmapRegionDecoder: BitmapRegionDecoder? = null
    private val decoderLock = Any()
    @Volatile
    private var mNeedFetchBitmap = true
    private val mPendingDecodeRegionModels = mutableListOf<DecodeRegionModel>()
    private val mPendingDecodeRegionCallBacks = mutableListOf<ImageRegionDecodeCallBack>()


    private val mBitmapFetchCallBack: BitmapFetchCallBack = object : BitmapFetchCallBack {
        override fun success(uri: Uri, inputStream: InputStream) {
            checkThread()
            DecodeRegionExecutor.execute(Runnable {
                val bitmapRegionDecoder = BitmapRegionDecoder.newInstance(inputStream, false)
                synchronized(decoderLock) {
                    this@DefaultImageRegionDecoder.mBitmapRegionDecoder = bitmapRegionDecoder
                }
                runOnUIThread {
                    consumePendingQue()
                }
            })
        }

        override fun fail(uri: Uri, exception: Throwable) {
            checkThread()
            mImageFetch.unregisterBitmapFetchCallBack(this)
            mImageFetch = bitmapFetchInterfaceClazz.newInstance()
            mNeedFetchBitmap = true
        }
    }

    private fun consumePendingQue() {
        for (index in 0 until mPendingDecodeRegionModels.size) {
            decodeRegionInternal(mPendingDecodeRegionModels[index], mPendingDecodeRegionCallBacks[index])
        }
        mPendingDecodeRegionModels.clear()
        mPendingDecodeRegionCallBacks.clear()
    }


    private fun runOnUIThread(block: () -> Unit) {
        MainUIHandler.post(block)
    }

    private fun init(decodeRegionModel: DecodeRegionModel) {
        if (mNeedFetchBitmap) {
            mImageFetch.registerBitmapFetchCallBack(mBitmapFetchCallBack)
            mImageFetch.fetchBitmap(decodeRegionModel.context, decodeRegionModel.uri)
            mNeedFetchBitmap = false
            mDecodeUrl = decodeRegionModel.uri.toString()
        }
    }

    private fun decodeRegionInternal(
        decodeRegionModel: DecodeRegionModel,
        imageRegionDecodeCallBack: ImageRegionDecodeCallBack
    ): Future<*> {
        return DecodeRegionExecutor.execute(Runnable {
            var bitmap: Bitmap? = null
            synchronized(decoderLock) {
                val decoderReady = mBitmapRegionDecoder != null && !mBitmapRegionDecoder!!.isRecycled
                if (decoderReady) {
                    val options = BitmapFactory.Options().apply {
                        inSampleSize = decodeRegionModel.sampleSize
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    bitmap = this.mBitmapRegionDecoder!!.decodeRegion(decodeRegionModel.rect, options)
                } else {
                    // 说明外部调用了recycle方法
                }
            }
            runOnUIThread {
                if (bitmap == null) {
                    imageRegionDecodeCallBack.fail(RuntimeException("Region decoder returned null bitmap - image format may not be supported"))
                } else {
                    imageRegionDecodeCallBack.success(bitmap!!)
                }
            }
        })
    }

    private fun addToPendingQueue(
        decodeRegionModel: DecodeRegionModel,
        imageRegionDecodeCallBack: ImageRegionDecodeCallBack
    ) {
        if (!mPendingDecodeRegionModels.contains(decodeRegionModel)) {
            mPendingDecodeRegionModels.add(decodeRegionModel)
        }
        if (!mPendingDecodeRegionCallBacks.contains(imageRegionDecodeCallBack)) {
            mPendingDecodeRegionCallBacks.add(imageRegionDecodeCallBack)
        }
    }

    private fun removeFromPendingQueue(
        decodeRegionModel: DecodeRegionModel,
        imageRegionDecodeCallBack: ImageRegionDecodeCallBack
    ) {
        mPendingDecodeRegionModels.remove(decodeRegionModel)
        mPendingDecodeRegionCallBacks.remove(imageRegionDecodeCallBack)
    }

    @UiThread
    override fun decodeRegion(
        decodeRegionModel: DecodeRegionModel,
        imageRegionDecodeCallBack: ImageRegionDecodeCallBack
    ): ImageRegionDecodeWorker {

        checkThread()

        init(decodeRegionModel)

        if (mDecodeUrl != decodeRegionModel.uri.toString()) {
            throw IllegalArgumentException("与初始化传入的url不匹配")
        }

        val decoderReady = synchronized(decoderLock) {
            mBitmapRegionDecoder != null && !mBitmapRegionDecoder!!.isRecycled
        }

        val future: Future<*>? =
            if (decoderReady) {
                decodeRegionInternal(decodeRegionModel, imageRegionDecodeCallBack)
            } else {
                addToPendingQueue(decodeRegionModel, imageRegionDecodeCallBack)
                null
            }
        return object : ImageRegionDecodeWorker {
            override fun cancel() {
                checkThread()
                future?.cancel(true)
                removeFromPendingQueue(decodeRegionModel, imageRegionDecodeCallBack)
            }
        }
    }

    private fun checkThread() {
        if (Looper.getMainLooper().thread != Thread.currentThread()) {
            throw RuntimeException("应该在主线程调用")
        }
    }


    override fun recycle() {
        checkThread()
        mImageFetch.recycle()
        mImageFetch.unregisterBitmapFetchCallBack(mBitmapFetchCallBack)
        mBitmapRegionDecoder?.recycle()
        mPendingDecodeRegionModels.clear()
        mPendingDecodeRegionCallBacks.clear()
        mDecodeUrl = null
    }

    companion object {
        private val MainUIHandler = Handler(Looper.getMainLooper())
    }
}