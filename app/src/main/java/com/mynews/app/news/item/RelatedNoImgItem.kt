package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BaseRelatedItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_article_no_img.view.*

class RelatedNoImgItem(mBean: BaseNewsBean)
    : BaseRelatedItem<BaseNewsBean, RelatedNoImgItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_related_article_no_img
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.news_title_txt?.setTitle(mBean.title)
        holder.source_txt?.text = mBean.sourceName
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        val source_txt = itemView.news_source_txt
    }
}