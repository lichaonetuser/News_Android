package com.box.common.extension.app.mvp.loading.browser

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.view.KeyEvent
import android.view.KeyEvent.ACTION_DOWN
import android.view.ViewGroup
import android.webkit.*
import android.widget.RelativeLayout
import com.box.app.news.R
import com.box.common.core.browser.agent.AgentWeb
import com.box.common.core.browser.agent.IWebLayout
import com.box.common.core.js.JSBridge
import com.box.common.core.js.callJSBridgeAction
import com.box.common.core.js.callJSBridgeCallback
import com.box.common.core.log.Logger
import com.box.common.extension.app.mvp.loading.MVPLoadingFragment
import kotlinx.android.synthetic.main.fragment_common_browser_web.*

abstract class MVPBrowserFragment<in V : MVPBrowserContract.View,
        out P : MVPBrowserContract.Presenter<V>>
    : MVPLoadingFragment<V, P>(), MVPBrowserContract.View {

    override val mLayoutRes: Int = R.layout.fragment_common_browser_web
    protected var mLoadError = false

    protected open val mIWebLayout: IWebLayout<WebView, ViewGroup>? = null
    protected open val mAgentWeb by lazy {
        AgentWeb.with(this)
                .setAgentWebParent(web_view_layout, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeDefaultIndicator()
                .setWebLayout(mIWebLayout)
                .setWebViewClient(object : WebViewClient() {

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        mLoadError = false
                        mPresenter.onWebPageStarted(url)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        mPresenter.onWebPageFinished(url)
                    }

                    @TargetApi(Build.VERSION_CODES.M)
                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val errorCode = error?.errorCode ?: 0
                            val description = error?.description
                            val failingUrl = request?.url.toString()
                            Logger.tag("onReceivedError").d("errorCode : ${error?.errorCode},description :${error?.description}")
                            mLoadError = true
                            if (!mPresenter.onWebReceivedError(errorCode, description, failingUrl)) {
                                showFail()
                            }
                        }
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        Logger.tag("onReceivedError").d("errorCode : $errorCode,description : $description")
                        mLoadError = true
                        if (!mPresenter.onWebReceivedError(errorCode, description, failingUrl)) {
                            showFail()
                        }
                    }

                })
                .setWebChromeClient(object : WebChromeClient() {
                    override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        result?.cancel()
                        return true
                    }

                    override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?): Boolean {
                        result?.cancel()
                        return true
                    }

                    override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?): Boolean {
                        result?.cancel()
                        return true
                    }
                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        Logger.tag("onProgressChanged").d(newProgress)
                        if (newProgress == 100 && !mLoadError) {
                            showContent()
                            mPresenter.onProgressChanged(newProgress)
                        }
                    }
                })
                .createAgentWeb()
                .ready()
                .go(null)
    }

    protected fun getWebView(): WebView {
        return mAgentWeb.webCreator.get()
    }

    override fun onPause() {
        pauseWebView()
        super.onPause()
    }

    open fun pauseWebView() {
        mAgentWeb.webLifeCycle.onPause(false)
    }

    override fun onResume() {
        resumeWebView()
        super.onResume()
    }

    open fun resumeWebView() {
        mAgentWeb.webLifeCycle.onResume()
    }

    override fun onDestroyView() {
        destroyWebView()
        super.onDestroyView()
    }

    open fun destroyWebView() {
        mAgentWeb.webLifeCycle.onDestroy()
    }

    override fun onBackPressedSupport(): Boolean {
        return onWebViewDispatch() || super.onBackPressedSupport()
    }

    open fun onWebViewDispatch(): Boolean {
        return mAgentWeb.handleKeyEvent(
                KeyEvent.KEYCODE_BACK,
                KeyEvent(ACTION_DOWN, KeyEvent.KEYCODE_BACK))
    }

    override fun getAgentWeb(): AgentWeb {
        return mAgentWeb
    }

    override fun quickCallJs(jsFunctionName: String) {
        mAgentWeb.jsEntraceAccess.quickCallJs(jsFunctionName)
    }

    override fun addJavaObject(objectName: String, any: Any) {
        mAgentWeb.jsInterfaceHolder.addJavaObject(objectName, any)
    }

    override fun <T : Any> callJSBridgeCallback(callbackID: String, bean: T) {
        mAgentWeb.callJSBridgeCallback(callbackID, bean)
    }

    override fun <T : Any> callJSBridgeAction(name: String, bean: T) {
        mAgentWeb.callJSBridgeAction(name, bean)
    }

    override fun loadURL(url: String) {
        mAgentWeb.loader.loadUrl(url)
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        mAgentWeb.loader.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(baseUrl: String?, data: String?, mimeType: String?, encoding: String?, historyUrl: String?) {
        mAgentWeb.loader.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    override fun injectScript(script: String) {
        try {
            val encoded = Base64.encodeToString(script.toByteArray(), Base64.NO_WRAP)
            val javascript = "javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAgentWeb.webCreator.get().evaluateJavascript(javascript) {}
            } else {
                mAgentWeb.webCreator.get().loadUrl(javascript)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun reloadURL(showLoading: Boolean) {
        if (showLoading) {
            showLoading()
        }
        mAgentWeb.loader.reload()
    }

}