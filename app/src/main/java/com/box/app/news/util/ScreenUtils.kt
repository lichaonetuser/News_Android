package com.box.app.news.util

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.PowerManager
import android.view.Display

import com.box.common.core.CoreApp

import android.content.Context.POWER_SERVICE
import android.os.Build
import com.crashlytics.android.Crashlytics
import org.jetbrains.anko.powerManager

object ScreenUtils {

    fun isScreenOn(): Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                val dm = CoreApp.getInstance().getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
                val displays = dm.displays
                for (display in displays) {
                    if (display.state == Display.STATE_ON) {
                        return true
                    }
                }
                return false
            } else {
                return CoreApp.getInstance().powerManager.isScreenOn
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            return true
        }
    }
}
