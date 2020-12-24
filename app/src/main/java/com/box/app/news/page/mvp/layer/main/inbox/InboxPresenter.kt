package com.box.app.news.page.mvp.layer.main.inbox

import android.os.Bundle
import com.box.app.news.analytics.AnalyticsKey
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.box.common.extension.app.mvp.loading.MVPLoadingPresent

class InboxPresenter<V : InboxContract.View> : MVPBasePresenter<V>(),
        InboxContract.Presenter<V> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (isNotRestore()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.INBOX, AnalyticsKey.Parameter.ENTER)
        }
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        mView?.loadPages()
    }

    override fun onPageSelected(position: Int) {
        if (position == 0) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.INBOX, AnalyticsKey.Parameter.SWITCH_TO_MESSAGE)
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.INBOX, AnalyticsKey.Parameter.SWITCH_TO_NOTIFY)
        }
    }

    override fun onClickTab(currentIndex: Int, newIndex: Int) {
        mView?.setCurrentPagePosition(newIndex)
    }

}