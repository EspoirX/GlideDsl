package com.espoir.glidedsl

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.espoir.glidedsldemo.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //用法示例

        val imageView = findViewById<ImageView>(R.id.imageView)
//        val svgaImageView = findViewById<SVGAImageView>(R.id.svgaImageView)
        val url = "https://img-baofun.zhhainiao.com/pcwallpaper_ugc_mobile/static/fa93efe53df4f8c2e813fc85695aa203.jpg"
        val gif = "https://media0.giphy.com/media/kEKcOWl8RMLde/giphy.gif"
        val svga = "file:///android_asset/call_phone.svga"
        val webp = "file:///android_asset/webp_no_energy.webp"
//        imageView.loadImage(url)
//        imageView.loadImage(gif) {
//            asGif()
//        }
//        svgaImageView.loadImage(svga) {
//            asSvga()
//        }
//        imageView.loadImage(webp) {
//            webpGif()
//        }
//        loadImage(url) {
//            onDrawableSuccess {
//                imageView.setImageDrawable(it)
//            }
//        }

//        loadImage(url) {
//            asBitmap()
//            onBitmapSuccess {
//                imageView.setImageBitmap(it)
//            }
//        }
//        loadImage(svga) {
//            asSvga()
//            onSvgaSuccess { entity, width, height, drawable ->
//                svgaImageView.setVideoItem(entity)
//                svgaImageView.startAnimation()
//            }
//        }
    }
}