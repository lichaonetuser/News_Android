package com.mynews.app.news.item.inbox

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.bean.InboxMessage
import com.mynews.app.news.util.TimeUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_inbox_include_message.view.*

abstract class BaseInboxItem<VH : BaseInboxItem.ViewHolder>(mBean: InboxMessage)
    : BaseItem<InboxMessage, VH>(mBean) {

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        holder.msg_title_txt.text = mBean.title
        holder.msg_description_txt.text = mBean.description
        holder.msg_time_txt.text = if (mBean.displayTime != null) {
            TimeUtils.getDisplayTimeString(mBean.displayTime ?: 0, "MM-dd")
        } else {
            ""
        }
    }

    abstract class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val msg_cover_img = itemView.msg_cover_img
        val msg_title_txt = itemView.msg_title_txt
        val msg_description_txt = itemView.msg_description_txt
        val msg_time_txt = itemView.msg_time_txt
    }

}