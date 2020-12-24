package com.box.app.news.item.world

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt;
import android.text.Spannable
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.bean.Comment
import com.box.app.news.item.payload.CommentPayload
import com.box.app.news.util.TimeUtils
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.items.ISectionable
import eu.davidea.flexibleadapter.utils.FlexibleUtils
import kotlinx.android.synthetic.main.item_worldcup_board_include_comment.view.*
import lt.neworld.spanner.Spanner
import lt.neworld.spanner.Spans.foreground
import org.jetbrains.anko.findOptional
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class BaseWorldCupBoardItem<VH : BaseWorldCupBoardItem.ViewHolder>(mBean: Comment)
    : BaseItem<Comment, VH>(mBean), ISectionable<VH, WorldCupBoardHeaderItem> {

    private var mHeader: WorldCupBoardHeaderItem? = null

    override fun getHeader(): WorldCupBoardHeaderItem? {
        return mHeader
    }

    override fun setHeader(header: WorldCupBoardHeaderItem?) {
        this.mHeader = header
    }


    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        //点赞刷新
        if (payloads?.contains(CommentPayload.DIG) == true) {
            holder.dig_btn.isActivated = mBean.isDigged
            holder.dig_btn.text = mBean.digCount.toString()
            return
        }

        //文本
        holder.comment_txt.text = mBean.content
        holder.comment_time_txt.text = TimeUtils.getDisplayTimeString(mBean.ctime)
        holder.dig_btn.text = mBean.digCount.toString()
        holder.dig_btn.isActivated = mBean.isDigged

        //字体大小
        holder.comment_txt.updateFontSize()
        holder.reply_txt.updateFontSize()

        //高亮Url
        val pattern = Pattern.compile("((http|https)://)(([a-zA-Z0-9._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9&%_./-~-]*)?", Pattern.CASE_INSENSITIVE)
        val match = pattern.matcher(mBean.content)
        if (match.find()) {
            holder.comment_txt.text = Spanner(mBean.content).span(match.group(), foreground(Color.BLUE))
        } else {
            holder.comment_txt.text = mBean.content
        }

        //用户匿名处理
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
    }

    abstract class ViewHolder(view: View, mCommonAdapter: CommonRecyclerAdapter) : BaseViewHolder(view, mCommonAdapter) {
        val user_img = itemView.user_img
        val user_name = itemView.user_name
        val comment_txt = itemView.comment_txt
        val reply_txt = itemView.reply_txt
        val comment_time_txt = itemView.comment_time_txt
        val dig_btn = itemView.dig_btn
        val content_layout = itemView.findOptional<View>(R.id.content_layout)

        init {
            bindItemChildViewClick(dig_btn)
            bindItemChildViewClick(content_layout)
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as BaseWorldCupBoardItem<*>

        if (mHeader != other.mHeader) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (mHeader?.hashCode() ?: 0)
        return result
    }

}