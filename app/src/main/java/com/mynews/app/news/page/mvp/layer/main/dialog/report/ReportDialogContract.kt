package com.mynews.app.news.page.mvp.layer.main.dialog.report

import android.os.Bundle
import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem

interface ReportDialogContract {

    interface View : MVPDialogContract.View {
        fun getAdapter(): CommonRecyclerAdapter
        fun goReportContent(bundle: Bundle)
        fun reportByEmail(content: String)
    }

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean
    }

}