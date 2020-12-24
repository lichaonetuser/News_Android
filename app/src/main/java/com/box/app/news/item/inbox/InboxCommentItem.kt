package com.box.app.news.item.inbox

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.Comment
import com.box.app.news.bean.InboxMessage
import com.box.app.news.data.DataDictionary
import com.box.common.core.CoreApp
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_inbox_comment.view.*
import org.jetbrains.anko.dip

class InboxCommentItem(mBean: InboxMessage)
    : BaseInboxItem<InboxCommentItem.ViewHolder>(mBean) {

    companion object {
        val DP_8 = CoreApp.getInstance().dip(8)
        val DP_10 = CoreApp.getInstance().dip(10)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_inbox_comment
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        val comment = mBean.item as? Comment
        if (mBean.type == DataDictionary.InboxMessageType.CUSTOM.intValue) {
            // DataDictionary.InboxMessageType.CUSTOM类型
            holder.comment_txt.text = mBean.description
            holder.msg_description_txt.text = ""
            ImageManager.with(holder.msg_cover_img).load(mBean.iconUrl)
        } else {
            //COMMENT类型
            val headerStr = ResUtils.getString(R.string.Setting_MyComment) + ":"
            val commentStr = comment?.reply?.content ?: comment?.content
            val wholeStr = headerStr + commentStr

            val headerSpan = ForegroundColorSpan(ResUtils.getColor(R.color.color_44))
            val commentSpan = ForegroundColorSpan(ResUtils.getColor(R.color.color_1))

            val spannableString = SpannableString(wholeStr)
            spannableString.setSpan(headerSpan, 0, headerStr.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(commentSpan, headerStr.length, wholeStr.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)

            if (comment?.reply == null) {
                holder.comment_txt.text = spannableString
                holder.comment_txt.setBackgroundColor(ResUtils.getColor(R.color.color_8))
                holder.comment_txt.setPadding(DP_10, DP_8, DP_10, DP_8)
                holder.reply_txt.visibility = View.GONE
                ImageManager.with(holder.msg_cover_img).load(R.drawable.me_board_good)
            } else {
                holder.comment_txt.text = comment?.content
                holder.comment_txt.setBackgroundColor(ResUtils.getColor(R.color.transparent))
                holder.comment_txt.setPadding(0, 0, 0, 0)
                holder.reply_txt.text = spannableString
                holder.reply_txt.visibility = View.VISIBLE
                ImageManager.with(holder.msg_cover_img).load(R.drawable.me_board_comment)
            }
        }
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseInboxItem.ViewHolder(view, mCommonAdapter) {
        val comment_txt = itemView.comment_txt
        val reply_txt = itemView.reply_txt
    }

}