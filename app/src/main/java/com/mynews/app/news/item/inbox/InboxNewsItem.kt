package com.mynews.app.news.item.inbox

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_inbox_news.view.*

class InboxNewsItem(mBean: InboxMessage)
    : BaseInboxItem<InboxNewsItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_inbox_news
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        ImageManager.with(holder.msg_cover_img).load(R.drawable.me_board_push)
        val news = mBean.item as? BaseNewsBean
        holder.news_title_txt.text = news?.title ?: ""
        when (news) {
            is Article -> {
                if (news.listImageUrls.isEmpty()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.listImageUrls.firstOrNull())
                }
                holder.news_video_ic_img.visibility = View.GONE
            }
            is Image -> {
                if (news.info.urls.isEmpty()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.info.urls.firstOrNull())
                }
                holder.news_video_ic_img.visibility = View.GONE
            }
            is Video -> {
                if (news.coverImage.isBlank()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.coverImage)
                }
                holder.news_video_ic_img.visibility = View.VISIBLE
            }
            is GIF -> {
                if (news.coverImage.isBlank()) {
                    holder.news_cover_img.visibility = View.GONE
                } else {
                    holder.news_cover_img.visibility = View.VISIBLE
                    ImageManager.with(holder.news_cover_img).load(news.coverImage)
                }
                holder.news_video_ic_img.visibility = View.VISIBLE
            }
            else -> {
                holder.news_video_ic_img.visibility = View.GONE
            }
        }
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseInboxItem.ViewHolder(view, mCommonAdapter) {
        val news_title_txt = itemView.news_title_txt
        val news_cover_img = itemView.news_cover_img
        val news_video_ic_img = itemView.news_video_ic_img
    }

}