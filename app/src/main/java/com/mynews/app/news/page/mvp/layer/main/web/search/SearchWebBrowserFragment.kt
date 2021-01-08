package com.mynews.app.news.page.mvp.layer.main.web.news

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.RelativeLayout
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.browser.agent.AgentWeb
import com.mynews.common.core.log.Logger
import kotlinx.android.synthetic.main.fragment_common_browser_web_title.*

class SearchWebBrowserFragment : WebBrowserFragment<SearchWebBrowserContract.View,
        SearchWebBrowserContract.Presenter<SearchWebBrowserContract.View>>(),
        SearchWebBrowserContract.View {

    override val mAttachSwipeBack = false
    override val mPresenter = SearchWebBrowserPresenter()

    override val mAgentWeb by lazy {
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
                        view?.clearHistory()
                        showContent()
                        if (view?.progress == 100) {
                            AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.SEARCHED_ARTICLE)
                        }
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
                            showFail()
                            mPresenter.onWebReceivedError(errorCode, description, failingUrl)
                        }
                        view?.clearHistory()
                        showContent()
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        Logger.tag("onReceivedError").d("errorCode : $errorCode,description : $description")
                        mLoadError = true
                        showFail()
                        mPresenter.onWebReceivedError(errorCode, description, failingUrl)
                        view?.clearHistory()
                        showContent()
                    }

                })
                .setWebChromeClient(object : WebChromeClient() {
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
                .go(mPresenter.mUrl)
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        super.initView(view, savedInstanceState)
        hideStatusBar()
        hideTitleBar()
    }

    override fun loadURL(url: String) {
        web_view_layout?.visibility = View.INVISIBLE
        super.loadURL(url)
    }

    override fun showContent(message: String) {
        showLayout()
        super.showContent(message)
    }

    private fun showLayout() {
        web_view_layout?.visibility = View.VISIBLE
    }
}


