package com.espoir.glidedslib

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import androidx.annotation.Keep
import kotlinx.coroutines.CoroutineScope
import java.io.Serializable

@Keep
open class SvgaText : Serializable {
    var colorA: Int = 255
    var colorR: Int = 255
    var colorG: Int = 255
    var colorB: Int = 255
    var colorString: String? = null
    var textSize: Float = 20f
    var textAlign: Paint.Align = Paint.Align.LEFT
    var text: String = ""
    var key: String = ""
    var typeface: Typeface? = null

    fun build(): TextPaint {
        return runCatching {
            val textPaint = TextPaint()
            if (colorString.isNullOrEmpty()) {
                textPaint.setARGB(colorA, colorR, colorG, colorB)
            } else {
                textPaint.color = Color.parseColor(colorString)
            }
            if (typeface != null) {
                textPaint.typeface = typeface
            }
            textPaint.textSize = textSize
            textPaint.textAlign = textAlign
            return@runCatching textPaint
        }.getOrElse { TextPaint() }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SvgaText

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}

@Keep
open class SvgaImage : Serializable {
    var url: String = ""
    var key: String = ""
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SvgaImage

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}

@Keep
open class SvgaImageBitmap : Serializable {
    var url: String = ""
    var key: String = ""
    var width: Int = 0
    var height: Int = 0
    var roundAngle: Float = 0f

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SvgaImageBitmap

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}