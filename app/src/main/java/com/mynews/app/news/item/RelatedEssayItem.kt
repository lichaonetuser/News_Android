package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Essay
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BaseRelatedItem
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_joke.view.*

class RelatedEssayItem(mBean: BaseNewsBean)
    : BaseRelatedItem<BaseNewsBean, RelatedEssayItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_related_joke
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)

        if (mBean is Essay) {
            holder.bind(mBean as Essay)
        }
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        fun bind(essay: Essay) = with(view){
            joke_title_txt.setLineSpace(5)
            joke_title_txt.setText(essay.text)
            joke_source_txt.text = essay.sourceName
        }
    }
}