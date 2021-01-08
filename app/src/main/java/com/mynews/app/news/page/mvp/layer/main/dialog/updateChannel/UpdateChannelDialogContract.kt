package com.mynews.app.news.page.mvp.layer.main.dialog.updateChannel

import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract

/**
 *
 */
class UpdateChannelDialogContract{

    interface View: MVPDialogContract.View

    interface Presenter<in V : UpdateChannelDialogContract.View> : MVPDialogContract.Presenter<V> {
        fun onClickSkip()
        fun onClickConfirm()
    }
}