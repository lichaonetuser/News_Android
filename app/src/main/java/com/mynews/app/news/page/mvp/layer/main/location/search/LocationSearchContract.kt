package com.mynews.app.news.page.mvp.layer.main.location.search

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface LocationSearchContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}