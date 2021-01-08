package com.mynews.app.news.item

import android.text.TextUtils
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.GIF
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BaseRelatedItem
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_gif.view.*

class RelatedGifItem(mBean: BaseNewsBean)
    : BaseRelatedItem<BaseNewsBean, RelatedGifItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_related_gif
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)

        if (mBean is GIF) {
            holder.bind(mBean as GIF)
            holder.news_title_txt?.setTitle(mBean.title)
        }
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        fun bind(gif: GIF) = with(view){
            ImageManager.with(gif_img).load(gif.coverImage)
            if (TextUtils.isEmpty(gif.title)) {
                news_title_txt.visibility = View.GONE
            } else {
                news_title_txt.visibility = View.VISIBLE
                news_title_txt.text = gif.title
            }
            news_source_txt.text = gif.sourceName
        }
    }
}