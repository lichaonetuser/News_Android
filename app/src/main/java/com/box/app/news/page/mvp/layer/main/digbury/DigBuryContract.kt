package com.box.app.news.page.mvp.layer.main.digbury

import com.box.app.news.bean.base.BaseNewsBean
import com.box.common.extension.app.mvp.base.MVPBaseContract

interface DigBuryContract {

    interface View : MVPBaseContract.View {
        fun showDeleteDialog(news: BaseNewsBean)
        fun setNews(news : BaseNewsBean)
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickDig()
        fun onClickBury()
        fun onClickDelete()
        fun onClickDeleteConfirm(isYes: Boolean)
    }

}