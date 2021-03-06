package com.mynews.app.news.page.mvp.layer.main.weather.guide

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.WeatherRegionChangeEvent
import com.mynews.app.news.page.mvp.layer.main.location.LocationFragment
import com.mynews.app.news.page.mvp.layer.main.weather.detail.WeatherDetailFragment
import com.mynews.app.news.util.CityUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.permission.PermissionException
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.location.RxLocation
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WeatherGuidePresenter : MVPBasePresenter<WeatherGuideContract.View>(),
        WeatherGuideContract.Presenter<WeatherGuideContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickChooseCity() {
        mView?.goFromRoot(LocationFragment::class.java)
    }

    override fun onClickCurrentLocation() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.CLICK_LOCATE_GPS)
        mView?.showLocationProgress()
        RxLocation.getLastKnownAddress()
                .flatMap { address ->
                    DataManager.Remote
                            .checkCity(address)
                            .io()
                            .singleOrError()
                }
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onSuccess = { city ->
                            val region = city.region
                            if (region.isEmptyRegion()) {
                                AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_UNSUPPORTED_CITY)
                                mView?.showToast(R.string.Tip_CurrentCityUnsupport)
                                mView?.hideLocationProgress()
                            } else {
                                DataManager.Local.saveUserLastSelectedRegion(region)
                                CityUtils.updateHttpCityCodeParams(region.cityCode)
                                mView?.replaceTo(WeatherDetailFragment::class.java)
                            }
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                            when (throwable) {
                                is PermissionException -> {
                                    AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_GPS_AUTHORIZATION_OFF)
                                    mView?.showSettingDialogIfAlwaysDenied()
                                }
                                else -> AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_PHONE_LOCATE)
                            }
                            mView?.showToast(R.string.Tip_ServerLocateFail)
                            mView?.hideLocationProgress()
                        }
                )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWeatherRegionChangeEvent(event: WeatherRegionChangeEvent) {
        if (!event.region.isEmptyRegion()) {
            mView?.replaceTo(WeatherDetailFragment::class.java)
        }
    }
}
