package com.chy.image.region.loader.util

import com.chy.image.region.loader.impl.DefaultLongImageSlicerImpl
import com.chy.image.region.loader.inter.LongImageSlicerInterface

class LongImageSlicerFactory {
    companion object {
        @JvmStatic
        fun createLongImageSlicer(): LongImageSlicerInterface {
            return DefaultLongImageSlicerImpl()
        }
    }
}