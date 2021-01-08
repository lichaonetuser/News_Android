package com.mynews.app.news.page.mvp.layer.main.location

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Region
import com.mynews.app.news.bean.list.ListRegion
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.WeatherRegionChangeEvent
import com.mynews.app.news.item.RegionItem
import com.mynews.app.news.page.mvp.layer.main.location.search.LocationSearchFragment
import com.mynews.app.news.page.mvp.layer.main.location.search.LocationSearchPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.location.sub.LocationSubFragment
import com.mynews.app.news.page.mvp.layer.main.location.sub.LocationSubPresenterAutoBundle
import com.mynews.app.news.util.CityUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.permission.PermissionException
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.location.RxLocation
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class LocationPresenter : MVPListPresent<LocationContract.View>(),
        LocationContract.Presenter<LocationContract.View> {

    @AutoBundleField(required = false)
    var mListRegion = ListRegion()
    @AutoBundleField(required = false)
    var mLastRegion = Region()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.ENTER_CHANGE_LOCATION)
        if (isRestore()) {
            loadDataComplete(Observable.just(mListRegion.regions)
                    .convertBeansToItems { bean -> RegionItem(bean) }
                    .blockingSingle())
        } else {
            loadData(0)
        }
        loadTitle()
    }

    private fun loadTitle() {
        val region = DataManager.Local.getUserLastSelectedRegion()
        val title = if (region.isEmptyRegion()) ResUtils.getString(R.string.Weather_ChooseCity) else region.name
        mView?.setTitle(title)
    }

    private fun loadCurrentLocation() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.CLICK_LOCATE_GPS)
        mView?.setLocationResult("")
        mView?.showLocationProgress()
        mView?.hideArrow()
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
                            mView?.hideLocationProgress()
                            val region = city.region
                            if (region.isEmptyRegion()) {
                                AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_UNSUPPORTED_CITY)
                                if (mLastRegion.isEmptyRegion()) {
                                    mView?.setLocationResult("")
                                    mView?.showArrow()
                                } else {
                                    mView?.setLocationResult(mLastRegion.name)
                                    mView?.hideArrow()
                                }
                                mView?.showToast(R.string.Tip_CurrentCityUnsupport)
                            } else {
                                mLastRegion = region
                                mView?.setLocationResult(mLastRegion.name)
                                mView?.hideArrow()
                                DataManager.Local.saveUserLastSelectedRegion(region)
                                CityUtils.updateHttpCityCodeParams(region.cityCode)
                                EventManager.post(WeatherRegionChangeEvent(region))
                            }
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                            when (throwable) {
                                is PermissionException -> AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_GPS_AUTHORIZATION_OFF)
                                else -> AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.LOCATE_FAIL_PHONE_LOCATE)
                            }
                            mView?.showToast(R.string.Tip_ServerLocateFail)
                            mView?.hideLocationProgress()
                            mView?.setLocationResult(mLastRegion.name)
                            if (mLastRegion.isEmptyRegion()) {
                                mView?.showArrow()
                            } else {
                                mView?.hideArrow()
                            }
                        }
                )
    }

    private fun loadUserLastSelectedRegion() {
        mLastRegion = DataManager.Local.getUserLastSelectedRegion()
        if (!mLastRegion.isEmptyRegion()) {
            mView?.setLocationResult(mLastRegion.name)
            mView?.hideLocationProgress()
            mView?.hideArrow()
        } else {
            loadCurrentLocation()
        }
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        val cacheDataItem = Observable.just(DataManager.Memory.getRegionList())
                .map { data ->
                    mListRegion = data
                    mListRegion.regions
                }
                .convertBeansToItems { bean -> RegionItem(bean) }
                .blockingFirst()
        if (cacheDataItem.isNotEmpty()) {
            loadDataComplete(cacheDataItem)
        } else {
            DataManager.Remote.getWeatherCityList()
                    .onErrorResumeNext(DataManager.Local.getRegionList())
                    .map { data ->
                        DataManager.Local.saveRegionList(data) //更新本地内容
                        DataManager.Memory.putRegionList(data) //更新内存
                        mListRegion = data
                        mListRegion.regions
                    }
                    .convertBeansToItems { bean -> RegionItem(bean) }
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = {
                                loadDataComplete(it)
                            },
                            onError = {
                                Logger.e(it)
                                loadDataFail()
                            }
                    )
        }
    }

    private fun getAllLastRegion(regions: List<Region>): ArrayList<Region> {
        val allLastRegions = arrayListOf<Region>()
        for (region in regions) {
            if (region.subRegions.isEmpty()) {
                allLastRegions.add(region)
            } else {
                allLastRegions.addAll(getAllLastRegion(region.subRegions))
            }
        }
        return allLastRegions
    }

    override fun onClickCurrentLocation() {
        loadCurrentLocation();
    }

    override fun onClickSearch() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.CLICK_SEARCH)
        if (mListRegion.regions.isEmpty()) {
            mView?.showToast(R.string.Common_Loading)
            return
        }
        val regions: ArrayList<Region> = getAllLastRegion(mListRegion.regions)
        mView?.goFromRoot(LocationSearchFragment::class.java, LocationSearchPresenterAutoBundle
                .Builder(regions)
                .bundle())
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val region = mListRegion.regions[position]
        val subRegions: ArrayList<Region> = region.subRegions
        if (subRegions.isNotEmpty()) {
            mView?.go(LocationSubFragment::class.java, LocationSubPresenterAutoBundle.builder(region.name, subRegions).bundle())
        }
        return super.onItemClick(position, item)
    }

}

