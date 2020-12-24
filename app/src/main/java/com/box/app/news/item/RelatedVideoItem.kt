package com.box.app.news.item

import android.text.TextUtils
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Video
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.item.base.BaseRelatedItem
import com.box.app.news.item.base.holder.BaseNewsRightImgViewHolder
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_article_right_img.view.*

class RelatedVideoItem(mBean: Video) : BaseRelatedItem<BaseNewsBean, RelatedVideoItem.ViewHolder>(mBean) {

    private val mDurationText by lazy {
        val duration = mBean.durationInterval * 1000
        return@lazy TimeUtils.getVideoDurationText(duration)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_related_video
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.duration_txt.text = mDurationText
        if (TextUtils.isEmpty(mBean.title)) {
            holder.news_title_txt?.setTitleVisibility(View.GONE)
        } else {
            holder.news_title_txt?.setTitleVisibility(View.VISIBLE)
            holder.news_title_txt?.setTitle(mBean.title)
        }
        holder.source_name?.text = mBean.sourceName
        ImageManager.with(holder.news_img).load((mBean as Video).coverImage)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsRightImgViewHolder(view, adapter) {
        val duration_txt = itemView.duration_txt
        val source_name = itemView.news_source_txt
    }

}