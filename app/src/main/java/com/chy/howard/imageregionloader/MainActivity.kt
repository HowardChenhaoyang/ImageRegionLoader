package com.chy.howard.imageregionloader

import android.graphics.Point
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import com.chy.image.region.loader.model.BitmapInfo
import com.chy.image.region.loader.model.BitmapRegionModel
import com.chy.image.region.loader.util.BitmapUtil
import com.chy.image.region.loader.util.ImageRegionDecoderFactory.Companion.ASSET_PREFIX
import com.chy.image.region.loader.util.LongImageSlicerFactory
import com.chy.image.region.loader.view.RegionImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private val mLongImageSlicer = LongImageSlicerFactory.createLongImageSlicer()

    private val mImagesUrls = arrayOf(
        "${ASSET_PREFIX}image01.jpg",
        "${ASSET_PREFIX}image02.jpg",
        "${ASSET_PREFIX}image03.jpg"
    )

    private val mBitmapRegionModels = mutableListOf<BitmapRegionModel>()

    private val mScreenWidth: Int by lazy(LazyThreadSafetyMode.NONE) {
        applicationContext.resources.displayMetrics.widthPixels
    }

    private val mScreenHeight: Int by lazy(LazyThreadSafetyMode.NONE) {
        applicationContext.resources.displayMetrics.heightPixels
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        thread {
            mImagesUrls.forEachIndexed { _, url ->
                val options = BitmapUtil.getBitmapSize(applicationContext, Uri.parse(url))
                val bitmapWidth = options?.outWidth ?: mScreenWidth
                val bitmapHeight = options?.outHeight ?: mScreenHeight

                val bitmapInfo = BitmapInfo(
                    bitmapWidth = bitmapWidth,
                    bitmapHeight = bitmapHeight,
                    requestWidth = mScreenWidth,
                    requestHeight = (mScreenWidth * (bitmapHeight / bitmapWidth.toFloat())).toInt()
                )
                Log.d("MainActivity","bitmapWidth is $bitmapWidth bitmapHeight is $bitmapHeight")
                mBitmapRegionModels.addAll(mLongImageSlicer.split(applicationContext, Uri.parse(url), bitmapInfo))
            }
            recyclerView.adapter!!.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(this).apply { orientation = LinearLayoutManager.VERTICAL }
        recyclerView.adapter = object : RecyclerView.Adapter<MyViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MyViewHolder {
                return MyViewHolder(RegionImageView(applicationContext))
            }

            override fun getItemCount(): Int {
                return mBitmapRegionModels.size
            }

            override fun onBindViewHolder(p0: MyViewHolder, p1: Int) {
                p0.onBind(mBitmapRegionModels[p1])
            }
        }
    }

    private inner class MyViewHolder(private val mItemView: RegionImageView) : RecyclerView.ViewHolder(mItemView) {
        init {
            mItemView.scaleType = ImageView.ScaleType.FIT_XY
            mItemView.layoutParams = RecyclerView.LayoutParams(
                mScreenWidth,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        fun onBind(longImageItem: BitmapRegionModel) {
            mItemView.layoutParams = mItemView.layoutParams.apply {
                width = mScreenWidth
                height = (mScreenWidth * longImageItem.rect.height() / longImageItem.rect.width().toFloat()).toInt()
            }
            mItemView.loadRegion(longImageItem)
        }
    }
}