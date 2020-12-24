package com.box.app.news.page.mvp.layer.main.dialog.more

import android.os.Bundle
import com.box.app.news.bean.base.BaseNewsBean
import com.box.common.extension.app.mvp.dialog.MVPDialogContract
import com.box.common.extension.share.ContentLink
import com.box.common.extension.share.IShareListener
import com.box.common.extension.share.SharePlatform
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.item.BaseItem

interface MoreDialogContract {

    interface View : MVPDialogContract.View {
        fun getShareAdapter(): CommonRecyclerAdapter
        fun getActionAdapter(): CommonRecyclerAdapter
        fun showDeleteDialog(news: BaseNewsBean)
        fun reportByEmail(content: String)
        fun shareLink(platform: SharePlatform, content: ContentLink, listener: IShareListener)
        fun goReport(bundle: Bundle)
    }

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean

        fun onClickDeleteConfirm(isYes: Boolean)
    }

}