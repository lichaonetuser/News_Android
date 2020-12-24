package com.box.app.news.item

import android.os.Build
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Video
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_worldcup_video_view.view.*

class WorldCupFeaturedVideoItem(mBean: Video)
    : BaseItem<Video, WorldCupFeaturedVideoItem.ViewHolder>(mBean), IHeader<WorldCupFeaturedVideoItem.ViewHolder> {

    private val mDurationText by lazy {
        val duration = mBean.durationInterval * 1000
        return@lazy TimeUtils.getVideoDurationText(duration)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_video_view
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        holder.worldcup_video_title.text = mBean.title
        if (mBean.tags.getOrNull(0)?.name.isNullOrBlank()) {
            holder.video_tag.visibility = View.GONE
            holder.video_line.visibility = View.GONE
        } else {
            holder.video_tag.visibility = View.VISIBLE
            holder.video_tag.text = mBean.tags[0].name
            holder.video_line.visibility = View.VISIBLE
        }

        holder.video_duration.visibility = View.VISIBLE
        holder.video_duration.text = mDurationText
        holder.video_source.text = mBean.sourceName

        ImageManager.with(holder.worldcup_video_image).load(mBean.coverImage)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            holder.worldcup_video_view.background = ResUtils.getDrawable(R.drawable.shadow_layer)
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {

        val worldcup_video_view = itemView.worldcup_video_view
        val worldcup_video_image = itemView.worldcup_video_image
        val worldcup_video_title = itemView.worldcup_video_title
        val video_tag = itemView.video_tag
        val video_source = itemView.video_source
        val video_line = itemView.line
        val video_duration = itemView.duration_txt

    }


}