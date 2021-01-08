package com.mynews.app.news.page.mvp.layer.main.font

import com.mynews.app.news.data.DataDictionary
import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface FontContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>{

        fun onFontSizeChange(size: DataDictionary.FontSize)

    }

}