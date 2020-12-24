package com.box.app.news.page.mvp.layer.main.me.profile.edit.name

import com.box.app.news.bean.Account
import com.box.common.extension.app.mvp.base.MVPBaseContract

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