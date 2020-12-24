package com.box.app.news.data.source.remote.http.url

import android.os.Build
import com.box.app.news.BuildConfig
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.LocalSource
import com.box.app.news.util.CityUtils
import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.extension.addQueryParameters
import okhttp3.HttpUrl

object WebNewsUrls {


    const val ROOT_PATH = "/webnews"

    private val HOST by lazy {
        if (BuildConfig.DEBUG && BuildConfig.BUILD_TYPE == "debug") {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                "http://test.api.bigappinfo.com"
            } else {
                "http://test.api.bigappinfo.com"
            }
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                "http://www.newsbox-inc.com"
            } else {
                "https://www.newsbox-inc.com"
            }
        }
    }

    private val USER_TERM_URL by lazy { "$HOST$ROOT_PATH/user_term/" }
    private val NEWS_WEATHER by lazy { "$HOST$ROOT_PATH/news_weather/" }
    private val NEWS_SEARCH_RESULT by lazy { "${LocalSource().getBaseUrl()}search/" }

    fun getUserTermUrl(appendCommonParams: Boolean = false): String {
        val originHttpUrl = HttpUrl.parse(USER_TERM_URL) ?: return USER_TERM_URL
        val returnHttpUrl = originHttpUrl.newBuilder()
        if (appendCommonParams) {
            returnHttpUrl.addQueryParameters(HttpManager.getCommonParams())
        }
        returnHttpUrl.build()
        return returnHttpUrl.toString()
    }

    fun getNewsWeatherUrl(cityCode: String = DataManager.Local.getUserLastSelectedRegion().cityCode): String {
        val originHttpUrl = HttpUrl.parse(NEWS_WEATHER) ?: return NEWS_WEATHER
        val returnHttpUrl = originHttpUrl.newBuilder()
                .addQueryParameters(HttpManager.getCommonParams())
                .setQueryParameter(CityUtils.KEY, cityCode)
                .build()
        return returnHttpUrl.toString()
    }

    fun getNewsSearchResultUrl(type: Int = 0, keyword: String): String {
        val originHttpUrl = HttpUrl.parse(NEWS_SEARCH_RESULT) ?: return NEWS_SEARCH_RESULT
        val returnHttpUrl = originHttpUrl.newBuilder()
                .addQueryParameters(HttpManager.getCommonParams())
                .setQueryParameter(DataDictionary.SearchResultQuery.TYPE.value, type.toString())
                .setQueryParameter(DataDictionary.SearchResultQuery.KEYWORD.value, keyword)
                .build()
        return returnHttpUrl.toString()
    }
}