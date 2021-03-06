package com.mynews.app.news.item.inbox

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.util.TimeUtils
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_inbox_right_img.view.*

class InboxRightImgItem(mBean: InboxMessage) : BaseItem<InboxMessage, InboxRightImgItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_inbox_right_img
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        val news = mBean.item as? BaseNewsBean
        holder.news_title_txt.text = news?.title ?: ""
        holder.news_source_txt.text = news?.sourceName ?: ""

        val time = mBean.displayTime?: 0
        if (TimeUtils.isSameDay(time, System.currentTimeMillis()).first) {  //当天
            holder.news_time_txt.text =  TimeUtils.getDisplayTimeString(time)
            holder.news_time_bottom.visibility = View.GONE
            holder.news_time_ic.visibility = View.VISIBLE
            holder.news_time_txt.visibility = View.VISIBLE
        } else { //非当天
            if (!adapter.flag) {
                mBean.needShowTimeHeader = true
                adapter.flag = true
            }
            if (mBean.needShowTimeHeader) {
                holder.news_time_ic.visibility = View.VISIBLE
                holder.news_time_txt.visibility = View.VISIBLE
                holder.news_time_txt.text = ResUtils.getString(R.string.Push_HistoryMessage)
            } else {
                holder.news_time_txt.visibility = View.GONE
                holder.news_time_ic.visibility = View.GONE
            }
            holder.news_time_bottom.visibility = View.VISIBLE
            holder.news_time_bottom.text = TimeUtils.getDisplayTimeString(time)
        }
        when (news) {
            is Article -> {
                if (news.listImageUrls.isEmpty()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.listImageUrls.firstOrNull())
                }
//                holder.news_video_ic_img.visibility = View.GONE
            }
            is Image -> {
                if (news.info.urls.isEmpty()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.info.urls.firstOrNull())
                }
//                holder.news_video_ic_img.visibility = View.GONE
            }
            is Video -> {
                if (news.coverImage.isBlank()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.coverImage)
                }
//                holder.news_video_ic_img.visibility = View.VISIBLE
            }
            is GIF -> {
                if (news.coverImage.isBlank()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.coverImage)
                }
//                holder.news_video_ic_img.visibility = View.VISIBLE
            }
            is Essay -> holder.news_cover_img.visibility = View.GONE
            else -> {
//                holder.news_video_ic_img.visibility = View.GONE
            }
        }
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseInboxItem.ViewHolder(view, mCommonAdapter) {
        val news_title_txt = itemView.notice_title
        val news_cover_img = itemView.notice_cover_img
        val news_time_txt = itemView.notice_time_txt
        val news_source_txt = itemView.notice_source_txt
        val news_time_bottom = itemView.notice_time_bottom
        val news_time_ic = itemView.notice_time_ic
    }

}