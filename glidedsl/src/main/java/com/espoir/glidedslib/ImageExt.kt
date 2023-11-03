package com.espoir.glidedslib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.opensource.svgaplayer.SVGADynamicEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
    val imageOptions = ImageOptions().also {
        it.res = res
        it.imageView = this@loadImage
        if ((res is String) && res.endsWith(".svga")) {
            it.loadSvga = true
        }
    }
    if (imageOptions.loadSvga) {
        this?.context?.createSVGADynamicEntity(imageOptions) {
            imageOptions.dynamicItem = it
            GlideImageLoader.loadImage(imageOptions)
        }
    } else {
        GlideImageLoader.loadImage(imageOptions)
    }
}

fun ImageView?.loadImage(res: Any?, options: ImageOptions.() -> Unit) {
    val imageOptions = ImageOptions().also {
        it.res = res
        it.imageView = this@loadImage
        options(it)
    }
    if (imageOptions.loadSvga) {
        this@loadImage?.context?.createSVGADynamicEntity(imageOptions) {
            imageOptions.dynamicItem = it
            GlideImageLoader.loadImage(imageOptions)
        }
    } else {
        GlideImageLoader.loadImage(imageOptions)
    }
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
    val imageOptions = ImageOptions().also {
        it.res = res
        it.context = this@loadImage
        options(it)
    }
    if (imageOptions.loadSvga) {
        this?.createSVGADynamicEntity(imageOptions) {
            imageOptions.dynamicItem = it
            GlideImageLoader.loadImage(imageOptions)
        }
    } else {
        GlideImageLoader.loadImage(imageOptions)
    }
}

private fun Context.createSVGADynamicEntity(options: ImageOptions, callback: (SVGADynamicEntity?) -> Unit) {
    var dynamicItem: SVGADynamicEntity? = null
    if (options.svgaImageBitmap.isNotEmpty() ||
        options.svgaImage.isNotEmpty() ||
        options.svgaText.isNotEmpty()
    ) {
        dynamicItem = SVGADynamicEntity()
        createSVGADynamicImage(dynamicItem, options) {
            options.svgaText.forEach {
                dynamicItem.setDynamicText(it.text, it.build(), it.key)
            }
            callback.invoke(dynamicItem)
        }
    } else {
        callback.invoke(null)
    }
}

private fun Context.createSVGADynamicImage(
    dynamicItem: SVGADynamicEntity?,
    options: ImageOptions,
    callback: (SVGADynamicEntity?) -> Unit
) {
    if (options.svgaImageBitmap.isNotEmpty()) {
        createSVGADynamicImageBitmap(options) { it ->
            it.forEach {
                it.second?.apply { dynamicItem?.setDynamicImage(this, it.first) }
            }
            callback.invoke(dynamicItem)
        }
    } else if (options.svgaImage.isNotEmpty()) {
        options.svgaImage.forEach {
            dynamicItem?.setDynamicImage(it.url, it.key)
        }
        callback.invoke(dynamicItem)
    } else {
        callback.invoke(dynamicItem)
    }
}

private fun Context.createSVGADynamicImageBitmap(
    options: ImageOptions,
    callback: (MutableList<Pair<String, Bitmap?>>) -> Unit
) {
    val list = mutableListOf<Pair<String, Bitmap?>>()
    options.svgaImageBitmap.map {
        MainScope().async {
            loadImageForBitmap(it)
        }
    }.asFlow().map { it.await() }.flowOn(Dispatchers.IO)
        .onEach {
            list.add(it)
        }
        .onCompletion {
            callback.invoke(list)
        }
        .launchIn(MainScope())
}

private suspend fun Context.loadImageForBitmap(svgaImage: SvgaImageBitmap): Pair<String, Bitmap?> =
    suspendCoroutine { coroutine ->
        if (!activityIsAlive()) {
            coroutine.resume(Pair(svgaImage.key, null))
            return@suspendCoroutine
        }
        Glide.with(this).asBitmap().apply {
            if (svgaImage.height != 0 && svgaImage.width != 0) {
                override(svgaImage.height, svgaImage.width)
            }
            if (svgaImage.roundAngle != 0f) {
                transform(RoundedCorners(svgaImage.roundAngle.toInt()))
            }
        }
            .load(svgaImage.url)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    coroutine.resume(Pair(svgaImage.key, resource))
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    coroutine.resume(Pair(svgaImage.key, null))
                }
            })
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