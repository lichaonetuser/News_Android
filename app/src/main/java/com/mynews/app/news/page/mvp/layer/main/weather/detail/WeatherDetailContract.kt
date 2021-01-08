package com.mynews.app.news.page.mvp.layer.main.weather.detail

import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserContract

interface WeatherDetailContract {

    interface View : MVPBrowserContract.View {
        fun setRegionName(name: String)
    }

    interface Presenter<in V : View> : MVPBrowserContract.Presenter<V> {
        fun onClickChooseCity()
    }

}