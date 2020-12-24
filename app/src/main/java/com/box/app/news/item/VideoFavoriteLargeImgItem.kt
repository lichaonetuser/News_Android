package com.box.app.news.item

import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Video
import com.box.app.news.item.base.BaseVideoItem
import com.box.app.news.item.base.holder.BaseNewsLargeImgViewHolder
import com.box.app.news.util.TimeUtils
import com.box.common.core.CoreApp
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_large_img.view.*
import org.jetbrains.anko.dip

class VideoFavoriteLargeImgItem(mBean: Video) : BaseVideoItem<VideoFavoriteLargeImgItem.ViewHolder>(mBean) {

    private val mDurationText by lazy {
        val duration = mBean.durationInterval * 1000
        TimeUtils.getVideoDurationText(duration)
    }

    override fun getLayoutRes(): Int = R.layout.item_video_favorite_large_img

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (isFavoriteStyle) {
//            holder.playBtn.visibility = View.VISIBLE
            holder.news_title_txt?.setTitleVisibility(View.GONE)

            val dp10 = CoreApp.getInstance().dip(10)
            holder.news_item_layout?.setPadding(dp10,0, dp10, 0)

            val params = holder.news_emit_time_txt?.layoutParams as ConstraintLayout.LayoutParams
            params.leftToRight = -1
            params.rightToRight = 0
            holder.news_img.aspectRatio = 2.5F
            ImageManager.with(holder.news_img).setAspectRatio(2.5F).load(mBean.coverImage)
        } else {
//            holder.playBtn.visibility = View.VISIBLE
            holder.news_title_txt?.setTitleVisibility(View.VISIBLE)
            holder.news_title_txt?.setTitle(mBean.title)
//            val height = holder.news_title_txt?.getHeightByExactWidth(EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(20))
//            val titleParams = holder.news_title_txt?.getLayoutParams()
//            titleParams?.width = EnvDisplayMetrics.WIDTH_PIXELS - CoreApp.getInstance().dip(20)
//            titleParams?.height = height
            ImageManager.with(holder.news_img).load(mBean.coverImage)
        }
        holder.durationTxt.text = mDurationText
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @Suppress("HasPlatformType")
    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsLargeImgViewHolder(view, adapter) {

        val playBtn = itemView.play_btn
        val durationTxt = itemView.duration_txt

        init {
            playBtn.visibility = View.VISIBLE
            durationTxt.visibility = View.VISIBLE
        }
    }
}