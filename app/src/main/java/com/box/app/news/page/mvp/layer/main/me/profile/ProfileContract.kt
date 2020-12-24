package com.box.app.news.page.mvp.layer.main.me.profile

import com.box.app.news.bean.Account
import com.box.common.extension.app.mvp.base.MVPBaseContract

interface ProfileContract {

    interface View : MVPBaseContract.View {
        fun setAccount(account: Account)
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickProfile()
    }

}