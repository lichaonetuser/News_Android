package com.mynews.app.news.page.mvp.layer.main

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface MainContract {

    interface View : MVPBaseContract.View {
        fun changeTab(tab: IMainTab)
        fun setHasUnreadFeedback(hasUnread: Boolean)
        fun getCurrentTab(): IMainTab
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onTabReselected(position: Int)
        fun onTabSelected(oldPosition: Int, newPosition: Int)
        fun onResume()
        fun onPause()
    }

}