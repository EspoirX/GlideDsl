package com.espoir.glidedsl

import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.espoir.glidedsldemo.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //用法示例

//        val imageView = findViewById<ImageView>(R.id.imageView)
//        val imageView1 = findViewById<ImageView>(R.id.imageView1)
//        val svgaImageView = findViewById<SVGAImageView>(R.id.svgaImageView)
//        val url = "https://img-baofun.zhhainiao.com/pcwallpaper_ugc_mobile/static/fa93efe53df4f8c2e813fc85695aa203.jpg"
//        val gif = "https://media0.giphy.com/media/kEKcOWl8RMLde/giphy.gif"
//        val svga = "file:///android_asset/call_phone.svga"
//        val svga1 = "file:///android_asset/matchCard.svga"
//        val webp = "file:///android_asset/webp_no_energy.webp"
//        imageView.loadImage(url)

//        loadImage(svga1) {
//            asSvga()
//            addSvgaImageBitmap {
//                key = "img_107"
//                width = 94.dp2px
//                height = 142.dp2px
//                roundAngle = 20f.dp2px
//                url = "https://www.surenxianqu.com/wp-content/uploads/2023/07/2023071114474362.png"
//            }
//            addSvgaImageBitmap {
//                key = "img_110"
//                width = 94.dp2px
//                height = 142.dp2px
//                roundAngle = 20f.dp2px
//                url = "https://p6.itc.cn/images01/20230530/7368eb04fa9b49dca32fe8c3f123fc09.jpeg"
//            }
//            addSvgaImageBitmap {
//                key = "img_112"
//                width = 94.dp2px
//                height = 142.dp2px
//                roundAngle = 20f.dp2px
//                url = "https://p9.itc.cn/images01/20230619/cce4b4b9815a4d278050d479d6b0191c.jpeg"
//            }
//            onSvgaSuccess { entity, width, height, drawable ->
//                svgaImageView.setImageDrawable(drawable)
//                svgaImageView.startAnimation()
//            }
//        }


//        loadImage(url) {
//            asBitmap()
//            targetSize(150, 200)
//            roundAngle(150f)
//            onBitmapSuccess {
//                imageView1.setImageBitmap(it)
//            }
//        }

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

val Int.dp2px: Int
    get() = this.toFloat().dp2px.toInt()

val Float.dp2px: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        DisplayUtil.getDisplayMetrics()
    )

object DisplayUtil {

    private var displayMetrics: DisplayMetrics? = null

    @JvmStatic
    fun getDisplayMetrics(): DisplayMetrics {
        if (displayMetrics == null) {
            displayMetrics = Resources.getSystem().displayMetrics
        }
        return displayMetrics!!
    }
}