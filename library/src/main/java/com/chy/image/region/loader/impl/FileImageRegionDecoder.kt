package com.chy.image.region.loader.impl

internal class FileImageRegionDecoder : DefaultImageRegionDecoder<FileImageFetchImpl>(FileImageFetchImpl::class.java)