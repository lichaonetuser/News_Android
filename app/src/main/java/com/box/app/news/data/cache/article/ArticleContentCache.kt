package com.box.app.news.data.cache.article

import android.util.LruCache

class ArticleContentCache(maxBytes: Int) : LruCache<String, String?>(maxBytes) {

    override fun sizeOf(key: String?, value: String?): Int {
        // https://stackoverflow.com/questions/31206851/how-much-memory-does-a-string-use-in-java-8
        return 8 * ((((value?.length ?: 0) * 2) + 45) / 8)
    }

}