package com.mynews.app.news.util

import java.util.*

object SessionUtils {

    const val KEY = "session"

    var tag: String? = null

    fun getSessionPair(): Pair<String, String> {
        return KEY to UUID.randomUUID().toString()
    }

}