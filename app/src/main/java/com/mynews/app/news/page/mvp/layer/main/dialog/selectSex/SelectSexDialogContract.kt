package com.mynews.app.news.page.mvp.layer.main.dialog.selectSex

import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract

/**
 *
 */
interface SelectSexDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onMaleSelect()
        fun onFemaleSelect()
    }
}