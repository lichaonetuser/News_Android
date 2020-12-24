package com.box.app.news.page.mvp.layer.main.dialog.loading

import com.box.common.extension.app.mvp.dialog.MVPDialogContract

interface LoadingDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
    }

}