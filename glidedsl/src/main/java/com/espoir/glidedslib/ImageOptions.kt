package com.espoir.glidedslib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DecodeFormat
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAVideoEntity

class ImageOptions {

    //加载的资源
    internal var res: Any? = null

    //targetView 展示图片
    internal var imageView: ImageView? = null

    //上下文，没传targetView的时候需要传上下
    internal var context: Context? = null

    //占位图
    internal var placeholderResId = 0

    //出错占位图
    internal var errorResId = 0

    //占位图Drawable
    internal var placeholder: Drawable? = null

    //出错占位图Drawable
    internal var errorDrawable: Drawable? = null

    //是否CenterCrop
    internal var isCenterCrop = false

    //是否CenterInside
    internal var isCenterInside = false

    //是否FitCenter
    internal var isFitCenter = false

    //是否跳过本地缓存
    internal var skipLocalCache = false

    //是否跳过网络缓存
    internal var skipNetCache = false

    //是否跳过内存缓存
    internal var skipMemoryCache = false

    //是否加载gif
    internal var loadGif = false
    internal var getGifDrawable = false

    //bitmap
    internal var loadBitmap = false

    //svga
    internal var loadSvga = false

    //是否用 Glide 来加载svga
    internal var loadSvgaByGlide = true

    //svga key配置
    internal var svgaText = mutableListOf<SvgaText>()
    internal var svgaImage = mutableListOf<SvgaImage>()
    internal var dynamicItem: SVGADynamicEntity? = null

    //是否高斯模糊
    internal var loadBlurImage = false
    internal var blurRadius = 0
    internal var blurSampling = -1

    //是否加载圆图
    internal var loadCircle = false

    //圆角角度
    internal var roundAngle = 0f

    internal var maskColor = 0

    //是否加载动画
    internal var dontAnimate = false

    internal var format: DecodeFormat? = null

    //图片宽
    internal var targetWidth = 0

    //图片高
    internal var targetHeight = 0

    //图片下载保持地址
    internal var downloadSavePath: String? = null

    //保存的图片名字
    internal var imageSaveName: String? = null

    //是否通知相册
    internal var isNotifyAfterDownload = false

    //监听
    internal var listener: OnImageListener? = null

    //彩色置灰
    internal var loadGrayImage: Boolean = false

    //加载Webp动图
    internal var loadWebpGif: Boolean = false

    internal var skipOverride = false

    /*** 资源 */
    fun res(res: Any?) = apply {
        this.res = res
    }

    /*** 控件 */
    fun view(imageView: ImageView?) = apply {
        this.imageView = imageView
    }

    /*** 控件 */
    fun into(imageView: View?) {
        if (imageView is ImageView) {
            this.imageView = imageView
            load()
        }
    }

    /*** 监听器，java用 */
    fun intoDrawable(listener: OnDrawableListener) {
        requestListener {
            onDrawableSuccess {
                listener.onDrawableSuccess(it)
            }
            onLoadFailed {
                listener.onLoadFailed(it)
            }
        }
        load()
    }

    /*** 监听器，java用 */
    fun intoBitmap(listener: OnBitmapListener) {
        asBitmap()
        requestListener {
            onBitmapSuccess {
                listener.onBitmapSuccess(it)
            }
            onLoadFailed {
                listener.onLoadFailed(it)
            }
        }
        load()
    }

    /*** 监听器，java用 */
    fun intoSvga(listener: OnSvgaListener) {
        requestListener {
            onSvgaSuccess { entity, width, height, drawable ->
                listener.onSvgaSuccess(entity, width, height, drawable)
            }
            onLoadFailed {
                listener.onLoadFailed(it)
            }
        }
        load()
    }

    /*** 上下文 */
    fun context(context: Context?) = apply {
        this.context = context
    }

    /*** 占位图 */
    fun placeholder(placeholder: Int) = apply {
        this.placeholderResId = placeholder
    }

    /*** 出错时占位图 */
    fun error(resId: Int) = apply {
        errorResId = resId
    }

    /*** 占位图 */
    fun placeholder(placeholder: Drawable) = apply {
        this.placeholder = placeholder
    }

    /*** 出错时占位图 */
    fun error(drawable: Drawable) = apply {
        errorDrawable = drawable
    }

    /*** DecodeFormat */
    fun decodeFormat(format: DecodeFormat?) = apply {
        this.format = format
    }

    /*** centerCrop 配置 */
    fun centerCrop() = apply {
        isCenterCrop = true
    }

    /*** centerInside 配置 */
    fun centerInside() = apply {
        isCenterInside = true
    }

    /*** fitCenter 配置 */
    fun fitCenter() = apply {
        isFitCenter = true
    }

    /*** 跳过本地缓存 */
    fun skipLocalCache() = apply {
        skipLocalCache = true
    }

