package com.box.app.news.data.adapter.json

import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataDictionary
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


class GsonBaseNewsBeanAdapters : JsonDeserializer<BaseNewsBean> {

    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): BaseNewsBean? {
        try {
            if (json == null) {
                return null
            }

            val itemObj = json.asJsonObject
            val type = itemObj?.get("type")?.asInt

            return when (type) {
                DataDictionary.NewsType.ARTICLE.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Article::class.java)
                DataDictionary.NewsType.VIDEO.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Video::class.java)
                DataDictionary.NewsType.IMAGE.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Image::class.java)
//                DataDictionary.NewsType.WORLDCUPVIDEO.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, WorldcupVideo::class.java)
//                DataDictionary.NewsType.WORLDCUPBANNER.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, WorldcupBanner::class.java)
//                DataDictionary.NewsType.WORLDCUPMATCH.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, WorldcupMatchDetail::class.java)
                DataDictionary.NewsType.HEADLINE.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, ArticleHeadline::class.java)
                DataDictionary.NewsType.MULTIPLEIMAGE.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Image::class.java)
                DataDictionary.NewsType.GIF.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, GIF::class.java)
                DataDictionary.NewsType.ESSAY.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Essay::class.java)
                DataDictionary.NewsType.CARD_TWITTER.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, Image::class.java)
                DataDictionary.NewsType.NATIVE_AD.value -> CoreGsonUtils.getDefaultInstance().fromJson(json, ArticleNativeAd::class.java)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}