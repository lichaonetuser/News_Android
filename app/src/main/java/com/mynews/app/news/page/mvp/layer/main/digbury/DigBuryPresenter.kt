package com.mynews.app.news.page.mvp.layer.main.digbury

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.NewsListChangeEvent
import com.mynews.app.news.util.ToastUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DigBuryPresenter : MVPBasePresenter<DigBuryContract.View>(),
        DigBuryContract.Presenter<DigBuryContract.View> {

    @AutoBundleField
    lateinit var mNewsBean: BaseNewsBean
    @AutoBundleField
    lateinit var mChannel: Channel
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setNews(mNewsBean)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickDig() {
        if (mNewsBean.isBuried) {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_BURY_CANCEL)
        }
        if (mNewsBean.isDigged) {
            ToastUtils.showToast(ResUtils.getString(R.string.Tip_CancelLikeSuccess))
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_DIG_CANCEL)
        } else {
            ToastUtils.showToast(ResUtils.getString(R.string.Tip_LikeSuccess))
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_DIG)
        }
        DataManager.Remote.toggleDigNews(mNewsBean, mChannel)
    }

    override fun onClickBury() {
        if (mNewsBean.isDigged) {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_DIG_CANCEL)
        }
        if (mNewsBean.isBuried) {
            ToastUtils.showToast(ResUtils.getString(R.string.Tip_CancelUnlikeSuccess))
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_BURY_CANCEL)
        } else {
            ToastUtils.showToast(ResUtils.getString(R.string.Tip_UnlikeSuccess))
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_BURY)
        }
        DataManager.Remote.toggleBuryNews(mNewsBean, mChannel)
    }

    override fun onClickDelete() {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_TRASH)
        mView?.showDeleteDialog(mNewsBean)
    }

    override fun onClickDeleteConfirm(isYes: Boolean) {
        if (isYes) {
            GSYVideoManager.releaseAllVideos()
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_TRASH_CONFIRM)
            DataManager.deleteArticleAndPostDelete(mNewsBean, mChannel)
                    .io()
                    .subscribeBy(onError = {})
        } else {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_NEWS_TRASH_CANCEL)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.news.aid != mNewsBean.aid) {
            return
        }
        when (event.action) {
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mNewsBean = event.news
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mNewsBean.updateInformation(event.news)
                    }
                }
                mView?.setNews(news = mNewsBean)
            }
            else -> {

            }
        }
    }

}

