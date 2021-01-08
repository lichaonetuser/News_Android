package com.mynews.app.news.page.mvp.layer.main.web.news

import android.os.Bundle
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.refresh.NewsListRefreshEvent
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenter
import com.yatatsu.autobundle.AutoBundleField
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class NewsWebBrowserPresenter : WebBrowserPresenter<NewsWebBrowserContract.View>(),
        NewsWebBrowserContract.Presenter<NewsWebBrowserContract.View> {

    @AutoBundleField
    override var mUrl = ""
    @AutoBundleField
    override var mTitle = ""
    @AutoBundleField(required = false)
    override var mFullScreen = false
    @AutoBundleField(required = false)
    override var mSwipeBackEnable = false
    @AutoBundleField(required = false)
    override var mAppendCommonParams = true
    @AutoBundleField(required = false)
    override var mHideIndicator = true
    @AutoBundleField(required = false)
    override var mDispatchBack = false
    @AutoBundleField(required = false)
    var mChannel = Channel()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun loadAppConfgShare() {
        // do nothing
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Suppress("unused")
    fun onNewsListRefreshEvent(event: NewsListRefreshEvent) {
        if (event.channel != mChannel) {
            return
        }
        mView?.reloadURL()
    }
}

