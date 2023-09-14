package com.espoir.glidedslib

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.opensource.svgaplayer.SVGADynamicEntity

/**  用法 , 详见 GlideImageLoader 注释
 *
 *   imageView.loadImage("url") {
 *     asGif()
 *     asBitmap()
 *     asSvga()
 *     placeholder()
 *     ...
 *     requestListener {
 *         onBitmapSuccess {  }
 *         onDrawableSuccess {  }
 *         onGifDrawableSuccess {  }
 *         onSvgaSuccess {  }
 *         onLoadFailed {  }
 *     }
 *   }
 */

fun ImageView?.loadImage(res: Any?) {
    GlideImageLoader.loadImage(ImageOptions().also {
        it.res = res
        it.imageView = this@loadImage
        if ((res is String) && res.endsWith(".svga")) {
            it.loadSvga = true
        }
        if (it.loadSvga) {
            it.dynamicItem = createSVGADynamicEntity(it)
        }
    })
}

fun ImageView?.loadImage(res: Any?, options: ImageOptions.() -> Unit) {
    GlideImageLoader.loadImage(ImageOptions().also {
        it.res = res
        it.imageView = this@loadImage
        options(it)
        if (it.loadSvga) {
            it.dynamicItem = createSVGADynamicEntity(it)
        }
    })
}


fun ImageView?.clearGlideRequest() {
    val activity = this?.context?.findActivity() ?: return
    runCatching {
        if (activity?.activityIsAlive()) {
            Glide.with(activity).clear(this)
        }
    }.onFailure {
        Log.e("clearGlideRequest", "clear err:", it)
    }
}


/**
 * 利用glide的加载图片背景，复用图片缓存池子
 */
fun View?.setBackGroundWithGlide(res: Any?) {
    GlideImageLoader.loadImage(ImageOptions().also {
        it.context = this?.context
        it.res = res
        it.intoDrawable(object : OnDrawableListener() {
            override fun onDrawableSuccess(drawable: Drawable) {
                this@setBackGroundWithGlide?.background = drawable
            }
        })
    })
}

fun Context?.loadImage(res: Any?, options: ImageOptions.() -> Unit) {
    GlideImageLoader.loadImage(ImageOptions().also {
        it.res = res
        it.context = this@loadImage
        options(it)
        if (it.loadSvga) {
            it.dynamicItem = createSVGADynamicEntity(it)
        }
    })
}

private fun createSVGADynamicEntity(options: ImageOptions): SVGADynamicEntity? {
    var dynamicItem: SVGADynamicEntity? = null
    if (options.svgaImage.isNotEmpty() || options.svgaText.isNotEmpty()) {
        dynamicItem = SVGADynamicEntity()
        options.svgaImage.forEach {
            dynamicItem.setDynamicImage(it.url, it.key)
        }
        options.svgaText.forEach {
            dynamicItem.setDynamicText(it.text, it.build(), it.key)
        }
    }
    return dynamicItem
}


/**
 * java 调用
 *    ImageLoader.load("")
 *      .context()
 *      .centerCrop()
 *      .asGif()
 *      .into(view)
 */
object ImageLoader {

    var currSetCookie: String? = null

    @JvmStatic
    fun load(res: Any?): ImageOptions {
        return ImageOptions().apply { this.res = res }
    }
}