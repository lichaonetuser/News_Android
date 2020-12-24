package com.box.app.news.page.mvp.layer.main.dialog.report.content

import com.box.common.extension.app.mvp.dialog.MVPDialogContract

interface ReportContentDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onClickSubmit(content: String)
    }
}