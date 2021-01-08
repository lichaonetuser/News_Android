package com.mynews.app.news.util

import com.mynews.app.news.data.DataManager
import com.mynews.common.core.net.http.HttpManager

/**
 * 6.9需求：网络请求通参里增加字体大小字段
 * 初始化app时增加该字段，字体更新时修改通参里该字段的值
 */
object TextSizeUtils {

    const val KEY = "text_size"

    fun getTextSizePair(): Pair<String, String> {
        return KEY to DataManager.Local.getFontSize().toCommonParams()
    }

    fun updateTextSizeParams(textSize: String) {
        HttpManager.putCommonParams(KEY to textSize)
    }

}