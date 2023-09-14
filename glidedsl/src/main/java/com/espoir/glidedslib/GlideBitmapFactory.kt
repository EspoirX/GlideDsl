package com.espoir.glidedslib

import android.graphics.Bitmap
import com.bumptech.glide.Glide

/**
 * Description:收拢图片创建
 */
object GlideBitmapFactory {
    private val bitmapPool by lazy {
        Glide.get(GlideDsl.sAppContext).bitmapPool
    }

    @JvmStatic
    fun createBitmap(width: Int, height: Int, config: Bitmap.Config): Bitmap {
        return bitmapPool.get(width, height, config)
    }
}