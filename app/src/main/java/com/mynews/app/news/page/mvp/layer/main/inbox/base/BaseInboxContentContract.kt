package com.mynews.app.news.page.mvp.layer.main.inbox.base

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface BaseInboxContentContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}