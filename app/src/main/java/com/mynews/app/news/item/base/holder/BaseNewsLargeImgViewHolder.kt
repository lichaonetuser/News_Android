package com.mynews.app.news.item.base.holder

import android.view.View
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_left_img.view.*

open class BaseNewsLargeImgViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
    val news_img = itemView.news_img
}