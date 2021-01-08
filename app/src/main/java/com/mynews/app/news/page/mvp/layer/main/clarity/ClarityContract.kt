package com.mynews.app.news.page.mvp.layer.main.clarity

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface ClarityContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}