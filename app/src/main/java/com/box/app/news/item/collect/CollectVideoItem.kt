package com.box.app.news.item.collect

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import com.box.app.news.R
import com.box.app.news.bean.Video
import com.box.app.news.item.base.BaseNewsItem
import com.box.app.news.item.base.BaseVideoItem
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_collect_video.view.*

/**
 * 收藏视频
 */
class CollectVideoItem(mBean: Video)
    : BaseVideoItem<CollectVideoItem.ViewHolder>(mBean) {

    override var isFavoriteStyle = true

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.bindInfo(mBean)
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): CollectVideoItem.ViewHolder {
        return CollectVideoItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes() = R.layout.item_news_collect_video


    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

        private val newsCover = itemView.news_img
        private val durationTextView = itemView.duration_txt
        private val sourceParams = itemView.news_source_txt.layoutParams as ConstraintLayout.LayoutParams


        fun bindInfo(mBean: Video) {
            ImageManager.with(newsCover).load(mBean.coverImage)
            durationTextView.text = durationText(mBean.durationInterval)

            sourceParams.topToBottom = if (mBean.title.isEmpty()) R.id.news_img else R.id.news_title_txt
        }

        private fun durationText(time: Long):String = TimeUtils.getVideoDurationText(time * 1000)
    }
}