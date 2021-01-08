package com.mynews.app.news.page.mvp.layer.main.me

import com.mynews.app.news.bean.InboxCountResponse
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface MeContract {

    interface View : MVPBaseContract.View {

        fun showLogin()

        fun showProfile()

        fun setInboxUnreadCount(countResponse: InboxCountResponse)

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickCollect()
        fun onClickMyComment()
        fun onClickMessage()
    }

}