package com.box.app.news.page.mvp.layer.main

import android.os.Bundle
import com.box.app.news.AppHelper
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.FeedbackUnread
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.FeedbackHasUnreadChangeEvent
import com.box.app.news.event.change.InboxUnreadCountChangeEvent
import com.box.app.news.event.change.MainTabChangeEvent
import com.box.app.news.event.refresh.NewsListRefreshEvent
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.rx.schedulers.io
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.box.common.extension.app.mvp.bindToLifecycle
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class MainPresenter : MVPBasePresenter<MainContract.View>(),
        MainContract.Presenter<MainContract.View> {


    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
        checkFeedbackHasUnread()
        checkInboxUnreadCount()
        updateCurrentTabWithAnalyticsHelper()
    }

    override fun onResume() {
//        checkSportsStatus()
    }

    override fun onPause() {
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onTabSelected(oldPosition: Int, newPosition: Int) {
        updateCurrentTabWithAnalyticsHelper()
        AnalyticsManager.logEvent(mView?.getCurrentTab()?.getAnalyticsEventKey()
                ?: "", AnalyticsKey.Parameter.ENTER)
        GSYVideoManager.releaseAllVideos()
    }

    override fun onTabReselected(position: Int) {
        updateCurrentTabWithAnalyticsHelper()
        when (mView?.getCurrentTab()) {
            MainTabEnum.NEWS, MainTabEnum.VIDEO -> {
                AnalyticsManager.logEvent(mView?.getCurrentTab()?.getAnalyticsEventKey()
                        ?: "", AnalyticsKey.Parameter.CLICK_TAB_TO_REFRESH)
                EventManager.post(NewsListRefreshEvent(DataManager.Memory.getCurrentClickedChannel()
                        ?: return))
            }
            else -> {
            }
        }
        GSYVideoManager.releaseAllVideos()
    }

    private fun checkFeedbackHasUnread() {
        DataManager.Remote.getFeedbackHasUnread()
                .map { t: FeedbackUnread -> t.hasUnread }
                .bindToLifecycle(this)
                .io()
                .subscribeBy(
                        onNext = { hasUnread ->
                            if (hasUnread) {
                                EventManager.post(FeedbackHasUnreadChangeEvent(true))
                            }
                        },
                        onError = {

                        })
    }

    private fun checkInboxUnreadCount() {
        DataManager.Remote.getInboxCount()
                .bindToLifecycle(this)
                .io()
                .subscribeBy(
                        onNext = {
                            EventManager.post(InboxUnreadCountChangeEvent(it))
                        },
                        onError = {

                        })
    }

    private fun updateCurrentTabWithAnalyticsHelper() {
        AppHelper.currentMainTab = mView?.getCurrentTab() ?: return
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFeedbackHasUnreadChangeEvent(event: FeedbackHasUnreadChangeEvent) {
        mView?.setHasUnreadFeedback(event.hasUnread)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInboxUnreadCountChangeEvent(event: InboxUnreadCountChangeEvent) {
        val count = event.countResponse.messageCount + event.countResponse.pushHistoryCount
        mView?.setHasUnreadFeedback(count > 0)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMainTabChangeEvent(event: MainTabChangeEvent) {
        mView?.changeTab(event.tab)
    }

}

