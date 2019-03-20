package com.chy.image.region.loader.impl

internal class OkHttpImageRegionDecoder : DefaultImageRegionDecoder<OkHttpBitmapFetchImpl>(OkHttpBitmapFetchImpl::class.java)