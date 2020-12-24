package com.box.common.core.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager

object StatusBarUtils {

    fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    fun notifyStatusBarIsLight(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    fun notifyStatusBarIsDark(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    fun showStatusBat(activity: Activity) {
        val systemUiVisibility = activity.window.decorView.systemUiVisibility
        activity.window.decorView.systemUiVisibility = systemUiVisibility and
                View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    fun hideStatusBar(activity: Activity) {
        val systemUiVisibility = activity.window.decorView.systemUiVisibility
        activity.window.decorView.systemUiVisibility = systemUiVisibility or
                View.SYSTEM_UI_FLAG_FULLSCREEN
    }

}
