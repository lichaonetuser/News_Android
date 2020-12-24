package com.box.common.core.environment

import android.os.Build
import com.box.common.core.CoreApp
import org.jetbrains.anko.dip

object EnvUI {

    val STATUS_BAR_HEIGHT by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            0
        } else {
            var height = getStatusBarHeightFromRes()
            if (height < 1) {
                height = getStatusBarHeightFromReflect()
            }
            if (height < 1) CoreApp.getInstance().dip(25) else height
        }
    }

    private fun getStatusBarHeightFromRes(): Int {
        return try {
            val resources = CoreApp.getInstance().resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0)
                resources.getDimensionPixelSize(resourceId) //根据资源ID获取响应的尺寸值
            else 0
        } catch (e: Exception) {
            -1
        }
    }

    private fun getStatusBarHeightFromReflect(): Int {
        return try {
            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(`object`).toString())
            return CoreApp.getInstance().resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            -1
        }
    }
}