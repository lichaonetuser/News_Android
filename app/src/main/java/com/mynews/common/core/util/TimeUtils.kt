package com.mynews.common.core.util

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    fun getGMTOffset(): Int {
        return try {
            TimeZone.getTimeZone("GMT").rawOffset
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
            val currentLocalTime = calendar.time
            val date = SimpleDateFormat("Z", Locale.getDefault())
            date.format(currentLocalTime)
                    .replace("+", "")
                    .replace("-", "")
                    .toInt()
        } catch (e: Exception) {
            0
        }
    }

}