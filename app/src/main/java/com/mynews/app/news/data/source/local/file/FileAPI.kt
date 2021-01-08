package com.mynews.app.news.data.source.local.file

import com.mynews.app.news.bean.PushArrival
import com.mynews.app.news.bean.list.ListChannel
import com.mynews.app.news.bean.list.ListRegion
import com.mynews.app.news.data.source.local.LocalKeys
import com.mynews.common.core.util.FileUtils
import java.io.File

object FileAPI {

    fun getChannels(): ListChannel? {
        return FileUtils.getBean(File(LocalKeys.FILE_CHANNEL_LIST), ListChannel::class.java)
    }

    fun saveChannels(listChannel: ListChannel) {
        FileUtils.writeBean(File(LocalKeys.FILE_CHANNEL_LIST), listChannel)
    }

    fun getRegions(): ListRegion? {
        return FileUtils.getBean(File(LocalKeys.FILE_REGION_LIST), ListRegion::class.java)
    }

    fun saveRegions(listRegion: ListRegion) {
        FileUtils.writeBean(File(LocalKeys.FILE_REGION_LIST), listRegion)
    }

    fun getPushArrival(): PushArrival? {
        return FileUtils.getBean(File(LocalKeys.FILE_PUSH_ARRIVAL), PushArrival::class.java)
    }

    fun savePushArrival(arrival: PushArrival) {
        FileUtils.writeBean(File(LocalKeys.FILE_PUSH_ARRIVAL), arrival)
    }

    fun getArticleContentCache(): String {
        return FileUtils.getText(File(LocalKeys.FILE_ARTICLE_CONTENT_CACHE))
    }

    fun saveArticleContentCache(cache: String) {
        FileUtils.writeText(File(LocalKeys.FILE_ARTICLE_CONTENT_CACHE), cache)
    }

    /******
     * JS *
     ******/

    fun getArticleTransCodeCss(): String {
        return FileUtils.getText(File(LocalKeys.ARTICLE_TRANS_CODE_CSS))
    }

    fun saveArticleTransCodeCss(css: String) {
        FileUtils.writeText(File(LocalKeys.ARTICLE_TRANS_CODE_CSS), css)
    }

    fun getArticleTransCodeJs(): String {
        return FileUtils.getText(File(LocalKeys.ARTICLE_TRANS_CODE_JS))
    }

    fun saveArticleTransCodeJs(js: String) {
        FileUtils.writeText(File(LocalKeys.ARTICLE_TRANS_CODE_JS), js)
    }

    fun getArticleTransCodeJSLang(): String {
        return FileUtils.getText(File(LocalKeys.ARTICLE_TRANS_CODE_JS_LANG))
    }

    fun saveArticleTransCodeJsLang(js: String) {
        FileUtils.writeText(File(LocalKeys.ARTICLE_TRANS_CODE_JS_LANG), js)
    }


}