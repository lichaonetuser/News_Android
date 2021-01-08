package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.SearchData
import com.mynews.app.news.item.base.BaseSearchItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_search_hotword.view.*

class SearchHotwordItem(mBean: SearchData) :
        BaseSearchItem<SearchHotwordItem.ViewHolder>(mBean)  {

    override fun getLayoutRes() = R.layout.item_search_hotword

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.content_txt.text = mBean.mContent
        holder.divider_view.visibility = View.VISIBLE
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseSearchItem.ViewHolder(view, mCommonAdapter) {
        val content_txt = itemView.content_txt
        val divider_view = itemView.divider_view
    }

}