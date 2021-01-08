package com.mynews.common.core.js

import android.webkit.JavascriptInterface
import com.mynews.common.core.log.Logger
import org.json.JSONObject

abstract class JSBaseAndroidObject {

    @JavascriptInterface
    open fun postMessage(messageJson: String) {
        Logger.d(messageJson)
        try {
            val jsonObject = JSONObject(messageJson)
            val action = jsonObject.optString("action")
            val callbackID = jsonObject.optString("callback")
            val parameters = jsonObject.optJSONObject("parameters")
            var actionType = ""
            if (parameters != null) {
                actionType = parameters.optString("action_type")
                parameters.remove("action_type")
            }
            onCallbackAction(callbackID, action, actionType, parameters)
        } catch (e: Exception) {
            onError(e)
        }
    }

    abstract fun onCallbackAction(callbackId: String, action: String, actionType: String, parameters: JSONObject?)

    abstract fun onError(e: Exception)


}