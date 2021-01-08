package com.mynews.app.news.page.mvp.layer.main.clarity

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.ClarityChangeEvent
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.widget.NewsVideoView
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import eu.davidea.flexibleadapter.SelectableAdapter
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class ClarityPresenter : MVPListPresent<ClarityContract.View>(),
        ClarityContract.Presenter<ClarityContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        getAdapter()?.mode = SelectableAdapter.Mode.SINGLE
        loadData()
        val decoration =  CommonItemDecoration().withDivider(R.drawable.divider_common).withDrawDividerOnLastItem(true)
        mView?.setCommonItemDecoration(decoration)
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {
        Observable.just(ResUtils.getString(R.string.Setting_SmoothDefinition),
                ResUtils.getString(R.string.Setting_StandardDefinition),
                ResUtils.getString(R.string.Setting_HighDefinition))
                .toList()
                .convertBeansToItems(ItemFactory.CLARITY)
                .bindToLifecycle(this)
                .subscribeBy(
                        onSuccess = {
                            mView?.onLoadMoreComplete(it)
                            val clarity = DataManager.Local.getVideoClarity()
                            for (i in it.indices) {
                                val string = it[i].getModel(String::class.java)
                                if (string == clarity.text) {
                                    getAdapter()?.toggleSelection(i)
                                }
                            }
                        },
                        onError = {
                            Logger.e(it)
                        }
                )
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val clarityString = item.getModel(String::class.java) ?: return true
        val clarity = NewsVideoView.Clarity.stringValueOf(clarityString)
        when (clarity) {
            NewsVideoView.Clarity.SD -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_360P)
            NewsVideoView.Clarity.NORMAL -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_480P)
            NewsVideoView.Clarity.HD -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_720P)
        }
        DataManager.Local.saveVideoClarity(clarity)
        EventManager.post(ClarityChangeEvent(clarity))
        getAdapter()?.toggleSelection(position)
        return true
    }

}

