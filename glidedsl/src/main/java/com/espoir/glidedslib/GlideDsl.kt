package com.espoir.glidedslib

import android.app.Application

object GlideDsl {
    internal lateinit var sAppContext: Application
    internal var imageUrlInterceptor: ImageUrlInterceptor? = null

    fun init(application: Application) = apply {
        sAppContext = application
    }

    fun setImageUrlInterceptor(interceptor: ImageUrlInterceptor) {
        imageUrlInterceptor = interceptor
    }
}