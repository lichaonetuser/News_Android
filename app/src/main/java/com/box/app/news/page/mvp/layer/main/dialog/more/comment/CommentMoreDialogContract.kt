package com.box.app.news.page.mvp.layer.main.dialog.more.comment

import android.os.Bundle
import com.box.app.news.bean.base.BaseNewsBean
import com.box.common.extension.app.mvp.dialog.MVPDialogContract
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.item.BaseItem

interface CommentMoreDialogContract {

    interface View : MVPDialogContract.View {
        fun getAdapter(): CommonRecyclerAdapter
        fun goReply(bundle: Bundle)
        fun goReport(bundle: Bundle)
        fun showDeleteDialog()
    }

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean
        fun onClickDeleteConfirm(isYes: Boolean)
    }

}