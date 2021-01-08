package com.mynews.app.news.util

import android.app.Activity
import android.content.Intent
import android.net.Uri

object IntentUtils {

    fun startBrowserIntent(activity: Activity, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        intent.data = Uri.parse(url)
        activity.startActivity(intent)
    }

}