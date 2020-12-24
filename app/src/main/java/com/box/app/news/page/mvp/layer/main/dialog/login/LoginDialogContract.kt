package com.box.app.news.page.mvp.layer.main.dialog.login

import com.box.common.extension.app.mvp.dialog.MVPDialogContract

interface LoginDialogContract {

    interface View : MVPDialogContract.View

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onClickLoginFacebook()
        fun onClickLoginTwitter()
        fun onClickLoginGoogle()
        fun onClickAgreement()
        fun onClickClose()
    }

}