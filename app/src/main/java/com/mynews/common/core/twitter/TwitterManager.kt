package com.mynews.common.core.twitter

import android.util.Log
import com.mynews.common.core.CoreApp
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig

object TwitterManager {

    fun init(debug: Boolean) {
        val config = TwitterConfig.Builder(CoreApp.getInstance())
                .logger(DefaultLogger(Log.WARN))
                .debug(debug)
                .build()
        Twitter.initialize(config)
    }

}