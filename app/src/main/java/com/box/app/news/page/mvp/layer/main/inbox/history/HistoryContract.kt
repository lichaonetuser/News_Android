package com.box.app.news.page.mvp.layer.main.inbox.history

import com.box.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentContract

interface HistoryContract {

    interface View : BaseInboxContentContract.View

    interface Presenter<in V : View> : BaseInboxContentContract.Presenter<V>

}