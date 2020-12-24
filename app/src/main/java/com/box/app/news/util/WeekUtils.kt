package com.box.app.news.util

import com.box.app.news.R
import com.box.common.core.util.ResUtils

object WeekUtils {

    fun getDayOfWeekName(dayOfWeek: Int): String {
        val array = ResUtils.getStringArray(R.array.week_array)
        return array[dayOfWeek]
    }

}

