package com.box.app.news.page.mvp.layer.main.debug

import com.box.app.news.widget.NewsVideoView
import com.box.common.extension.app.mvp.base.MVPBaseContract

interface DebugContract {

    interface View : MVPBaseContract.View {
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
    }

}