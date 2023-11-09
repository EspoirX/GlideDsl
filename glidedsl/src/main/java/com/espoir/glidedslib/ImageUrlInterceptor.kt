package com.espoir.glidedslib

interface ImageUrlInterceptor {
    fun getUrl(url: String, options: ImageOptions): String
}