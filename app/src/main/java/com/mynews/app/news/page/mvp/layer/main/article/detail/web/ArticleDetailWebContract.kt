package com.mynews.app.news.page.mvp.layer.main.article.detail.web

import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserContract

interface ArticleDetailWebContract {

    interface View : MVPBrowserContract.View{
        fun getPageHeight(): Long
        fun getReadHeight(): Long
    }

    interface Presenter<in V : View> : MVPBrowserContract.Presenter<V>

}