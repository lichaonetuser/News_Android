package com.box.common.core.analytics.platform

import com.box.common.core.CoreApp
import com.box.common.core.analytics.IAnalytics
import com.umeng.analytics.MobclickAgent

internal object UmengAnalytics : IAnalytics {


    override fun logEvent(event: String, vararg parameters: String) {
        val map = HashMap<String, String>()
        parameters.forEach { map[event] = it }
        if (map.isEmpty()) {
            MobclickAgent.onEvent(CoreApp.getInstance(), event)
        } else {
            MobclickAgent.onEvent(CoreApp.getInstance(), event, map)
        }
    }

    override fun logEvent(event: String, parameter: String, value: String) {
        val map = HashMap<String, String>()
        map[parameter] = value
        MobclickAgent.onEvent(CoreApp.getInstance(), event, map)
    }

    override fun logEvent(event: String, vararg pairs: Pair<String, String>) {
        MobclickAgent.onEvent(CoreApp.getInstance(), event, hashMapOf(*pairs))
    }

}