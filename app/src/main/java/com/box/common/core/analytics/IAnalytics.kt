package com.box.common.core.analytics

interface IAnalytics {

    fun logEvent(event: String, vararg parameters: String)

    fun logEvent(event: String, parameter: String, value: String)

    fun logEvent(event: String, vararg pairs: Pair<String, String>)

}