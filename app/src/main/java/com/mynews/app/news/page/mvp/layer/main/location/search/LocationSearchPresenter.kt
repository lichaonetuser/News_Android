package com.mynews.app.news.page.mvp.layer.main.location.search

import android.os.Bundle
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Region
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.WeatherRegionChangeEvent
import com.mynews.app.news.item.RegionItem
import com.mynews.app.news.util.CityUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable

class LocationSearchPresenter : MVPListPresent<LocationSearchContract.View>(),
        LocationSearchContract.Presenter<LocationSearchContract.View> {


    @AutoBundleField
    @JvmField // 也可以用转换器处理这个问题
    var mRegions = arrayListOf<Region>()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        loadData()
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        loadDataComplete(Observable.just(mRegions)
                .convertBeansToItems { bean -> RegionItem(bean, false) }
                .blockingSingle())
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.CLICK_LOCATE_SELECT_CITY)
        val region = item.getModel(Region::class.java)
        if (region != null) {
            DataManager.Local.saveUserLastSelectedRegion(region)
            CityUtils.updateHttpCityCodeParams(region.cityCode)
            EventManager.post(WeatherRegionChangeEvent(region))
            mView?.backToRoot()
        }
        return super.onItemClick(position, item)
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {

    }

}

