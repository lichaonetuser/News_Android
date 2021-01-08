package com.mynews.app.news.page.mvp.layer.main.me

import android.os.Bundle
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.InboxUnreadCountChangeEvent
import com.mynews.app.news.event.login.LoginEvent
import com.mynews.app.news.event.login.LogoutEvent
import com.mynews.app.news.page.mvp.layer.main.collect.CollectFragment
import com.mynews.app.news.page.mvp.layer.main.inbox.InboxFragment
import com.mynews.app.news.page.mvp.layer.main.list.comment.my.MyCommentFragment
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MePresenter : MVPBasePresenter<MeContract.View>(),
        MeContract.Presenter<MeContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInboxUnreadCountChangeEvent(event: InboxUnreadCountChangeEvent) {
        mView?.setInboxUnreadCount(event.countResponse)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.success) {
            mView?.showProfile()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLogoutEvent(event: LogoutEvent) {
        if (event.success) {
            mView?.showLogin()
        }
    }

    override fun onClickCollect() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_FAVORED)
        mView?.goFromRoot(CollectFragment::class.java)
    }

    override fun onClickMyComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_MY_COMMENT)
        mView?.goFromRoot(MyCommentFragment::class.java)
    }

    override fun onClickMessage() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_INBOX)
        mView?.goFromRoot(InboxFragment::class.java)
    }

}

