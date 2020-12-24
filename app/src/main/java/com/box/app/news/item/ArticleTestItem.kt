package com.box.app.news.item

import com.box.app.news.R
import com.box.app.news.bean.Article
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

class ArticleTestItem(mBean: Article) : ArticleRightImgItem(mBean) {

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        ImageManager.with(holder.news_img).load(R.drawable.font_sample_img)
    }

}