package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Article
import com.mynews.app.news.bean.Tag
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.item.base.BaseArticleItem
import com.mynews.app.news.item.base.holder.BaseNewsLeftImgViewHolder
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

class ArticleLeftImgItem(mBean: Article) : BaseArticleItem<ArticleLeftImgItem.ViewHolder>(mBean) {

    val tags: List<Tag>

    init {
        assert(mBean.listImageUrls.isNotEmpty(), { "至少包含一张图片才可创建，参见ARTICLE_ITEM_FACTORY" })
        tags = mBean.tags
    }

    override fun getLayoutRes(): Int {
        val enableNotInterested = DataManager.Memory.getAppConfig().enableNotInterested
        return if (enableNotInterested) {
            R.layout.item_news_left_img
        } else {
            R.layout.item_news_left_img_no_remove
        }
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        ImageManager.with(holder.news_img).load(mBean.listImageUrls.firstOrNull())
        holder.checkLineMax()
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsLeftImgViewHolder(view, adapter)
}