package com.espoir.glidedslib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.espoir.glidedslib.transform.PhotoMaskTransformation
import com.espoir.glidedslib.transform.blur.BlurTransformation
import com.espoir.glidedslib.transform.gray.GrayPicTransform
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import com.opensource.svgaplayer.glideplugin.asSVGA
import java.net.URL

/**
 * ImageLoader kotlin版，根 ImageLoader 一样，kt文件逐渐去掉ImageLoader，使用这个
 *
 *  用法
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
 *
 */

object GlideImageLoader {

    const val TAG = "GlideImageLoader"

    @SuppressLint("CheckResult")
    @JvmStatic
    fun loadImage(options: ImageOptions?) {
        if (options == null) return
        if (options.res == null) return
        val context = getContext(options)
        if (!context.activityIsAlive()) {
            Log.i(TAG, "activityIsAlive false :$context")
            return
        }
        if (context == null) return

        if (options.loadSvga && !options.loadSvgaByGlide && options.res is String) {
            val parser = SVGAParser(context)
            parser.decodeFromURL(URL(options.res as String), object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    videoItem.handlerSvgaEntity(options)
                }

                override fun onError() {
                    options.listener?.failAction?.invoke(null)
                }
            })
            return
        }
        Glide.with(context).apply {
            when {
                options.loadBitmap -> {
                    this.asBitmap().load(handlerImageRes(options)).apply(buildRequestOptions(options))
                        .addListener(GlideRequestListener()).apply {
                            if (options.imageView != null && options.listener == null) {
                                into(options.imageView!!)
                            } else {
                                into(ResultSimpleTarget<Bitmap>(options))
                            }
                        }
                }

                options.loadSvga -> {
                    this.asSVGA().load(handlerImageRes(options)).apply(buildRequestOptions(options))
                        .addListener(GlideRequestListener()).into(ResultSimpleTarget<SVGAVideoEntity>(options))
                }

                options.loadGif && options.getGifDrawable -> {
                    this.asGif().load(handlerImageRes(options)).apply(buildRequestOptions(options))
                        .addListener(GlideRequestListener()).apply {
                            if (options.imageView != null && options.listener == null) {
                                into(options.imageView!!)
                            } else {
                                into(ResultSimpleTarget<GifDrawable>(options))
                            }
                        }
                }

                else -> {
                    this.asDrawable().load(handlerImageRes(options)).apply(buildRequestOptions(options))
                        .addListener(GlideRequestListener()).addListener(GlideRequestListener()).apply {
                            if (options.imageView != null && options.listener == null) {
                                into(options.imageView!!)
                            } else {
                                into(ResultSimpleTarget<Drawable>(options))
                            }
                        }
                }
            }
        }
    }

    private fun getContext(options: ImageOptions): Context? {
        var context: Context? = null
        if (options.context != null) {
            context = options.context
        } else if (options.imageView != null) {
            context = options.imageView?.context
        }
        return context
    }

    private fun handlerImageRes(options: ImageOptions): Any? {
        return if (options.res is String) {
            val url = (options.res as String).trim()
            if (url.contains("format,webp") || url.endsWith(".webp")) {
                options.loadWebpGif = true
            }
            url
        } else {
            options.res
        }
    }

    @SuppressLint("CheckResult")
    private fun buildRequestOptions(options: ImageOptions): RequestOptions {
        return RequestOptions().apply {
            val imageView = options.imageView

            if (options.targetHeight > 0 && options.targetWidth > 0 && !options.skipOverride) {
                override(options.targetWidth, options.targetHeight)
            } else if (imageView != null && imageView.width > 0 && imageView.height > 0 && !options.skipOverride) {
                override(imageView.width, imageView.height)
            }

            //配置placeholder
            if (options.placeholder != null) {
                placeholder(options.placeholder)
            } else if (options.placeholderResId != 0) {
                placeholder(options.placeholderResId)
            }

            //配置error
            if (options.errorResId != 0) {
                error(options.errorResId)
            } else if (options.errorDrawable != null) {
                error(options.errorDrawable)
            }

            //配置显示模式
            when {
                options.isCenterCrop -> centerCrop()
                options.isFitCenter -> fitCenter()
                options.isCenterInside -> centerInside()
            }

            //配置动画
            if (options.dontAnimate) {
                dontAnimate()
            }

            //配置缓存
            if (!options.loadSvga) {
                if (options.skipLocalCache) {
                    diskCacheStrategy(DiskCacheStrategy.NONE)
                } else { //缓存所有尺寸
                    diskCacheStrategy(DiskCacheStrategy.ALL)
                }
            }
            if (options.skipMemoryCache) {
                skipMemoryCache(true)
            }

            //加载圆形
            if (options.loadCircle) {
                circleCrop()
            }

            //配置圆角
            if (options.roundAngle > 0f) {
                transform(CenterCrop(), RoundedCorners(options.roundAngle.toInt()))
            }

            //配置高斯模糊
            if (options.loadBlurImage) {
                if (options.blurSampling > 0 && options.blurRadius > 0) {
                    transform(
                        CenterCrop(), BlurTransformation(options.blurRadius)
                    )
                } else {
                    transform(
                        CenterCrop(), BlurTransformation()
                    )
                }
            }

            //彩色置灰
            if (options.loadGrayImage) {
                transform(
                    CenterCrop(), GrayPicTransform()
                )
            }

            if (options.maskColor > 0) {
                transform(PhotoMaskTransformation(options.maskColor))
            }

            //加载Webp动图
            if (options.loadWebpGif) {
                var wrapped: Transformation<Bitmap> = CenterInside()
                if (options.roundAngle > 0f) {
                    wrapped = RoundedCorners(options.roundAngle.toInt())
                } else if (options.isCenterCrop) {
                    wrapped = CenterCrop()
                } else if (options.isFitCenter) {
                    wrapped = FitCenter()
                } else if (options.isCenterInside) {
                    wrapped = CenterInside()
                } else if (options.loadCircle) {
                    wrapped = CircleCrop()
                }
                optionalTransform(WebpDrawable::class.java, WebpDrawableTransformation(wrapped))
            }

            if (options.format != null) {
                options.format?.let { format(it) }
            } else if (options.loadGif) { //gif图需要用PREFER_ARGB_8888解码
                format(DecodeFormat.PREFER_ARGB_8888)
            } else if (GlideMemoryState.isLowMemory() && Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                //根据内存情况选择解码方式
                format(DecodeFormat.PREFER_RGB_565)
            }
        }
    }


    class GlideRequestListener<T> : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean
        ): Boolean {
            try {
                val causes = e?.rootCauses ?: return false
                var i = 0
                val size = causes.size ?: 0
                while (i < size) {
                    i++
                }
            } catch (e1: java.lang.Exception) {
                e1.printStackTrace()
            }
            return false
        }

        override fun onResourceReady(
            resource: T, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean
        ): Boolean {
            return false
        }
    }

    class ResultSimpleTarget<T>(private val options: ImageOptions) : SimpleTarget<T>() {
        override fun onResourceReady(resource: T, transition: Transition<in T>?) {
            when (resource) {
                is WebpDrawable -> {
                    options.imageView?.let {
                        it.setImageDrawable(resource)
                        if (resource is Animatable) {
                            (resource as Animatable).start()
                        }
                    }
                    options.listener?.drawableAction?.invoke(resource)
                }

                is Bitmap -> options.listener?.bitmapAction?.invoke(resource)
                is Drawable -> {
                    options.imageView?.let {
                        it.setImageDrawable(resource)
                        if (resource is Animatable) {
                            (resource as Animatable).start()
                        }
                    }
                    options.listener?.drawableAction?.invoke(resource)
                }

                is GifDrawable -> {
                    options.imageView?.let {
                        it.setImageDrawable(resource)
                    }
                    options.listener?.drawableAction?.invoke(resource)
                }

                is SVGAVideoEntity -> {
                    resource.handlerSvgaEntity(options)
                }
            }
        }

        override fun onStart() {
            super.onStart()
            options.listener?.startAction?.invoke()
        }

        override fun onStop() {
            super.onStop()
            options.listener?.stopAction?.invoke()
        }

        override fun onLoadFailed(errorDrawable: Drawable?) {
            super.onLoadFailed(errorDrawable)
            options.listener?.failAction?.invoke(errorDrawable)
        }

        override fun onLoadCleared(placeholder: Drawable?) {
            options.listener?.clearAction?.invoke()
        }
    }

    private fun SVGAVideoEntity.handlerSvgaEntity(options: ImageOptions) {
        val height = this.videoSize.height.toInt()
        val width = this.videoSize.width.toInt()
        val drawable = if (options.dynamicItem != null) {
            SVGADrawable(this, options.dynamicItem!!)
        } else {
            SVGADrawable(this)
        }
        options.svgaImage.clear()
        options.svgaText.clear()
        options.dynamicItem = null
        options.listener?.svgaAction?.invoke(this, width, height, drawable)
        if (options.listener?.svgaAction == null && options.imageView != null && options.imageView is SVGAImageView) {
            (options.imageView as SVGAImageView).setVideoItem(this)
            (options.imageView as SVGAImageView).startAnimation()
        }
    }

    @JvmStatic
    fun clearMemory(context: Context?) {
        context?.let { Glide.get(it).clearMemory() }
    }

    @JvmStatic
    fun clearDiskCache(context: Context?) {
        context?.let { Glide.get(it).clearDiskCache() }
    }

    /*** 取消图片加载*/
    @JvmStatic
    fun clearImage(context: Context?, imageView: ImageView?) {
        if (context == null) return
        imageView?.let { Glide.get(context).requestManagerRetriever[context].clear(it) }
    }

    /*** 预加载*/
    @JvmStatic
    fun preloadImage(context: Context?, url: String?) {
        context?.let { Glide.with(it).load(url).preload() }
    }

    /*** 重新加载*/
    @JvmStatic
    fun resumeRequests(context: Context?) {
        context?.let { Glide.with(it).resumeRequests() }
    }

    /*** 暂停加载*/
    @JvmStatic
    fun pauseRequests(context: Context?) {
        context?.let { Glide.with(it).pauseRequests() }
    }
}

