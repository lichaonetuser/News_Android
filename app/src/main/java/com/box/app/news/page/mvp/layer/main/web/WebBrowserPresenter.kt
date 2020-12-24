package com.box.app.news.page.mvp.layer.main.web

import android.net.Uri
import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.bean.Article
import com.box.app.news.bean.Channel
import com.box.app.news.bean.Share
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.item.MoreItem
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.openurl.OpenUrlManager
import com.box.common.core.js.JSBaseAndroidObject
import com.box.common.core.js.JSBridge
import com.box.common.core.log.Logger
import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.extension.addQueryParameters
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserPresent
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.HttpUrl
import org.json.JSONObject

open class WebBrowserPresenter<V : WebBrowserContract.View> : MVPBrowserPresent<V>(),
        WebBrowserContract.Presenter<V> {

    @AutoBundleField
    open var mUrl = ""
    @AutoBundleField
    open var mTitle = ""
    @AutoBundleField(required = false)
    open var mFullScreen = false
    @AutoBundleField(required = false)
    open var mSwipeBackEnable = true
    @AutoBundleField(required = false)
    open var mAppendCommonParams = true
    @AutoBundleField(required = false)
    open var mHideIndicator = false
    @AutoBundleField(required = false)
    open var mDispatchBack = true

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setTitle(mTitle)
        if (mFullScreen) {
            mView?.hideTitleBar()
            mView?.hideStatusBar()
        }
        mView?.setSwipeBackEnable(mSwipeBackEnable)

        try {
            if (mUrl.isNotEmpty()) {
                mUrl = Uri.decode(mUrl)
            }
        } catch (e: Exception) {

        }
        if (savedState == null) {
            if (mAppendCommonParams) {
                mView?.loadURL(getAppendCommonParamsUrl(mUrl))
            } else {
                mView?.loadURL(mUrl)
            }
        } else {
            mView?.restoreStateWebState(savedState)
        }
        mView?.addJavaObject("WebViewJSBridge", JSAndroidObject())

        if (mHideIndicator) {
            mView?.hideIndicator()
        } else {
            mView?.showIndicator()
        }
        mView?.setDispatchBack(mDispatchBack)
        loadAppConfgShare()
    }

    override fun onClickShare(share: Share) {
        //TODO 统计
        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                .builder(Article(), Channel(), AnalyticsKey.Event.NEWS_DETAIL, "")
                .mMoreShare(arrayListOf(
                        MoreItem.More(MoreItem.More.Type.SHARE_FACEBOOK, R.drawable.share_facebook, ResUtils.getString(R.string.Common_Facebook)),
                        MoreItem.More(MoreItem.More.Type.SHARE_TWITTER, R.drawable.share_twitter, ResUtils.getString(R.string.Common_Twitter)),
                        MoreItem.More(MoreItem.More.Type.SHARE_LINE, R.drawable.share_line, ResUtils.getString(R.string.Common_LINE))))
                .mMoreAction(arrayListOf(
                        MoreItem.More(MoreItem.More.Type.SHARE_COPY, R.drawable.share_copy, ResUtils.getString(R.string.Common_CopyLink)),
                        MoreItem.More(MoreItem.More.Type.SHARE_SYSTEM, R.drawable.share_system, ResUtils.getString(R.string.Common_SystemShare)),
                        MoreItem.More(MoreItem.More.Type.SHARE_MESSAGE, R.drawable.share_messages, ResUtils.getString(R.string.Common_Message)),
                        MoreItem.More(MoreItem.More.Type.SHARE_MAIL, R.drawable.share_mail, ResUtils.getString(R.string.Common_Mail))
                ))
                .mReferName(AppLogKey.Refer.ARTICLE_DETAIL)
                .mReferId(mUrl)
                .mShare(share)
                .bundle(), hideSelf = false)
    }

    override fun onSave(outState: Bundle) {
        super.onSave(outState)
        mView?.saveWebState(outState)
    }

    open fun loadAppConfgShare() {
        if (mFullScreen){
            return
        }
        DataManager.Remote.getAppConfigShare(mUrl)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            mView?.setShare(it.share)
                        },
                        onError = {
                            Logger.e(it)
                        }
                )
    }

    fun getAppendCommonParamsUrl(url: String): String {
        val originHttpUrl = HttpUrl.parse(url) ?: return url
        val returnHttpUrl = originHttpUrl.newBuilder()
                .addQueryParameters(HttpManager.getCommonParams())
                .build()
        return returnHttpUrl.toString()
    }

    override fun onWebPageFinished(url: String?) {
        super.onWebPageFinished(url)
        mView?.injectScript(JSBridge.script)
    }

    override fun onViewVisible() {
        super.onViewVisible()
        mView?.quickCallJs("did_appear")
    }

    inner class JSAndroidObject : JSBaseAndroidObject() {

        override fun onCallbackAction(callbackId: String, action: String, actionType: String, parameters: JSONObject?) {
            when (action) {
                DataDictionary.JSArticleDetailAction.OPEN_URL.value -> {
                    val url = parameters?.optString("url")
                    OpenUrlManager.checkOpenUrl(url ?: return)
                }
            }
        }

        override fun onError(e: Exception) {

        }
    }


}

