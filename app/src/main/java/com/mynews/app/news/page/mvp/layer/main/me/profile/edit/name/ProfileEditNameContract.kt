package com.mynews.app.news.page.mvp.layer.main.me.profile.edit.name

import com.mynews.app.news.bean.Account
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface ProfileEditNameContract {

    interface View : MVPBaseContract.View {
        fun setAccount(account: Account)
        fun showProgress()
        fun hideProgress()
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickDone(name: String)
    }

}