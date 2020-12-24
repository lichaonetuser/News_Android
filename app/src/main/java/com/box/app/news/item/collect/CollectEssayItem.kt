package com.box.app.news.item.collect

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Essay
import com.box.app.news.item.base.BaseEssayItem
import com.box.app.news.item.base.holder.BaseEssayViewHolder
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_essay.view.*

/**
 * 收藏段子
 */
class CollectEssayItem(mBean: Essay)
    : BaseEssayItem<CollectEssayItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: CollectEssayItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.essayTextView.setText(mBean.text)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectEssayItem.ViewHolder {
        return CollectEssayItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_essay

    @Suppress("HasPlatformType")
    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseEssayViewHolder(view, adapter) {
        val essayTextView = view.news_essay_text

        init {
            essayTextView.setBold(true)
            essayTextView.setMaxLines(4)
            essayTextView.setEssayTextSize(15)
            essayTextView.setLineSpace(5)
        }
    }
}