package com.mynews.app.news.page.mvp.layer.main.dialog.font

import com.mynews.app.news.data.DataDictionary
import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract

interface FontSizeDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onFontSizeChange(size: DataDictionary.FontSize)
    }

}