package com.box.app.news.item

import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Video
import com.box.app.news.data.DataManager
import com.box.app.news.item.base.BaseVideoItem
import com.box.app.news.item.base.holder.BaseNewsRightImgViewHolder
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_right_img.view.*

open class VideoRightImgItem(mBean: Video) : BaseVideoItem<VideoRightImgItem.ViewHolder>(mBean) {

    private val mDurationText by lazy {
        val duration = mBean.durationInterval * 1000
        TimeUtils.getVideoDurationText(duration)
    }

    override fun getLayoutRes(): Int {
        val enableNotInterested = DataManager.Memory.getAppConfig().enableNotInterested
        return if (enableNotInterested) {
            R.layout.item_news_right_img
        } else {
            R.layout.item_news_right_img_no_remove
        }
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        ImageManager.with(holder.news_img).load(mBean.coverImage)
        holder.durationTxt.text = mDurationText

        val enableNotInterested = DataManager.Memory.getAppConfig().enableNotInterested
        if (enableNotInterested) {
            holder.checkLineMax()
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @Suppress("HasPlatformType")
    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsRightImgViewHolder(view, adapter) {
        val durationTxt = itemView.duration_txt

        init {
            durationTxt.visibility = View.VISIBLE
        }
    }
}