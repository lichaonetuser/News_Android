package com.mynews.app.news.page.mvp.layer.main.web

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.app.news.bean.Share
import com.mynews.common.core.browser.agent.AgentWeb
import com.mynews.common.core.log.Logger
import com.mynews.common.core.util.ResUtils
import com.mynews.common.core.util.StatusBarUtils
import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserFragment
import kotlinx.android.synthetic.main.fragment_common_browser_web_title.*

open class WebBrowserFragment<in V : WebBrowserContract.View, out P : WebBrowserContract.Presenter<V>>
    : MVPBrowserFragment<V, P>(),
        WebBrowserContract.View {

    override val mAttachSwipeBack = true
    override val mPresenter = WebBrowserPresenter<V>() as P
    override val mLayoutRes = R.layout.fragment_common_browser_web_title
    override var mDispatchBack = false
    protected open var mNotifyStatusBarIsLight = true

    override val mAgentWeb by lazy {
        AgentWeb.with(this)
                .setAgentWebParent(web_view_layout, RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT))
                .setIndicatorColorWithHeight(ResUtils.getColor(R.color.color_11), 1)
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
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            val errorCode = error?.errorCode ?: 0
//                            val description = error?.description
//                            val failingUrl = request?.url.toString()
//                            Logger.tag("onReceivedError").d("errorCode : ${error?.errorCode},description :${error?.description}")
//                            mLoadError = true
//                            showFail()
//                            mPresenter.onWebReceivedError(errorCode, description, failingUrl)
//                        }
                    }

                    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
//                        Logger.tag("onReceivedError").d("errorCode : $errorCode,description : $description")
//                        mLoadError = true
//                        showFail()
//                        mPresenter.onWebReceivedError(errorCode, description, failingUrl)
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

    override fun setDispatchBack(dispatch: Boolean) {
        mDispatchBack = dispatch
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        //边界滑动回弹效果。前端需要取消，修改需要和前端沟通
        //VerticalOverScrollBounceEffectDecorator(WebViewOverScrollDecorAdapter(mAgentWeb.webCreator.get()))
        showContent()
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (mNotifyStatusBarIsLight) {
            StatusBarUtils.notifyStatusBarIsLight(_mActivity)
        }
    }

    override fun setTitle(title: String) {
        title_bar.setTitle(title)
    }

    override fun hideTitleBar() {
        title_bar.visibility = View.GONE
    }

    override fun hideStatusBar() {
        status_bar.visibility = View.GONE
    }

    override fun saveWebState(outState: Bundle) {
        mAgentWeb.webCreator.get().saveState(outState)
    }

    // 暂不可用
    override fun hideIndicator() {
        mAgentWeb.indicatorController.offerIndicator()?.hide()
    }

    // 暂不可用
    override fun showIndicator() {
        mAgentWeb.indicatorController.offerIndicator()?.show()
    }

    override fun restoreStateWebState(inState: Bundle) {
        mAgentWeb.webCreator.get().restoreState(inState)
    }

    override fun setShare(share: Share?) {
        if (share == null || share.url.isNullOrBlank()) {
            share_btn.visibility = View.GONE
        } else {
            share_btn.visibility = View.VISIBLE
            share_btn.setOnClickListener({
                mPresenter.onClickShare(share)
            })
        }
    }

}


