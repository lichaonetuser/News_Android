package com.mynews.common.extension.app.mvp.loading.browser

import com.mynews.common.core.browser.agent.AgentWeb
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

interface MVPBrowserContract {

    interface View : MVPLoadingContract.View {

        fun getAgentWeb(): AgentWeb

        fun quickCallJs(jsFunctionName: String)

        fun addJavaObject(objectName: String, any: Any)

        /**
         * 必须通过addJavaObject注入JSBaseAndroidObject的子类后才有效
         */
        fun <T : Any> callJSBridgeCallback(callbackID: String, bean: T)

        fun <T : Any> callJSBridgeAction(name: String, bean: T)

        fun loadURL(url: String)

        fun loadData(data: String, mimeType: String? = null, encoding: String? = null)

        fun loadDataWithBaseURL(baseUrl: String?, data: String?,
                                mimeType: String? = "text/html", encoding: String? = "UTF-8",
                                historyUrl: String? = null)

        fun injectScript(script: String)

        fun reloadURL(showLoading: Boolean = true)
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onWebPageStarted(url: String?)
        fun onWebPageFinished(url: String?)
        fun onWebReceivedError(errorCode: Int, description: CharSequence?, failingUrl: String?) : Boolean
        fun onProgressChanged(newProgress: Int)
    }

}