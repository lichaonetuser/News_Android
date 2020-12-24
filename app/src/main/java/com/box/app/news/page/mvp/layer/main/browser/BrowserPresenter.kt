package com.box.app.news.page.mvp.layer.main.browser

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.Article
import com.box.app.news.bean.Channel
import com.box.app.news.bean.ExtraInfo
import com.box.app.news.bean.Share
import com.box.app.news.data.DataManager
import com.box.app.news.item.MoreItem
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.weather.detail.WeatherDetailFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.box.common.extension.app.mvp.bindToLifecycle
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy

class BrowserPresenter : MVPBasePresenter<BrowserContract.View>(),
        BrowserContract.Presenter<BrowserContract.View> {

    @AutoBundleField
    var mUrl = ""
    @AutoBundleField
    var mTitle = ""
    @AutoBundleField
    var mRefer = AppLogKey.CRN.UNKNOWN
    @AutoBundleField
    var mReferId = ""

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (isNotRestore()){
            val info = ExtraInfo(cE = AppLogKey.CE.WEB_PAGE, cL = AppLogKey.CL.ENTER, cRN = mRefer, cRI = mReferId)
            AppLogManager.logEvent(info)
        }
        mView?.loadContainer(
                WebBrowserFragment::class.java,
                WebBrowserPresenterAutoBundle.builder(mUrl, mTitle)
                        .mSwipeBackEnable(false)
                        .mDispatchBack(false)
                        .mFullScreen(true)
                        .bundle())
        mView?.setTitle(mTitle)
        loadAppConfigShare()
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
                .mReferId(mReferId)
                .mShare(share)
                .bundle(), hideSelf = false)
    }

    private fun loadAppConfigShare() {
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

    override fun onViewInvisible() {
        super.onViewInvisible()
        val info = ExtraInfo(cE = AppLogKey.CE.WEB_PAGE, cL = AppLogKey.CL.STAY_PAGE, cRN = mRefer, cRI = mReferId,
                enterTime = mVisibleTime, duration = System.currentTimeMillis() - mVisibleTime)
        AppLogManager.logEvent(info)
    }
}

