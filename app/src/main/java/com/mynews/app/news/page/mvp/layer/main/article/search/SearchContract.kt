package com.mynews.app.news.page.mvp.layer.main.article.search

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface SearchContract {

    interface View : MVPListContract.View {
        fun setSearchHint(hint : String)
        fun doSearch(keyword : String)
        fun goBack() : Boolean
    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {
        fun setSearchHistory(keyword: String)
        fun getSpanSize(position : Int) : Int
    }

}