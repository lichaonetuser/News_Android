package com.mynews.app.news.page.mvp.layer.main.article.headline

import com.mynews.app.news.page.mvp.layer.main.list.news.NewsListContract

interface HeadlineContract : NewsListContract {

    interface View : NewsListContract.View {
    }

    interface Presenter<in V : View> : NewsListContract.Presenter<V> {
        fun getTitleTime(position : Int) : String
    }

}