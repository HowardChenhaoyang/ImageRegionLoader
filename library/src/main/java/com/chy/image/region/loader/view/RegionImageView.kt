package com.chy.image.region.loader.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.widget.ImageView
import com.chy.image.region.loader.inter.ImageRegionDecodeCallBack
import com.chy.image.region.loader.inter.ImageRegionDecodeWorker
import com.chy.image.region.loader.model.BitmapRegionModel
import com.chy.image.region.loader.model.DecodeRegionModel

class RegionImageView : ImageView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var imageRegionDecodeWorker: ImageRegionDecodeWorker? = null
    private var bitmapRegionModel: BitmapRegionModel? = null
    private var bitmap: Bitmap? = null

    fun loadRegion(bitmapRegionModel: BitmapRegionModel) {
        post {
            if (this.bitmapRegionModel == bitmapRegionModel && this.bitmap != null && !this.bitmap!!.isRecycled) {
                setImageBitmap(bitmap)
                return@post
            }
            this.bitmap = null
            setImageBitmap(null)
            release()
            this.bitmapRegionModel = bitmapRegionModel
            loadRegionInternal(bitmapRegionModel)
        }
    }

    private fun loadRegionInternal(bitmapRegionModel: BitmapRegionModel) {
        val decodeRegionModel = DecodeRegionModel(
            context = context,
            uri = bitmapRegionModel.uri,
            rect = bitmapRegionModel.rect,
            sampleSize = bitmapRegionModel.sampleSize
        )
        imageRegionDecodeWorker = bitmapRegionModel.decoder.decodeRegion(decodeRegionModel,
            object : ImageRegionDecodeCallBack {
                override fun success(bitmap: Bitmap) {
                    this@RegionImageView.bitmap = bitmap
                    setImageBitmap(bitmap)
                }

                override fun fail(exception: Throwable) {

                }
            })
    }

    private fun release() {
        imageRegionDecodeWorker?.cancel()
        imageRegionDecodeWorker = null
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (this.bitmap != null && !this.bitmap!!.isRecycled) {
            setImageBitmap(this.bitmap)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        release()
    }
}