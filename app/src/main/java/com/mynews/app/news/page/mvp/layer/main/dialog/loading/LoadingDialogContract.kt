package com.mynews.app.news.page.mvp.layer.main.dialog.loading

import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract

interface LoadingDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
    }

}