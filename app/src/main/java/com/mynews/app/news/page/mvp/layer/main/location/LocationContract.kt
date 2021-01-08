package com.mynews.app.news.page.mvp.layer.main.location

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface LocationContract {

    interface View : MVPListContract.View {
        fun showLocationProgress()
        fun hideLocationProgress()
        fun showArrow()
        fun hideArrow()
        fun setLocationResult(result: String)
        fun setTitle(title:String)
    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {
        fun onClickCurrentLocation()
        fun onClickSearch()
    }

}