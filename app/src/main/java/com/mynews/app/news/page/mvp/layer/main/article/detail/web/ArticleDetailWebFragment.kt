package com.mynews.app.news.page.mvp.layer.main.article.detail.web

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.app.news.widget.WebViewOverScrollDecorAdapter
import com.mynews.common.core.browser.agent.AgentWeb
import com.mynews.common.core.log.Logger
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserFragment
import kotlinx.android.synthetic.main.fragment_common_browser_web.*
import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator

class ArticleDetailWebFragment : MVPBrowserFragment<ArticleDetailWebContract.View,
        ArticleDetailWebContract.Presenter<ArticleDetailWebContract.View>>(),
        ArticleDetailWebContract.View {

    override val mAttachSwipeBack = false
    override var mDispatchBack = false
    override val mPresenter = ArticleDetailWebPresenter()
    override val mLayoutRes = R.layout.fragment_article_detail_web
    override val mAgentWeb by lazy {
        AgentWeb.with(this)
                .setAgentWebParent(web_view_layout, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT))
                .setIndicatorColorWithHeight(ResUtils.getColor(R.color.color_11), 1)
                .setWebView(object : WebView(_mActivity) {
                    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                        super.onScrollChanged(l, t, oldl, oldt)
                        if (t > mMaxScrollHeight) {
                            mMaxScrollHeight = t.toLong()
                        }
                    }
                })
                .setWebViewClient(object : WebViewClient() {
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        showContent()
                    }

                    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                        super.onReceivedError(view, request, error)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Logger.tag("onReceivedError").d("errorCode : ${error?.errorCode},description :${error?.description}")
                        }

                        //不处理超时
                        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && error?.errorCode != WebViewClient.ERROR_TIMEOUT) {
                        //    mLoadError = true
                        //    showFail()
                        //}
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        Logger.tag("onReceivedError").d("errorCode : $errorCode,description : $description")
                        //不处理超时
                        //if (errorCode != WebViewClient.ERROR_TIMEOUT) {
                        //    mLoadError = true
                        //    showFail()
                        //}
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        mMaxScrollHeight = view?.height?.toLong() ?: 0L
                    }

                    override fun onScaleChanged(view: WebView?, oldScale: Float, newScale: Float) {
                        super.onScaleChanged(view, oldScale, newScale)
                        mScale = newScale
                    }
                })
                .setWebChromeClient(object : WebChromeClient() {


                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        Logger.tag("onProgressChanged").d(newProgress)
                        if (newProgress == 100 && !mLoadError) {
                            showContent()
                            mPresenter.onProgressChanged(newProgress)
                        }

                        var scale = mScale
                        if (scale <= 0) {
                            scale = view.scale
                        }
                        if (scale <= 0) {
                            scale = 1f
                        }

                        val contentHeight = view.contentHeight.toLong() * scale
                        if (contentHeight > mPageHeight) {
                            mPageHeight = contentHeight.toLong()
                        }
                        if (contentHeight > 0 && mMaxScrollHeight < view.height) {
                            mMaxScrollHeight = contentHeight.toLong()
                        }
                    }
                })
                .createAgentWeb()
                .ready()
                .go(null)
    }

    private var mPageHeight: Long = -1
    private var mMaxScrollHeight: Long = -1
    private var mScale = -1f

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        VerticalOverScrollBounceEffectDecorator(WebViewOverScrollDecorAdapter(mAgentWeb.webCreator.get()))
        showContent()
    }

    override fun back() {
        if (parentFragment is MVPBaseFragment<*, *>) {
            (parentFragment as MVPBaseFragment<*, *>).back()
        } else {
            super.back()
        }
    }

    override fun getPageHeight(): Long {
        return mPageHeight
    }

    override fun getReadHeight(): Long {
        return mMaxScrollHeight
    }

}


