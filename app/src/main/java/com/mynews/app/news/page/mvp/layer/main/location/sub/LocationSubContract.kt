package com.mynews.app.news.page.mvp.layer.main.location.sub

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface LocationSubContract {

    interface View : MVPListContract.View {

        fun setTitle(title: String)

    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}