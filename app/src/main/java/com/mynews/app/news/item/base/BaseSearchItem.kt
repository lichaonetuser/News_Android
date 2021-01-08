package com.mynews.app.news.item.base

import android.view.View
import com.mynews.app.news.bean.SearchData
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem

abstract class BaseSearchItem<VH : BaseSearchItem.ViewHolder>(mBean: SearchData)
    : BaseItem<SearchData, VH>(mBean) {

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {

    }

    open class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
    }

}