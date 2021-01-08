package com.mynews.app.news.page.mvp.layer.main.inbox

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

class InboxContract {

    interface View : MVPBaseContract.View {
        fun setCurrentPagePosition(position: Int)
        fun loadPages()
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onPageSelected(position: Int)
        fun onClickTab(currentIndex: Int, newIndex: Int)
    }

}