package com.box.app.news.item.collect

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.app.news.item.base.BaseArticleItem
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

/**
 * 收藏文章无图
 */
class CollectArticleNoImgItem(mBean: Article)
    : BaseArticleItem<CollectArticleNoImgItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectArticleNoImgItem.ViewHolder {
        return CollectArticleNoImgItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_no_img


    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter)
}