package com.box.app.news.page.mvp.layer.main.web.news

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.webkit.*
import android.widget.RelativeLayout
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.common.core.browser.agent.AgentWeb
import com.box.common.core.log.Logger
import kotlinx.android.synthetic.main.fragment_common_browser_web_title.*

class NewsWebBrowserFragment : WebBrowserFragment<NewsWebBrowserContract.View,
        NewsWebBrowserContract.Presenter<NewsWebBrowserContract.View>>(),
        NewsWebBrowserContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = NewsWebBrowserPresenter()
    override var mNotifyStatusBarIsLight = false

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

}