    /*** 跳过网络缓存 */
    fun skipNetCache() = apply {
        skipNetCache = true
    }
    /*** 是否跳过裁剪 ,默认false ，如果获取到图片高宽会 override 高宽
     * 通过设置true 跳过裁剪避免一些原图被放大，
     * */
    fun skipOverride(skip: Boolean) = apply {
        skipOverride = skip
    }
    /*** 跳过内存缓存 */
    fun skipMemoryCache() = apply {
        skipMemoryCache = true
    }

    /*** gif 转换 */
    fun asGif(getGifDrawable: Boolean = false) = apply {
        loadGif = true
        this.getGifDrawable = getGifDrawable
    }

    /*** Bitmap 转换 */
    fun asBitmap() = apply {
        loadBitmap = true
    }

    /*** Svga 转换 */
    fun asSvga(useGlide: Boolean = true) = apply {
        loadSvga = true
        loadSvgaByGlide = useGlide
    }

    /*** 添加 Svga image key */
    fun addSvgaImage(imageCreation: SvgaImage.() -> Unit): SvgaImage = SvgaImage()
        .apply(imageCreation)
        .also {
            if (it.key.isNotEmpty() && it.url.isNotEmpty() && !svgaImage.contains(it)) {
                svgaImage.add(it)
            }
        }

    /*** 添加 Svga text key */
    fun addSvgaText(textCreation: SvgaText.() -> Unit): SvgaText = SvgaText()
        .apply(textCreation)
        .also {
            if (it.key.isNotEmpty() && it.text.isNotEmpty() && !svgaText.contains(it)) {
                svgaText.add(it)
            }
        }

    /*** 加载圆形 */
    fun circleCrop() = apply {
        loadCircle = true
    }

    /*** 圆角 */
    fun roundAngle(angle: Float) = apply {
        roundAngle = angle
    }

    /*** 遮照颜色 */
    fun maskColor(maskColor: Int) = apply {
        this.maskColor = maskColor
    }

    /*** 不要动画 */
    fun dontAnimate() = apply {
        dontAnimate = true
    }

    /*** 高斯模糊 */
    fun blur(blurRadius: Int = 0, blurSampling: Int) = apply {
        loadBlurImage = true
        this.blurRadius = blurRadius
        this.blurSampling = blurSampling
    }

    /*** 加载大小 */
    fun targetSize(width: Int, height: Int) = apply {
        targetWidth = width
        targetHeight = height
    }

    /*** 彩色置灰*/
    fun grayImage() = apply {
        loadGrayImage = true
    }

    /*** 加载Webp动图*/
    fun webpGif() = apply {
        loadWebpGif = true
    }

    /*** 监听器 */
    fun requestListener(listener: OnImageListener.() -> Unit) = apply {
        this.listener = OnImageListener().also(listener)
    }

    /*** 下载配置 */
    fun downloadConfig(savePath: String?, saveName: String?, isNotifyAfterDownload: Boolean = false) = apply {
        downloadSavePath = savePath
        imageSaveName = saveName
        this.isNotifyAfterDownload = isNotifyAfterDownload
    }


    /**
     * java 调用
     */
    fun load() {
        GlideImageLoader.loadImage(this)
    }
}

open class OnImageListener {
    internal var failAction: ((Drawable?) -> Unit)? = null
    internal var drawableAction: ((Drawable) -> Unit)? = null
    internal var bitmapAction: ((Bitmap) -> Unit)? = null
    internal var svgaAction: ((
        entity: SVGAVideoEntity, width: Int, height: Int,
        drawable: SVGADrawable
    ) -> Unit)? = null

    internal var startAction: (() -> Unit)? = null
    internal var stopAction: (() -> Unit)? = null
    internal var clearAction: (() -> Unit)? = null

    fun onDrawableSuccess(action: (Drawable) -> Unit) {
        drawableAction = action
    }

    fun onBitmapSuccess(action: (Bitmap) -> Unit) {
        bitmapAction = action
    }

    fun onSvgaSuccess(
        action: (
            entity: SVGAVideoEntity, width: Int, height: Int,
            drawable: SVGADrawable
        ) -> Unit
    ) {
        svgaAction = action
    }

    fun onStart(action: () -> Unit) {
        startAction = action
    }

    fun onStop(action: () -> Unit) {
        stopAction = action
    }

    fun onLoadFailed(action: (Drawable?) -> Unit) {
        failAction = action
    }

    fun onLoadCleared(action: () -> Unit) {
        clearAction = action
    }
}

open abstract class OnDrawableListener {
    abstract fun onDrawableSuccess(drawable: Drawable)
    open fun onLoadFailed(errorDrawable: Drawable?) {
    }
}

open abstract class OnBitmapListener {
    abstract fun onBitmapSuccess(bitmap: Bitmap)
    open fun onLoadFailed(errorDrawable: Drawable?) {
    }
}

open abstract class OnSvgaListener {
    abstract fun onSvgaSuccess(entity: SVGAVideoEntity, width: Int, height: Int, drawable: SVGADrawable)
    open fun onLoadFailed(errorDrawable: Drawable?) {
    }
}