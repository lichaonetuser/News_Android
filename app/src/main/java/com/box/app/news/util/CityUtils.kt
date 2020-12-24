package com.box.app.news.util

import com.box.app.news.data.DataManager
import com.box.common.core.net.http.HttpManager

object CityUtils {

    const val KEY = "city_code"

    fun getCityCodePair(): Pair<String, String> {
        return KEY to DataManager.Local.getUserLastSelectedRegion().cityCode
    }

    fun updateHttpCityCodeParams(cityCode: String) {
        HttpManager.putCommonParams(KEY to cityCode)
    }

}