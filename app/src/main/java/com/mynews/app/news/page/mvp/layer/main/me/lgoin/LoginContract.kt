package com.mynews.app.news.page.mvp.layer.main.me.lgoin

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface LoginContract {

    interface View : MVPBaseContract.View

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickAgreement()
        fun onClickFacebook()
        fun onClickTwitter()
        fun onClickGoogle()
    }

}