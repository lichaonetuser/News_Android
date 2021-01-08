package com.mynews.app.news.data.source.local.raw

import com.mynews.app.news.bean.list.ListChannel
import com.mynews.app.news.bean.list.ListRegion
import com.mynews.app.news.data.source.local.LocalKeys
import com.mynews.common.core.util.RawUtils

object RawAPI {

    fun getChannels(): ListChannel? {
        return RawUtils.getBean(LocalKeys.R_DEFAULT_CHANNEL_LIST, ListChannel::class.java)
    }

    fun getRegions(): ListRegion? {
        return RawUtils.getBean(LocalKeys.R_DEFAULT_REGION_LIST, ListRegion::class.java)
    }

    fun getArticleTransCodeCss(): String? {
        return RawUtils.getString(LocalKeys.R_DEFAULT_ARTICLE_TRANS_CODE_CSS)
    }

    fun getArticleTransCodeJS(): String? {
        return RawUtils.getString(LocalKeys.R_DEFAULT_ARTICLE_TRANS_CODE_JS)
    }

    fun getArticleTransCodeJSLang(): String? {
        return RawUtils.getString(LocalKeys.R_DEFAULT_ARTICLE_TRANS_CODE_JS_LANG)
    }

}