package com.mynews.app.news.page.mvp.layer.main

import com.mynews.app.news.analytics.AnalyticsKey

enum class MainTabEnum(private val position: Int, private val analyticsEventKey: String) : IMainTab {

    //已经移除，旧position为2
    //WEATHER(-1, AnalyticsKey.Event.WEATHER),
    NEWS(0, AnalyticsKey.Event.NEWS),
    VIDEO(1, AnalyticsKey.Event.VIDEO),
    ME(2, AnalyticsKey.Event.SETTING);

    companion object {
        fun positionOf(position: Int): MainTabEnum {
            return when (position) {
                //MainTabEnum.WEATHER.position -> MainTabEnum.WEATHER
                MainTabEnum.NEWS.position -> MainTabEnum.NEWS
                MainTabEnum.VIDEO.position -> MainTabEnum.VIDEO
                MainTabEnum.ME.position -> MainTabEnum.ME
                else -> MainTabEnum.NEWS
            }
        }
    }

    override fun getPosition(): Int {
        return position
    }

    override fun getAnalyticsEventKey(): String {
        return analyticsEventKey
    }
}