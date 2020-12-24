package com.box.app.news.page.mvp.layer.main.list.related

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface RelatedContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}