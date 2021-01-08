package com.mynews.app.news.page.mvp.layer.main.article

import com.mynews.app.news.bean.Channel
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface ArticleContract {

    interface View : MVPBaseContract.View {
        fun setChannels(channels: List<Channel>)
        fun setDispatchBack(dispatchBack: Boolean)
        fun getCurrentPagePosition(): Int
        fun setCurrentPagePosition(position: Int)
        fun setSearchData(currentHotword : String)
        fun updateChannels(channels: List<Channel>, position: Int, forceUpdatePostion: Boolean) //更新换过位置的标题
        fun hideOrShowChannelEditTip(show: Boolean)//接手EventBus广播，是否消除频道编辑按钮右上角的提示
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