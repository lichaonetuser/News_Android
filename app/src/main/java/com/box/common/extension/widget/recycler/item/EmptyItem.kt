package com.box.common.extension.widget.recycler.item

import android.view.View
import com.box.app.news.R
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder

class EmptyItem<T : Any>(any: T) : BaseItem<T, EmptyItem.ViewHolder>(any) {

    override fun getLayoutRes() = R.layout.core_item_empry

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
    }

    open class ViewHolder(view: View, adapter: CommonRecyclerAdapter)
        : BaseViewHolder(view, adapter)

}