package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.SearchData
import com.mynews.app.news.item.base.BaseSearchItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_search_hotword_title.view.*

class SearchHotwordTitleItem(mBean: SearchData) :
        BaseSearchItem<SearchHotwordTitleItem.ViewHolder>(mBean){

    override fun getLayoutRes() = R.layout.item_search_hotword_title

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.content_txt.text = mBean.mContent
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseSearchItem.ViewHolder(view, mCommonAdapter) {
        val content_txt = itemView.content_txt
    }

}