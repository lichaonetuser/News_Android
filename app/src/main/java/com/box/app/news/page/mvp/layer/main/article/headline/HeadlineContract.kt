package com.box.app.news.page.mvp.layer.main.article.headline

import com.box.app.news.page.mvp.layer.main.list.news.NewsListContract
import com.box.app.news.page.mvp.layer.main.list.news.NewsListPresenter
import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface HeadlineContract : NewsListContract {

    interface View : NewsListContract.View {
    }

    interface Presenter<in V : View> : NewsListContract.Presenter<V> {
        fun getTitleTime(position : Int) : String
    }

}