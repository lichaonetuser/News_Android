package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.SearchData
import com.mynews.app.news.item.base.BaseSearchItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_search_history.view.*

class SearchHotwordHistoryItem(mBean: SearchData) :
        BaseSearchItem<SearchHotwordHistoryItem.ViewHolder>(mBean) {

    override fun getLayoutRes() = R.layout.item_search_history

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.content_txt.text = mBean.mContent.trim()
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseSearchItem.ViewHolder(view, mCommonAdapter) {
        val content_txt = itemView.content_txt
        val clear_img = itemView.clear_img

        init {
            bindItemChildViewClick(clear_img)
        }
    }

}