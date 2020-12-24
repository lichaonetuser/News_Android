package com.box.common.core.js

import com.box.common.core.browser.agent.AgentWeb
import com.box.common.core.json.gson.util.CoreGsonUtils


fun <T : Any> AgentWeb.callJSBridgeCallback(callbackID: String, bean: T) {
    val msgObj = JSBridge.JSCallBackMessage(JSBridge.JSCallBackDetail(callbackID, bean))
    val msgJsonString = CoreGsonUtils.toJson(msgObj)
    val js = "javascript: (function(){" +
            "if(!window.CustomEvent){(function(){function CustomEvent(event,params){params=params||{bubbles:false,cancelable:false,detail:undefined};var evt=document.createEvent(\"CustomEvent\");evt.initCustomEvent(event,params.bubbles,params.cancelable,params.detail);return evt}CustomEvent.prototype=window.Event.prototype;window.CustomEvent=CustomEvent})()};" +
            "var event = new CustomEvent('${JSBridge.EVENT_CALLBACK}',$msgJsonString);" +
            "document.dispatchEvent(event);" +
            "}());"
    jsEntraceAccess.callJs(js)
}

fun <T : Any> AgentWeb.callJSBridgeAction(name: String, bean: T) {
    val msgObj = JSBridge.JSPostMessage(JSBridge.JSPostDetail(name, bean))
    val msgJsonString = CoreGsonUtils.toJson(msgObj)
    val js = "javascript: (function(){" +
            "if(!window.CustomEvent){(function(){function CustomEvent(event,params){params=params||{bubbles:false,cancelable:false,detail:undefined};var evt=document.createEvent(\"CustomEvent\");evt.initCustomEvent(event,params.bubbles,params.cancelable,params.detail);return evt}CustomEvent.prototype=window.Event.prototype;window.CustomEvent=CustomEvent})()};" +
            "var event = new CustomEvent('${JSBridge.EVENT_POST}',$msgJsonString);" +
            "document.dispatchEvent(event);" +
            "}());"
    jsEntraceAccess.callJs(js)
}