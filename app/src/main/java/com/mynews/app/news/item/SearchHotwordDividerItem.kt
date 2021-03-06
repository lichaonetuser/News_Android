package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.SearchData
import com.mynews.app.news.item.base.BaseSearchItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_search_divider.view.*

class SearchHotwordDividerItem(mBean: SearchData) :
        BaseSearchItem<SearchHotwordDividerItem.ViewHolder>(mBean) {

    override fun getLayoutRes() = R.layout.item_search_divider

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.divider_view.visibility = View.VISIBLE
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseSearchItem.ViewHolder(view, mCommonAdapter) {
        val divider_view = itemView.divider_view
    }

}