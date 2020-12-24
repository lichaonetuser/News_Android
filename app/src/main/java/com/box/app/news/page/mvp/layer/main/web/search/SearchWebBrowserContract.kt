package com.box.app.news.page.mvp.layer.main.web.news

import com.box.app.news.page.mvp.layer.main.web.WebBrowserContract

interface SearchWebBrowserContract {

    interface View : WebBrowserContract.View

    interface Presenter<in V : View> : WebBrowserContract.Presenter<V>

}