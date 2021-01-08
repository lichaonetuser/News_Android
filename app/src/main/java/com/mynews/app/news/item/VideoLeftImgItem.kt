package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.Video
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.item.base.BaseVideoItem
import com.mynews.app.news.item.base.holder.BaseNewsLeftImgViewHolder
import com.mynews.app.news.util.TimeUtils
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_news_left_img.view.*

class VideoLeftImgItem(mBean: Video) : BaseVideoItem<VideoLeftImgItem.ViewHolder>(mBean) {

    private val mDurationText by lazy {
        val duration = mBean.durationInterval * 1000
        TimeUtils.getVideoDurationText(duration)
    }

    override fun getLayoutRes(): Int {
        val enableNotInterested = DataManager.Memory.getAppConfig().enableNotInterested
        return if (enableNotInterested) {
            R.layout.item_news_left_img
        } else {
            R.layout.item_news_left_img_no_remove
        }
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        ImageManager.with(holder.news_img).load(mBean.coverImage)
        holder.duration_txt.text = mDurationText
        holder.checkLineMax()
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsLeftImgViewHolder(view, adapter) {
        val duration_txt = itemView.duration_txt

        init {
            duration_txt.visibility = View.VISIBLE
        }
    }

}