package com.mynews.app.news.page.mvp.layer.main.article.search

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.SearchData
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.local.preference.PreferenceAPI
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.list.MVPListPresent
import com.mynews.common.extension.widget.recycler.decoration.CommonItemDecoration
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

/**
 *
 */
open class SearchPresenter : MVPListPresent<SearchContract.View>(),
        SearchContract.Presenter<SearchContract.View> {

    companion object {
        val MAX_SEARCH_HISTORY = 3
    }

    @AutoBundleField(required = false)
    var mSearchHotwords = arrayListOf<String>()
    @AutoBundleField(required = false)
    var mSearchHotwordsIndex: Int = 0

    var mSearchHistory = mutableListOf<String>()

    var mSearchItemList = arrayListOf<SearchData>()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        // 如果bundle有数据，更新搜索框内hint
        when (mSearchHotwordsIndex) {
            in mSearchHotwords.indices -> mView?.setSearchHint(mSearchHotwords[mSearchHotwordsIndex])
        }

        AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.ENTER)

        val decoration =  CommonItemDecoration().withDivider(R.drawable.divider_common)
        mView?.setCommonItemDecoration(decoration)
    }

    override fun onEnterEnd() {
        super.onEnterEnd()

        DataManager.Remote.getSearchHotwords(DataDictionary.SearchType.ARTICLE.value)
                .map {
                    if (it.keywords.isNotEmpty()) {
                        mSearchHotwords.clear()
                        mSearchHotwords.addAll(it.keywords)
                    }
                    getSearchHistoryFromPreference()
                    initItem()
                    mSearchItemList
                }
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            notifyChange()
                        }, onError = {
                            Logger.d(it)
                        }
                )
    }

    private fun notifyChange() {
        mView?.getCommonAdapter()?.updateDataSet(Observable.just(mSearchItemList)
                .convertBeansToItems(ItemFactory.SEARCH_ITEM)
                .blockingFirst(), false)
        mView?.getCommonAdapter()?.setEnableLoadMore(false)
    }

    private fun initItem() {
        appendSearchHistoryItem()
        appendSearchHotwordsItem()
    }

    private fun appendSearchHistoryItem() {
        if (mSearchHistory.isEmpty()) {
            return
        }

        val temp = mSearchHistory.indices
                .map { SearchData(mType = SearchData.TYPE_SEARCH_HISTORY, mContent = mSearchHistory[it]) }
                .toMutableList()

        val clear = SearchData(mType = SearchData.TYPE_SEARCH_CLEAR_HISTORY, mContent = ResUtils.getString(R.string.Query_ClearSearchHistory))
        temp.add(clear)

        if (mSearchHotwords.isNotEmpty()) {
            val divider = SearchData(mType = SearchData.TYPE_SEARCH_DIVIDER)
            temp.add(divider)
        }

        mSearchItemList.addAll(0, temp)
    }

    private fun appendSearchHotwordsItem() {
        if (mSearchHotwords.isEmpty()) {
            return
        }

        val hotTitle = SearchData(mType = SearchData.TYPE_SEARCH_HOTWORD_TITLE, mContent = ResUtils.getString(R.string.Query_PopularSearches))
        mSearchItemList.add(hotTitle)

        mSearchHotwords.indices
                .map { SearchData(mType = SearchData.TYPE_SEARCH_HOTWORD, mContent = mSearchHotwords[it], mSpanSize = 1) }
                .forEach { mSearchItemList.add(it) }
    }

    private fun clearSearchHistoryItem() {
        val temp = mSearchItemList.filter {
            it.mType != SearchData.TYPE_SEARCH_HISTORY
                    && it.mType != SearchData.TYPE_SEARCH_CLEAR_HISTORY
                    && it.mType != SearchData.TYPE_SEARCH_DIVIDER
        }
        mSearchItemList.clear()
        mSearchItemList.addAll(temp)
    }

    private fun clearSearchHistoryData() {
        mSearchHistory.clear()
        PreferenceAPI.saveSearchHistory("")
    }

    private fun refreshSearchHistoryItem() {
        clearSearchHistoryItem()
        appendSearchHistoryItem()
    }

    override fun setSearchHistory(keyword: String) {
        if (mSearchHistory.contains(keyword)) {
            mSearchHistory.remove(keyword)
        }
        if (mSearchHistory.size >= MAX_SEARCH_HISTORY) {
            mSearchHistory.remove(mSearchHistory.last())
        }
        mSearchHistory.add(0, keyword)
        mSearchHistory = mSearchHistory.distinct().toMutableList()

        PreferenceAPI.saveSearchHistory(mSearchHistory.joinToString())
        refreshSearchHistoryItem()
        notifyChange()
    }

    private fun getSearchHistoryFromPreference() {
        val history = PreferenceAPI.getSearchHistory()
        mSearchHistory.clear()
        if (!history.isBlank() && !history.isEmpty()) {
            mSearchHistory.addAll(history.split(","))
        }
    }

    private fun removeSearchHistoryData(keyword: String) {
        mSearchHistory.remove(keyword)
        PreferenceAPI.saveSearchHistory(mSearchHistory.joinToString())
    }

    override fun getSpanSize(position: Int): Int {
        if (position in mSearchItemList.indices) {
            val searchData = mSearchItemList[position]
            return searchData.mSpanSize
        }
        return 2
    }

    private fun getItemData(position: Int): SearchData {
        if (position in mSearchItemList.indices) {
            return mSearchItemList[position]
        }
        return SearchData()
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val temp = getItemData(position)
        when (temp.mType) {
            SearchData.TYPE_SEARCH_HISTORY -> {
                mView?.doSearch(temp.mContent)
                setSearchHistory(temp.mContent)
                AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_HISTORY)
            }
            SearchData.TYPE_SEARCH_HOTWORD -> {
                mView?.doSearch(temp.mContent)
                setSearchHistory(temp.mContent)
                AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_POPULAR_SEARCH_ARTICLE)
            }
            SearchData.TYPE_SEARCH_CLEAR_HISTORY -> {
                removeSearchHistory(clear = true)
                AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_CLEAR_SEARCH_HISTORY)
            }
        }
        return super.onItemClick(position, item)
    }

    override fun onItemChildClick(position: Int, item: BaseItem<*, *>, id: Int): Boolean {
        when (id) {
            R.id.clear_img -> {
                removeSearchHistory(position = position, clear = false)
                AnalyticsManager.logEvent(AnalyticsKey.Event.SEARCH, AnalyticsKey.Parameter.CLICK_DELETE_SEARCH_RECORD)
            }
        }
        return super.onItemChildClick(position, item, id)
    }

    private fun removeSearchHistory(position: Int = -1, clear: Boolean = false) {
        // 非法数据
        if (!clear && position == -1) {
            return
        }

        if (!clear) {
            removeSearchHistoryData(getItemData(position).mContent)
        } else {
            clearSearchHistoryData()
        }
        if (mSearchHistory.isEmpty()) {
            // 先过滤掉不需要的类型的数据，获得list对象
            val list = mSearchItemList.filter {
                it.mType != SearchData.TYPE_SEARCH_HISTORY
                        && it.mType != SearchData.TYPE_SEARCH_CLEAR_HISTORY
                        && it.mType != SearchData.TYPE_SEARCH_DIVIDER
            }
            // 比较list对象和当前数据列表的差，表示一共有多少个数据被过滤掉了
            val sub: Int = Math.abs(mSearchItemList.size - list.size)
            // 循环处理item列表，并更新当前数据
            var i = 0
            while (i < sub) {
                mView?.getCommonAdapter()?.removeItem(0)
                i++
            }
            mSearchItemList.clear()
            mSearchItemList.addAll(list)
        } else {
            mSearchItemList.removeAt(position)
            mView?.getCommonAdapter()?.removeItem(position)
        }
    }

    override fun loadData(pageNum: Int, isRefresh: Boolean) {

    }
}