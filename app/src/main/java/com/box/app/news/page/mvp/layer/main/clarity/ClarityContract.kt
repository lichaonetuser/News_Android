package com.box.app.news.page.mvp.layer.main.clarity

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface ClarityContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}