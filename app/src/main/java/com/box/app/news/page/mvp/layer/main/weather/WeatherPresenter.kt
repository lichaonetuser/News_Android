package com.box.app.news.page.mvp.layer.main.weather

import android.os.Bundle
import com.box.app.news.data.DataManager
import com.box.app.news.page.mvp.layer.main.weather.detail.WeatherDetailFragment
import com.box.app.news.page.mvp.layer.main.weather.guide.WeatherGuideFragment
import com.box.common.extension.app.mvp.base.MVPBasePresenter

class WeatherPresenter : MVPBasePresenter<WeatherContract.View>(),
        WeatherContract.Presenter<WeatherContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        val region = DataManager.Local.getUserLastSelectedRegion()
        if (region.isEmptyRegion()) {
            mView?.loadContainer(WeatherGuideFragment::class.java)
        } else {
            mView?.loadContainer(WeatherDetailFragment::class.java)
        }
    }

}

