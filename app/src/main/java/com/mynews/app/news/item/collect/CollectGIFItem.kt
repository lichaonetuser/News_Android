package com.mynews.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.GIF
import com.mynews.app.news.item.base.BaseGifItem
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_gif.view.*

/**
 * 收藏GIF
 */
class CollectGIFItem(mBean: GIF)
    : BaseGifItem<CollectGIFItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectGIFItem.ViewHolder {
        return CollectGIFItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_gif


    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        private val newsCover = itemView.news_img
        private val sourceParams = itemView.news_source_txt.layoutParams as ConstraintLayout.LayoutParams


        fun bindInfo(mBean: GIF) {
            ImageManager.with(newsCover).load(mBean.coverImage)

            sourceParams.topToBottom = if (mBean.title.isEmpty()) R.id.news_img else R.id.news_title_txt
        }
    }
}