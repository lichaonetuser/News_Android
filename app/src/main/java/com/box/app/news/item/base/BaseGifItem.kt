package com.box.app.news.item.base

import android.view.View
import com.box.app.news.bean.GIF
import com.box.app.news.bean.Video
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

abstract class BaseGifItem<VH : BaseNewsItem.ViewHolder>(bean: GIF)
    : BaseNewsItem<GIF, VH>(bean) {

    companion object {
        const val ItemViewTypeExtra = 10000
    }

    override fun getItemViewType(): Int {
        return super.getItemViewType() - ItemViewTypeExtra
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (isFavoriteStyle) {
            holder.news_emit_time_txt?.visibility = View.VISIBLE
        } else {
            holder.news_emit_time_txt?.visibility = View.INVISIBLE
        }
    }

}