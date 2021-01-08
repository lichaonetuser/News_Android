package com.mynews.app.news.item.inbox

import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.util.TimeUtils
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.item.BaseItem
import kotlinx.android.synthetic.main.item_inbox_multi.view.*

class InboxMultiItem(mBean: InboxMessage) : BaseItem<InboxMessage, InboxMultiItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_inbox_multi
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
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
//        var str = ""
//        mBean.items?.forEach {
//            val news = it as? BaseNewsBean
//            val title = news?.title
//            str += "◆ $title\n\n"
//        }
//        holder.news_title_txt.text = str
        mBean.items?.run {
            if (size > 0) {
                val news = this[0] as? BaseNewsBean
                holder.news_title_txt.text = news?.title
                holder.notice_title_layout.visibility = View.VISIBLE
            }
            if (size > 1) {
                val news = this[1] as? BaseNewsBean
                holder.news_title_txt_1.text = news?.title
                holder.notice_title_1_layout.visibility = View.VISIBLE
            } else {
                holder.notice_title_1_layout.visibility = View.GONE
            }
            if (size > 2) {
                val news = this[2] as? BaseNewsBean
                holder.news_title_txt_2.text = news?.title
                holder.notice_title_2_layout.visibility = View.VISIBLE
            } else {
                holder.notice_title_2_layout.visibility = View.GONE
            }
            if (size > 3) {
                val news = this[3] as? BaseNewsBean
                holder.news_title_txt_3.text = news?.title
                holder.notice_title_3_layout.visibility = View.VISIBLE
            } else {
                holder.notice_title_3_layout.visibility = View.GONE
            }
            if (size > 4) {
                val news = this[4] as? BaseNewsBean
                holder.news_title_txt_4.text = news?.title
                holder.notice_title_4_layout.visibility = View.VISIBLE
            } else {
                holder.notice_title_4_layout.visibility = View.GONE
            }
        }
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseInboxItem.ViewHolder(view, mCommonAdapter) {
        val news_title_txt = itemView.notice_title
        val news_title_txt_1 = itemView.notice_title_1
        val news_title_txt_2 = itemView.notice_title_2
        val news_title_txt_3 = itemView.notice_title_3
        val news_title_txt_4 = itemView.notice_title_4
        val notice_title_layout = itemView.notice_title_layout
        val notice_title_1_layout = itemView.notice_title_1_layout
        val notice_title_2_layout = itemView.notice_title_2_layout
        val notice_title_3_layout = itemView.notice_title_3_layout
        val notice_title_4_layout = itemView.notice_title_4_layout
        val news_time_txt = itemView.notice_time_txt
        val news_more_txt = itemView.notice_more_txt
        val news_time_ic = itemView.notice_time_ic
        val news_time_bottom = itemView.notice_time_bottom
    }

}