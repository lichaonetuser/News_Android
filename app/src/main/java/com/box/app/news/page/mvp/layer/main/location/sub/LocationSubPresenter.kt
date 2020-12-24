package com.box.app.news.page.mvp.layer.main.location.sub

import android.os.Bundle
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Region
import com.box.app.news.data.DataManager
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.WeatherRegionChangeEvent
import com.box.app.news.item.RegionItem
import com.box.app.news.util.CityUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.extension.app.mvp.loading.list.MVPListPresent
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import me.yokeyword.fragmentation.ISupportFragment

class LocationSubPresenter : MVPListPresent<LocationSubContract.View>(),
        LocationSubContract.Presenter<LocationSubContract.View> {

    @AutoBundleField
    var mTitle = ""
    @AutoBundleField
    @JvmField // 也可以用转换器处理这个问题
    var mRegions = arrayListOf<Region>()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setTitle(mTitle)
        loadData()
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        loadDataComplete(Observable.just(mRegions)
                .convertBeansToItems { bean -> RegionItem(bean) }
                .blockingSingle())
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        if (position < 0 || position >= mRegions.size) return super.onItemClick(position, item)

        val region = mRegions[position]
        val subRegions: ArrayList<Region> = region.subRegions
        if (subRegions.isNotEmpty()) {
            mView?.go(LocationSubFragment::class.java,
                    LocationSubPresenterAutoBundle.builder(region.name, subRegions)
                            .bundle(),
                    ISupportFragment.STANDARD)
        } else {
            AnalyticsManager.logEvent(AnalyticsKey.Event.WEATHER, AnalyticsKey.Parameter.CLICK_LOCATE_SELECT_CITY)
            DataManager.Local.saveUserLastSelectedRegion(region)
            CityUtils.updateHttpCityCodeParams(region.cityCode)
            EventManager.post(WeatherRegionChangeEvent(region))
            mView?.backToRoot()
        }
        return super.onItemClick(position, item)
    }
}

