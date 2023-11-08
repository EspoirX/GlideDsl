# GlideDsl

glide 的封装

[![](https://jitpack.io/v/EspoirX/GlideDsl.svg)](https://jitpack.io/#EspoirX/GlideDsl)

## 使用

```gradle
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}

dependencies {
      implementation 'com.github.EspoirX:GlideDsl:vX.X.X'
}
``` 

### 简单使用：

先自己依赖 glide：

```groovy
implementation "com.github.bumptech.glide:glide:$glide_version"
```

在 Application 里面给一下上下文：
```kotlin
 GlideDsl.sAppContext = this
```

然后可以使用了（简单的用法示例代码见 MainActivity）：

```kotlin
  imageView.loadImage(url)
```

如果加载的 resource 属于 Animatable，会自动调用 start()

### 配置加载参数：

```kotlin
  ImageView.loadImage(url){
    placeholder()      //占位，提供了 resId 和 drawable 两个方法
    error()            //错误占位，提供了 resId 和 drawable 两个方法
    decodeFormat()     //对应 Glide 的 format 配置
    centerCrop()       //显示模式，对应 centerCrop
    centerInside()     //显示模式，对应 centerInside
    fitCenter()        //显示模式，对应 fitCenter
    skipLocalCache()   //跳过缓存，下面几个都是
    skipNetCache()
    skipMemoryCache()
    skipOverride()     // 设置这个，就不会执行 override，即使设置了宽高
    circleCrop()       //圆形
    roundAngle()       //圆角
    maskColor()        //设置遮照颜色
    dontAnimate()      //不要动画
    blur()             //高斯模糊
    targetSize()       //设置宽高
    grayImage()        //彩色置灰
  }
```

如上，可以设置很多种基础配置。

### 加载回调

1. 回调 drawable

```kotlin
imageView.loadImage(url){
    onDrawableSuccess { drawable ->
       //...
    }
    onLoadFailed { 
        //...
    }
}

也可以这样：

context.loadImage(url){
    onDrawableSuccess { drawable ->
        imageView.setImageDrawable(drawable)
    }
}
```

2. 回调 bitmap，同理，不过需要调用 asBitmap

```kotlin
imageView.loadImage(url){
    asBitmap()
    onBitmapSuccess { drawable ->
       //...
    }
}
```

### 加载 gif

```kotlin
  imageView.loadImage(url){ asGif() }
```

asGif() 有个参数 getGifDrawable，默认 false，true 的话会在 onDrawableSuccess 里面得到 GifDrawable

### 加载 webp 动画

```kotlin
  imageView.loadImage(url){ webpGif() }
```

webp动画需要自己依赖解码库：

```groovy
  implementation "com.zlc.glide:webpdecoder:2.0.4.12.0"
```

### 加载 svga

加载svga需要自己依赖svga库：

```groovy
    implementation("com.github.yyued:SVGAPlayer-Android:2.6.1")
```

然后加载 svga 可选择用 Glide 去加载或者用 svga 自己的加载器加载，推荐用 glide 加载，需要自己依赖：

```groovy
 // 手Y SVGA管理
   implementation('com.github.YvesCheung:SVGAGlidePlugin:4.13.3') {
        exclude group: 'com.github.yyued', module: 'SVGAPlayer-Android'
   }
```

然后使用：

```kotlin
  imageView.loadImage(url){ asSvga() }
```

默认使用的是 glide 加载，不想的话 asSvga(false) 即可。

回调的话也是一样：

```kotlin
context.loadImage(url) {
    asSvga() 
    onSvgaSuccess { entity, width, height, drawable ->
       //...
    }
}
```

给 svga 添加 key：

```kotlin
context.loadImage(svgaTitle) {
    asSvga()
    addSvgaText {
        key = ""     //key
        text = ""    //文案 
        colorString = "#FFFFFF"  //颜色
        textSize = 20f.sp2px  //字体大小
        typeface = FontCache.getTypeface("XXX.ttf", context) //字体
    }
    addSvgaText {} //可添加多个
    //...
    addSvgaImage{
        key = ""     //key
        url=""     //图片url
    }
    addSvgaImage {} //可添加多个
    //...
    onSvgaSuccess { entity, width, height, drawable ->
        svgaView?.setImageDrawable(drawable)
        svgaView?.startAnimation()
    }
}
```

svga 添加图片 key 时支持用 Bitmap
```kotlin
  addSvgaImageBitmap {
    key = "img_107"
    width = 94.dp2px
    height = 142.dp2px
    roundAngle = 20f.dp2px
    url = "https://www.surenxianqu.com/wp-content/uploads/2023/07/2023071114474362.png"
 }
 addSvgaImageBitmap {} //可添加多个
 //...
```

会自动根据url获取到 bitmap 并支持对 bitmap 进行宽高和圆角的变化  
该功能是用用协程实现的，所以需要依赖：
```groovy
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
  implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
```
