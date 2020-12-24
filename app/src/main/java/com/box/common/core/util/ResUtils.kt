package com.box.common.core.util

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import com.box.common.core.CoreApp

object ResUtils {

    fun getString(resId: Int): String {
        return CoreApp.getInstance().getString(resId)
    }

    fun getString(resId: Int, vararg args: Any): String {
        return CoreApp.getInstance().getString(resId, *args)
    }

    fun getStringArray(resId: Int): Array<out String> {
        return CoreApp.getInstance().resources.getStringArray(resId)
    }

    fun getBoolean(id: Int): Boolean {
        return CoreApp.getInstance().resources.getBoolean(id)
    }

    @Suppress("DEPRECATION")
    fun getColor(color: Int): Int {
        return CoreApp.getInstance().resources.getColor(color)
    }

    fun getInteger(id: Int): Int {
        return CoreApp.getInstance().resources.getInteger(id)
    }

    fun getDimension(id: Int): Float {
        return CoreApp.getInstance().resources.getDimension(id)
    }

    @Suppress("DEPRECATION")
    fun getDrawable(id: Int): Drawable {
        return CoreApp.getInstance().resources.getDrawable(id)
    }

    @Suppress("DEPRECATION")
    fun getCompoundDrawable(id: Int): Drawable? {
        val drawable = CoreApp.getInstance().resources.getDrawable(id) ?: return null
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        return drawable
    }

}
