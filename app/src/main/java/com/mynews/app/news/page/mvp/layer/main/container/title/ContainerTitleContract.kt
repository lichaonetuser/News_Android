package com.mynews.app.news.page.mvp.layer.main.container.title

import android.os.Bundle
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface ContainerTitleContract {

    interface View : MVPBaseContract.View {
        fun loadContainer(fragmentClassName: String, bundle: Bundle)
        fun setTitle(title: String)
        fun setStatusBarIsLight(isLight: Boolean)
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V>

}