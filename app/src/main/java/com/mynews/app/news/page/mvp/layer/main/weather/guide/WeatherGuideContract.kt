package com.mynews.app.news.page.mvp.layer.main.weather.guide

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface WeatherGuideContract {

    interface View : MVPBaseContract.View {
        fun showLocationProgress()
        fun hideLocationProgress()
        fun showSettingDialogIfAlwaysDenied()
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickChooseCity()
        fun onClickCurrentLocation()
    }

}