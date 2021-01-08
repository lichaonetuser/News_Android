package com.mynews.app.news.page.mvp.layer.main.debug

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface DebugContract {

    interface View : MVPBaseContract.View {
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
    }

}