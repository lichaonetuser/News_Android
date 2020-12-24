package com.box.app.news.page.mvp.layer.main.web.news

import android.os.Bundle
import com.box.app.news.bean.Channel
import com.box.app.news.event.EventManager
import com.box.app.news.event.refresh.NewsListRefreshEvent
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenter
import com.box.common.core.log.Logger
import com.yatatsu.autobundle.AutoBundleField
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SearchWebBrowserPresenter : WebBrowserPresenter<SearchWebBrowserContract.View>(),
        SearchWebBrowserContract.Presenter<SearchWebBrowserContract.View> {

    @AutoBundleField(required = false)
    override var mUrl = ""
    @AutoBundleField(required = false)
    override var mTitle: String = ""

    override var mAppendCommonParams = false
    override var mDispatchBack = false
}

