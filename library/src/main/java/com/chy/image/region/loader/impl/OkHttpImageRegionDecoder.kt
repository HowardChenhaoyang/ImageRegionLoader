package com.chy.image.region.loader.impl

internal class OkHttpImageRegionDecoder : DefaultImageRegionDecoder<OkHttpImageFetchImpl>(OkHttpImageFetchImpl::class.java)