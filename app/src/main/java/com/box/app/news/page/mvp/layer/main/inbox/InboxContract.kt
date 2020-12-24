package com.box.app.news.page.mvp.layer.main.inbox

import com.box.app.news.bean.Channel
import com.box.app.news.bean.WorldcupMatch
import com.box.common.extension.app.mvp.base.MVPBaseContract
import com.box.common.extension.app.mvp.loading.MVPLoadingContract

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