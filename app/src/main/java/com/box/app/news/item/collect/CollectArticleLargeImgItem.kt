package com.box.app.news.item.collect

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_large_img.view.*

/**
 * 收藏文章大图
 */
class CollectArticleLargeImgItem(mBean: Article)
    : BaseArticleItem<CollectArticleLargeImgItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectArticleLargeImgItem.ViewHolder {
        return CollectArticleLargeImgItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_large_img

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        private val newsImg = itemView.news_img

        fun bindInfo (mBean:Article) {
            ImageManager.with(newsImg).load(mBean.listImageUrls.firstOrNull())
        }
    }
}