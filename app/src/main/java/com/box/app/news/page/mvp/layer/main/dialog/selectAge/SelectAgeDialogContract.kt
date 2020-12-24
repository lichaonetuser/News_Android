package com.box.app.news.page.mvp.layer.main.dialog.selectAge

import com.box.common.extension.app.mvp.dialog.MVPDialogContract

/**
 *
 */
class SelectAgeDialogContract {

    interface View : MVPDialogContract.View {

        fun setAge(ages:ArrayList<String>)

        fun getAge():Int
    }

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {

        fun onClickConfirm()
    }
}