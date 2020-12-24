package com.box.app.news.page.mvp.layer.main.inbox.message

import com.box.app.news.page.mvp.layer.main.inbox.base.BaseInboxContentContract

interface MessageContract {

    interface View : BaseInboxContentContract.View

    interface Presenter<in V : View> : BaseInboxContentContract.Presenter<V>

}