package com.box.app.news.util

import com.box.app.news.R
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.core.util.ResUtils
import com.box.common.core.util.extension.format2DateString
import java.util.*

object TimeUtils {


    fun getVideoDurationText(timeMs: Long): String {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00"
        }
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }

    fun getDisplayTimeString(displayTime: Long, formatPattern: String = "yyyy-MM-dd"): String {
        if (displayTime == 0L) {
            return ""
        }

        val currentTimeMillis = System.currentTimeMillis()
        val pastTimeMillis = currentTimeMillis - displayTime
        return when {
            pastTimeMillis <= BaseNewsItem.ONE_MIN_MILLIS -> {
                ResUtils.getString(R.string.Time_JustNow)
            }
            pastTimeMillis <= BaseNewsItem.ONE_HOUR_MILLIS -> {
                val min = pastTimeMillis / BaseNewsItem.ONE_MIN_MILLIS
                String.format(ResUtils.getString(R.string.Time_NumberMinutesAgo), min)
            }
            pastTimeMillis <= BaseNewsItem.ONE_DAY_MILLIS -> {
                val hour = pastTimeMillis / BaseNewsItem.ONE_HOUR_MILLIS
                String.format(ResUtils.getString(R.string.Time_NumberHoursAgo), hour)
            }
            else -> {
                displayTime.format2DateString(formatPattern)
            }
        }
    }

    fun getHeadlineTimeString(displayTime: Long, oldStrategy : Pair<Int, Int> = Pair(11, 16)): String {
        if (displayTime == 0L) {
            return ""
        }

        val currentTimeMillis = System.currentTimeMillis()
        val pastTimeMillis = currentTimeMillis - displayTime

        val isSameDay = isSameDay(currentTimeMillis, displayTime)

        return when {
            pastTimeMillis <= BaseNewsItem.ONE_MIN_MILLIS && isSameDay.first -> {
                ResUtils.getString(R.string.Time_JustNow)
            }
            pastTimeMillis <= BaseNewsItem.ONE_HOUR_MILLIS && isSameDay.first -> {
                val min = pastTimeMillis / BaseNewsItem.ONE_MIN_MILLIS
                String.format(ResUtils.getString(R.string.Time_NumberMinutesAgo), min)
            }
            pastTimeMillis <= BaseNewsItem.ONE_DAY_MILLIS && isSameDay.first  -> {
                val hour = pastTimeMillis / BaseNewsItem.ONE_HOUR_MILLIS
                String.format(ResUtils.getString(R.string.Time_NumberHoursAgo), hour)
            }
            else -> {
                isSameDay.third.substring(oldStrategy.first, oldStrategy.second)
            }
        }
    }

    fun isSameDay(firstTime : Long, secondTime : Long) : Triple<Boolean, String, String> {
        val firstTimeFormat = firstTime.format2DateString("yyyy-MM-dd HH:mm:ss")
        val secondTimeFormat = secondTime.format2DateString("yyyy-MM-dd HH:mm:ss")
        if (firstTimeFormat.substring(0, 10) == secondTimeFormat.substring(0, 10)) {
            return Triple(true, firstTimeFormat, secondTimeFormat)
        }
        return Triple(false, firstTimeFormat, secondTimeFormat)
    }
}