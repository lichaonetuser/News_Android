package com.mynews.app.news.item.base

import com.mynews.app.news.bean.Article

abstract class BaseArticleItem<VH : BaseNewsItem.ViewHolder>(mBean: Article)
    : BaseNewsItem<Article, VH>(mBean){

    companion object {
        const val ItemViewTypeExtra = 0
    }

    override fun getItemViewType(): Int {
        return super.getItemViewType() - ItemViewTypeExtra
    }

}