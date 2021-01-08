package com.mynews.app.news.page.mvp.layer.main.weather

import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface WeatherContract {

    interface View : MVPBaseContract.View {

        fun <F : CoreBaseFragment> loadContainer(clazz: Class<F>)

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V>
}