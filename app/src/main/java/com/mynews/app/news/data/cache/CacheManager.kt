package com.mynews.app.news.data.cache

import com.mynews.app.news.data.cache.article.ArticleContentCache
import com.mynews.app.news.data.source.local.file.FileAPI
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.log.Logger
import com.google.gson.reflect.TypeToken

open class CacheManager {

    companion object {
        private const val LOGGER_TAG = "CACHE_MANAGER"
    }

    private val mArticleContentCache = ArticleContentCache(3 * 1024 * 1024)

    fun putArticleContent(aid: String, content: String) {
        if (aid.isBlank() || content.isBlank()) {
            return
        }
        mArticleContentCache.put(aid, content)
    }

    fun getArticleContent(aid: String): String? {
        return mArticleContentCache.get(aid)
    }

    fun transferArticleContentCacheToDisk() {
        val startTime = System.currentTimeMillis()
        val snapshot = mArticleContentCache.snapshot()
        FileAPI.saveArticleContentCache(CoreGsonUtils.toJson(snapshot))
        val finishTime = System.currentTimeMillis()
        Logger.tag(LOGGER_TAG).d("transferArticleContentCacheToDisk in ${finishTime - startTime}ms" +
                " size = ${mArticleContentCache.size()} count = ${snapshot.keys.size}")
    }

    fun transferArticleContentDiskToCache() {
        val startTime = System.currentTimeMillis()
        val snapshotString = FileAPI.getArticleContentCache()
        val snapshot = CoreGsonUtils.fromJson<LinkedHashMap<String, String?>>(snapshotString, object : TypeToken<LinkedHashMap<String, String?>>() {}.type)
                ?: return
        snapshot.forEach {
            val key = it.key
            val value = it.value
            if (value != null && mArticleContentCache.get(key) == null) {
                putArticleContent(key, value)
            }
        }
        val finishTime = System.currentTimeMillis()
        Logger.tag(LOGGER_TAG).d("transferArticleContentDiskToCache in ${finishTime - startTime}ms" +
                " size = ${mArticleContentCache.size()} count = ${snapshot.keys.size}")
    }
}