package com.box.app.news.page.mvp.layer.main.font

import com.box.app.news.data.DataDictionary
import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface FontContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>{

        fun onFontSizeChange(size: DataDictionary.FontSize)

    }

}