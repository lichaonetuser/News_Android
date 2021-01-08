package com.mynews.common.core.environment

import com.mynews.common.core.CoreApp

object EnvDisplayMetrics {
    val DISPLAY_METRICS = CoreApp.getInstance().resources.displayMetrics
    val DENSITY_DPI = DISPLAY_METRICS.densityDpi
    val HEIGHT_PIXELS = DISPLAY_METRICS.heightPixels
    val WIDTH_PIXELS = DISPLAY_METRICS.widthPixels
    val RESOLUTION = "{$HEIGHT_PIXELS,$WIDTH_PIXELS}"
}