package com.mynews.app.news.page.mvp.layer.main.weather.detail

import android.os.Bundle
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.remote.http.url.WebNewsUrls
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.WeatherRegionChangeEvent
import com.mynews.app.news.page.mvp.layer.main.location.LocationFragment
import com.mynews.app.news.page.mvp.layer.main.weather.guide.WeatherGuideFragment
import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserPresent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WeatherDetailPresenter : MVPBrowserPresent<WeatherDetailContract.View>(),
        WeatherDetailContract.Presenter<WeatherDetailContract.View> {

    private var mInvisibleTime = 0L

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadWeatherRegion()
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    private fun loadWeatherRegion() {
        val region = DataManager.Local.getUserLastSelectedRegion()
        if (!region.isEmptyRegion()) {
            mView?.setRegionName(region.name)
            val url = WebNewsUrls.getNewsWeatherUrl(region.cityCode)
            mView?.loadURL(url)
        }
    }

    override fun onClickChooseCity() {
        mView?.goFromRoot(LocationFragment::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWeatherRegionChangeEvent(event: WeatherRegionChangeEvent) {
        val region = event.region
        if (region.isEmptyRegion()) {
            mView?.replaceTo(WeatherGuideFragment::class.java)
            return
        } else {
            val url = WebNewsUrls.getNewsWeatherUrl(region.cityCode)
            mView?.setRegionName(region.name)
            mView?.showLoading()
            mView?.loadURL(url)
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        if (System.currentTimeMillis() - mInvisibleTime > 5 * 60 * 1000) {
            mView?.reloadURL(false)
        }
        mView?.quickCallJs("resetScroll")
    }

    override fun onViewInvisible() {
        super.onViewInvisible()
        mInvisibleTime = System.currentTimeMillis()
    }

}

