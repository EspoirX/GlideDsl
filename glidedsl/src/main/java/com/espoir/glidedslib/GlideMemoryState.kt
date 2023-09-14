package com.espoir.glidedslib

import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW
import android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE
import com.bumptech.glide.Glide

/**
 * 内存状态，管理，回收
 */
object GlideMemoryState {
    const val TAG = "MemoryState"

    var curLevel = TRIM_MEMORY_RUNNING_CRITICAL //系统触发

    @JvmStatic
    fun onTrimMemory(level: Int) {
        curLevel = level
        Glide.get(GlideDsl.sAppContext).onTrimMemory(level)
    }

    @JvmStatic
    fun isLowMemory(): Boolean {
        return curLevel == TRIM_MEMORY_RUNNING_LOW || curLevel == TRIM_MEMORY_RUNNING_MODERATE
    }
}