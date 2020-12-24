package com.box.app.news.item.base

import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.util.TimeUtils
import com.box.app.news.widget.FontTextView
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

abstract class BaseRelatedItem<BEAN : BaseNewsBean, VH : BaseNewsItem.ViewHolder>(mBean: BEAN)
    : BaseNewsItem<BEAN, VH>(mBean) {

    override fun getDisplayTime(): Long {
        return mBean.publishedAt
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        holder.news_source_txt?.text = mBean.sourceName
        holder.news_emit_time_txt?.text = TimeUtils.getDisplayTimeString(getDisplayTime())
        holder.news_title_txt?.setActivated(adapter.isSelected(position))
    }
}