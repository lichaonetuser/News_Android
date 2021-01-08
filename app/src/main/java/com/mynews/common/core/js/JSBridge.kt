package com.mynews.common.core.js

import com.mynews.app.news.R
import com.mynews.common.core.util.RawUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

object JSBridge {

    val EVENT_CALLBACK = "JSBridgeDidReceiveNativeCallback"
    val EVENT_POST = "JSBridgeDidReceiveNativeBroadcast"

    val script: String by lazy {
        RawUtils.getString(R.raw.core_js_bridge) ?:""
    }

    data class JSCallBackMessage<T>(@Expose @SerializedName("detail") var detail: JSCallBackDetail<T>)

    data class JSCallBackDetail<T>(
            @Expose @SerializedName("id") var id: String,
            @Expose @SerializedName("parameters") var parameters: T)

    data class JSPostMessage<T>(@Expose @SerializedName("detail") var detail: JSPostDetail<T>)

    data class JSPostDetail<T>(
            @Expose @SerializedName("name") var name: String,
            @Expose @SerializedName("parameters") var parameters: T)

}

