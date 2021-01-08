package com.mynews.app.news.item.world

import android.annotation.SuppressLint
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.bean.Comment
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter

class WorldCupBoardCommentItem(mBean: Comment) : BaseWorldCupBoardItem<WorldCupBoardCommentItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_worldcup_board_child_comment
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: WorldCupBoardCommentItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        //回复处理
        if (mBean.reply != null && mBean.reply?.content?.isNotBlank() == true) {
            val reply = mBean.reply!!
            holder.reply_txt.visibility = View.VISIBLE
            //回复用户匿名处理
            if (reply.user == null) {
                if (reply.screenName.isBlank() || reply.anonymous) {
                    holder.reply_txt.text = "${ResUtils.getString(R.string.Common_AnonymousUser)}:${reply.content}"
                } else {
                    holder.reply_txt.text = "${reply.screenName}:${reply.content}"
                }
            } else {
                val user = reply.user!!
                if (reply.anonymous && user.uid.isNotBlank() && user.uid == AccountManager.account?.uid) {
                    holder.reply_txt.text = "${ResUtils.getString(R.string.Comment_AnonymousMe)}:${reply.content}"
                } else if (user.screenName.isNotBlank()) {
                    holder.reply_txt.text = "${user.screenName}:${reply.content}"
                } else if (reply.screenName.isNotBlank()) {
                    holder.reply_txt.text = "${reply.screenName}:${reply.content}"
                } else {
                    holder.reply_txt.text = "${ResUtils.getString(R.string.Common_AnonymousUser)}:${reply.content}"
                }
            }
        } else {
            holder.reply_txt.visibility = View.GONE
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): WorldCupBoardCommentItem.ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseWorldCupBoardItem.ViewHolder(view, adapter)

}