package com.box.app.news.data.source.memory

import com.box.app.news.bean.AppConfig
import com.box.app.news.bean.AppLogConfig
import com.box.app.news.bean.Channel
import com.box.app.news.bean.Weather
import com.box.app.news.bean.list.ListChannel
import com.box.app.news.bean.list.ListRegion
import com.box.app.news.data.DataManager
import java.util.concurrent.ConcurrentHashMap

open class MemorySource {

    private val cacheMap = ConcurrentHashMap<String, Any>()

    fun getRegionList(): ListRegion {
        @Suppress("UNCHECKED_CAST")
        return cacheMap[MemoryKeys.WEATHER_REGION_LIST] as? ListRegion ?: ListRegion()
    }

    fun putRegionList(listRegion: ListRegion) {
        cacheMap[MemoryKeys.WEATHER_REGION_LIST] = listRegion
    }

    fun getWeatherSimpleInfo(): Weather? {
        return cacheMap[MemoryKeys.WEATHER_SIMPLE_INFO] as? Weather
    }

    fun putWeatherSimpleInfo(weather: Weather) {
        cacheMap[MemoryKeys.WEATHER_SIMPLE_INFO] = weather
    }

    fun getChannelList(): ListChannel {
        @Suppress("UNCHECKED_CAST")
        val origin =  cacheMap[MemoryKeys.NEWS_CHANNEL_LIST] as? ListChannel ?: ListChannel()
        val temp = origin.copy()
        temp.hasMore = origin.hasMore
        return temp
    }

    fun putChannelList(listChannel: ListChannel) {
        val temp = listChannel.copy()
        temp.hasMore = listChannel.hasMore
        cacheMap[MemoryKeys.NEWS_CHANNEL_LIST] = temp
    }

    fun putRecommendChannelList(recommendChannel:ListChannel){
        cacheMap[MemoryKeys.RECOMMEND_CHANNEL_LIST] = recommendChannel
    }

    fun getRecommendChannelList():ListChannel{
        @Suppress("UNCHECKED_CAST")
        return cacheMap[MemoryKeys.RECOMMEND_CHANNEL_LIST] as? ListChannel ?: ListChannel()
    }

    fun getCurrentClickedChannel(): Channel? {
        return cacheMap[MemoryKeys.CURRENT_CHANNEL] as? Channel
    }

    fun putCurrentClickedChannel(channel: Channel) {
        cacheMap[MemoryKeys.CURRENT_CHANNEL] = channel
    }

    fun getAppConfig(): AppConfig {
        return cacheMap[MemoryKeys.APP_CONFIG] as? AppConfig ?: DataManager.Local.getAppConfig() ?: AppConfig()
    }

    fun putAppConfig(config: AppConfig) {
        cacheMap[MemoryKeys.APP_CONFIG] = config
    }

    fun getAppLogConfig(): AppLogConfig? {
        return cacheMap[MemoryKeys.APP_LOG_CONFIG] as? AppLogConfig
    }

    fun putAppLogConfig(config: AppLogConfig) {
        cacheMap[MemoryKeys.APP_LOG_CONFIG] = config
    }

    //下面的四个方法是为了不与之前的混淆，用来更新频道列表用的
    fun getCurrentArticleChannel(): Channel? {
        return cacheMap[MemoryKeys.CURRENT_ARTICLE_CHANNEL] as? Channel
    }

    fun putCurrentArticleChannel(channel: Channel) {
        cacheMap[MemoryKeys.CURRENT_ARTICLE_CHANNEL] = channel
    }

    fun getCurrentVideoChannel(): Channel? {
        return cacheMap[MemoryKeys.CURRENT_VIDEO_CHANNEL] as? Channel
    }

    fun putCurrentVideoChannel(channel: Channel) {
        cacheMap[MemoryKeys.CURRENT_VIDEO_CHANNEL] = channel
    }
}