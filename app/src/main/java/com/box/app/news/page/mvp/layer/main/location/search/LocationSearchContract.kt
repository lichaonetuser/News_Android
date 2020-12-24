package com.box.app.news.page.mvp.layer.main.location.search

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface LocationSearchContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}