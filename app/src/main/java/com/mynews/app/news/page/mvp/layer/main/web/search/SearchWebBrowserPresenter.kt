package com.mynews.app.news.page.mvp.layer.main.web.news

import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenter
import com.yatatsu.autobundle.AutoBundleField

class SearchWebBrowserPresenter : WebBrowserPresenter<SearchWebBrowserContract.View>(),
        SearchWebBrowserContract.Presenter<SearchWebBrowserContract.View> {

    @AutoBundleField(required = false)
    override var mUrl = ""
    @AutoBundleField(required = false)
    override var mTitle: String = ""

    override var mAppendCommonParams = false
    override var mDispatchBack = false
}

