package com.mynews.app.news.util

import com.mynews.app.news.R
import com.mynews.common.core.util.ResUtils

object WeekUtils {

    fun getDayOfWeekName(dayOfWeek: Int): String {
        val array = ResUtils.getStringArray(R.array.week_array)
        return array[dayOfWeek]
    }

}

