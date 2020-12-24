package com.box.common.core.analytics.platform

import android.util.Log
import com.box.common.core.CoreApp
import com.box.common.core.analytics.IAnalytics
import com.appsflyer.AppsFlyerLib

internal object AppsFlyerAnalytics : IAnalytics {

    fun setCustomerUserId(deviceId: String) {
        AppsFlyerLib.getInstance().setCustomerUserId(deviceId)
//        AppsFlyerLib.getInstance().setCustomerIdAndTrack(deviceId, CoreApp.getInstance())
    }

    override fun logEvent(event: String, vararg parameters: String) {
        val map = HashMap<String, Any>()
        parameters.forEach { map[it] = it }
        AppsFlyerLib.getInstance().trackEvent(CoreApp.getInstance(), event, map)
    }

    override fun logEvent(event: String, parameter: String, value: String) {
        val map = HashMap<String, Any>()
        map[parameter] = value
        AppsFlyerLib.getInstance().trackEvent(CoreApp.getInstance(), event, map)
    }

    override fun logEvent(event: String, vararg pairs: Pair<String, String>) {
        val map = HashMap<String, Any>()
        pairs.forEach { map[it.first] = it.second }
        AppsFlyerLib.getInstance().trackEvent(CoreApp.getInstance(), event, map)
    }

}