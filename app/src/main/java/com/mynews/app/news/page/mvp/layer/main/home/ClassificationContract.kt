package com.mynews.app.news.page.mvp.layer.main.home

import com.mynews.app.news.bean.Channel
import com.mynews.app.news.page.mvp.layer.main.IMainTab
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface ClassificationContract {

    interface View : MVPBaseContract.View {
        fun setChannels(channels: List<Channel>)
        fun setDispatchBack(dispatchBack: Boolean)
        fun getCurrentPagePosition(): Int
        fun setCurrentPagePosition(position: Int)
        fun setSearchData(currentHotword : String)
        fun getTabIco(ico : String)
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onPageSelected(position: Int)
        fun onClickTab(currentIndex: Int, newIndex: Int)
        fun getSearchHotwords(type : Int)
        fun switchSearchHotwords()
        fun onSearchClick()
        fun onChannelEditClick()
    }

}