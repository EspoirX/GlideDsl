package com.espoir.glidedslib

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build

fun Context?.activityIsAlive() = ActivityUtils.activityIsAlive(this)
fun Activity?.activityIsAlive() = ActivityUtils.activityIsAlive(this)
fun Context?.findActivity() = ActivityUtils.findActivity(this)

object ActivityUtils {
    @JvmStatic
    fun activityIsAlive(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        //旧逻辑中，application非activity 直接给了true？ ，引用地方比较多不好一刀切
        //这里在维持旧逻辑下修复You cannot start a load for a destroyed activity
        //增加漏掉的context is ContextWrapper的情况
        val activity = context.findActivity()
        return if (activity is Activity) activity.activityIsAlive() else true
    }

    @JvmStatic
    fun activityIsAlive(activity: Activity?): Boolean {
        if (activity == null) {
            return false
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !(activity.isDestroyed || activity.isFinishing)
        } else {
            !activity.isFinishing
        }
    }

    /**
     * 从context 从获得activity
     */
    @JvmStatic
    fun findActivity(context: Context?): Activity? {
        if (context == null) {
            return null
        }
        return when (context) {
            is Activity -> context
            is ContextWrapper -> findActivity(context.baseContext)
            else -> null
        }
    }
}