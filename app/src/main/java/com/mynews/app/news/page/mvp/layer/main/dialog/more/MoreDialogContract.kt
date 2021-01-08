package com.mynews.app.news.page.mvp.layer.main.dialog.more

import android.os.Bundle
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract
import com.mynews.common.extension.share.ContentLink
import com.mynews.common.extension.share.IShareListener
import com.mynews.common.extension.share.SharePlatform
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem

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