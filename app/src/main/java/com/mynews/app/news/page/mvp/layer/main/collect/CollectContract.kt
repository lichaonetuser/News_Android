package com.mynews.app.news.page.mvp.layer.main.collect

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface CollectContract {

    interface View : MVPListContract.View {
        fun setEditBtnText(text: String)
        fun setDeleteButtonShow(show: Boolean)
        fun refreshDeleteNum()
        fun setEditBtnEnabled(enabled: Boolean)
    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {
        fun onClickEditButton()
        fun onClickDeleteButton()
    }


}