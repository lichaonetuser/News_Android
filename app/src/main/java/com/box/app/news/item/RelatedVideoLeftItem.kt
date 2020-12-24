package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.item.base.BaseRelatedItem
import com.box.app.news.item.base.holder.BaseNewsRightImgViewHolder
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_article_right_img.view.*

class RelatedVideoLeftItem(mBean: BaseNewsBean) : BaseRelatedItem<BaseNewsBean, RelatedVideoLeftItem.ViewHolder>(mBean) {

    private val mDurationText by lazy {
        if (mBean is Video) {
            val duration = mBean.durationInterval * 1000
            return@lazy TimeUtils.getVideoDurationText(duration)
        }
        return@lazy ""
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_related_article_left_img
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        when (mBean) {
            is Video -> {
                holder.duration_txt.visibility = View.VISIBLE
                holder.duration_txt.text = mDurationText
                ImageManager.with(holder.news_img).load((mBean as Video).coverImage)
            }
            is Article -> {
                holder.duration_txt.visibility = View.GONE
                ImageManager.with(holder.news_img).load((mBean as Article).listImageUrls.firstOrNull())
            }
            is Image -> {
                holder.duration_txt.visibility = View.GONE
                val imageInfos: List<ImageInfo>? = (mBean as Image).images
                if (imageInfos != null && imageInfos.isNotEmpty()) {
                    ImageManager.with(holder.news_img).load(imageInfos[0].urls.firstOrNull())
                } else {
                    ImageManager.with(holder.news_img).load((mBean as Image).info.urls.firstOrNull())
                }
            }
            is GIF -> {
                holder.duration_txt.visibility = View.GONE
                ImageManager.with(holder.news_img).load((mBean as GIF).coverImage)
            }
        }
        holder.news_title_txt?.setTitle(mBean.title)
        holder.source_txt?.text = mBean.sourceName
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsRightImgViewHolder(view, adapter) {
        val duration_txt = itemView.duration_txt
        val source_txt = itemView.news_source_txt
    }

}