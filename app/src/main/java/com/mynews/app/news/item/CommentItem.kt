package com.mynews.app.news.item

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.bean.Comment
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.item.payload.CommentPayload
import com.mynews.app.news.util.TimeUtils
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.IHeader
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentItem(mBean: Comment)
    : BaseItem<Comment, CommentItem.ViewHolder>(mBean), IHeader<CommentItem.ViewHolder> {

    var isMyCommentStyle = false

    override fun getLayoutRes(): Int {
        return R.layout.item_comment
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        if (payloads?.contains(CommentPayload.DIG) == true) {
            holder.dig_btn.isActivated = mBean.isDigged
            holder.dig_btn.text = mBean.digCount.toString()
            return
        }

        holder.comment_txt.text = mBean.content
        holder.comment_time_txt.text = TimeUtils.getDisplayTimeString(mBean.ctime)
        holder.dig_btn.text = mBean.digCount.toString()
        holder.dig_btn.isActivated = mBean.isDigged

        holder.comment_txt.updateFontSize()
        holder.reply_txt.updateFontSize()

        if (mBean.user == null) {
            if (mBean.anonymous) {
                holder.user_name.text = ResUtils.getString(R.string.Common_AnonymousUser)
                ImageManager.with(holder.user_img).load(R.drawable.user_headshot_img)
            } else {
                holder.user_name.text = mBean.screenName
                ImageManager.with(holder.user_img).load(mBean.avatarUrl)
            }
        } else {
            if (mBean.anonymous) {
                holder.user_name.text = ResUtils.getString(R.string.Comment_AnonymousMe)
                ImageManager.with(holder.user_img).load(R.drawable.user_headshot_img)
            } else {
                holder.user_name.text = mBean.user!!.screenName
                ImageManager.with(holder.user_img).load(mBean.user!!.avatarUrl)
            }
        }

        if (mBean.reply != null && mBean.reply?.content?.isNotBlank() == true) {
            val reply = mBean.reply!!
            holder.reply_txt.visibility = View.VISIBLE
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
                } else if ((!reply.anonymous) && reply.screenName.isNotBlank()) {
                    holder.reply_txt.text = "${reply.screenName}:${reply.content}"
                } else {
                    holder.reply_txt.text = "${ResUtils.getString(R.string.Common_AnonymousUser)}:${reply.content}"
                }
            }
            val spanString = SpannableString(holder.reply_txt.text)
            val span = ForegroundColorSpan(ResUtils.getColor(R.color.color_4))
            spanString.setSpan(span, 0, holder.reply_txt.text.indexOf(":") + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            holder.reply_txt.text = spanString
        } else {
            holder.reply_txt.visibility = View.GONE
        }

        if (!isMyCommentStyle) {
            holder.news_layout.visibility = View.GONE
            return
        }

        holder.reply_txt.visibility = View.GONE
        holder.news_layout.visibility = View.VISIBLE
        holder.new_img.visibility = View.GONE

        when (mBean.deleteContent) {
            DataDictionary.DeleteContentType.EXPIRED.value -> {
                holder.new_title.text = ResUtils.getString(R.string.Comment_OverdueContent)
            }
            DataDictionary.DeleteContentType.NO_COPYRIGHT.value -> {
                holder.new_title.text = ResUtils.getString(R.string.Comment_InvalidContent)
            }
            else -> {
                when {
                    mBean.article?.listImageUrls?.isNotEmpty() == true -> {
                        holder.new_img.visibility = View.VISIBLE
                        ImageManager.with(holder.new_img).load(mBean.article!!.listImageUrls[0])
                    }
                    mBean.image?.info?.urls?.isNotEmpty() == true -> {
                        holder.new_img.visibility = View.VISIBLE
                        ImageManager.with(holder.new_img).load(mBean.image!!.info.urls[0])
                    }
                    mBean.image?.images?.isNotEmpty() == true -> {
                        val imageInfos = mBean.image?.images
                        if (imageInfos != null && imageInfos.isNotEmpty()) {
                            holder.new_img.visibility = View.VISIBLE
                            ImageManager.with(holder.new_img).load(imageInfos[0].urls.firstOrNull())
                        }
                    }
                    mBean.video?.coverImage?.isNotEmpty() == true -> {
                        holder.new_img.visibility = View.VISIBLE
                        ImageManager.with(holder.new_img).load(mBean.video!!.coverImage)
                    }
                    mBean.gif?.coverImage?.isNotEmpty() == true -> {
                        holder.new_img.visibility = View.VISIBLE
                        ImageManager.with(holder.new_img).load(mBean.gif!!.coverImage)
                    }
                }
                when {
                    mBean.article?.title != null -> holder.new_title.text = mBean.article?.title
                    mBean.video?.title != null -> holder.new_title.text = mBean.video?.title
                    mBean.image?.title != null -> holder.new_title.text = mBean.image?.title
                    mBean.gif?.title != null -> holder.new_title.text = mBean.gif?.title
                    mBean.essay?.title != null -> holder.new_title.text = mBean.essay?.text
                }
            }
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val user_img = itemView.user_img
        val user_name = itemView.user_name
        val more_btn = itemView.more_btn
        val comment_txt = itemView.comment_txt
        val reply_txt = itemView.reply_txt
        val comment_time_txt = itemView.comment_time_txt
        val dig_btn = itemView.dig_btn
        val new_img = itemView.news_img
        val new_title = itemView.news_title
        val news_layout = itemView.news_layout

        init {
            bindItemChildViewClick(more_btn)
            bindItemChildViewClick(dig_btn)
            bindItemChildViewClick(news_layout)
        }

        @SuppressLint("MissingSuperCall")
        override fun onLongClick(view: View?): Boolean {
            return false
        }

    }

}