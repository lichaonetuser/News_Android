package com.mynews.app.news.data.adapter.json

import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.GIF
import com.mynews.app.news.bean.interfaces.IInboxMessageContentBean
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataDictionary.InboxMessageType.*
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.log.Logger
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type


class GsonInboxMessageBeanAdapters : JsonDeserializer<InboxMessage> {

    override fun deserialize(json: JsonElement?, typeOfT: Type, context: JsonDeserializationContext): InboxMessage? {
        try {
            if (json == null) {
                return null
            }

            val inboxMessage = CoreGsonUtils.getDefaultInstance().fromJson(json, InboxMessage::class.java)

            val jsonObject = json.asJsonObject
            val typeInt = jsonObject?.get("type")?.asInt ?: -1
            val type = DataDictionary.InboxMessageType.intValueOf(typeInt)
            val itemJsonObject: JsonElement? = jsonObject.get("item")?.asJsonObject

            val item: IInboxMessageContentBean? = when (type) {
                ARTICLE -> CoreGsonUtils.getDefaultInstance().fromJson(itemJsonObject, Article::class.java)
                YOUTUBE_VIDEO, TWITTER_VIDEO -> CoreGsonUtils.getDefaultInstance().fromJson(itemJsonObject, Video::class.java)
                IMAGE -> CoreGsonUtils.getDefaultInstance().fromJson(itemJsonObject, Image::class.java)
                DataDictionary.InboxMessageType.GIF -> CoreGsonUtils.getDefaultInstance().fromJson(json, GIF::class.java)
                COMMENT -> CoreGsonUtils.getDefaultInstance().fromJson(itemJsonObject, Comment::class.java)
                else -> null
            }
            inboxMessage.item = item

            val list = arrayListOf<IInboxMessageContentBean?>()
            val itemJsonArray: JsonArray? = jsonObject.get("items")?.asJsonArray
            itemJsonArray?.forEach {
                val obj = it.asJsonObject
                val objTypeInt = obj?.get("type")?.asInt ?: -1
                val objType = DataDictionary.InboxMessageType.intValueOf(objTypeInt)

                val objItem: IInboxMessageContentBean? = when (objType) {
                    ARTICLE -> CoreGsonUtils.getDefaultInstance().fromJson(obj, Article::class.java)
                    YOUTUBE_VIDEO, TWITTER_VIDEO -> CoreGsonUtils.getDefaultInstance().fromJson(obj, Video::class.java)
                    IMAGE -> CoreGsonUtils.getDefaultInstance().fromJson(obj, Image::class.java)
                    DataDictionary.InboxMessageType.GIF -> CoreGsonUtils.getDefaultInstance().fromJson(obj, GIF::class.java)
                    COMMENT -> CoreGsonUtils.getDefaultInstance().fromJson(obj, Comment::class.java)
                    else -> null
                }

                list.add(objItem)
            }
            inboxMessage.items = list

            return inboxMessage
        } catch (e: Exception) {
            Logger.e(e)
            return null
        }
    }

}