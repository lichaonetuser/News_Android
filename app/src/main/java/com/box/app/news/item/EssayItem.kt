package com.box.app.news.item

import com.box.app.news.bean.Essay

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.box.app.news.R
import com.box.app.news.item.base.BaseEssayItem
import com.box.app.news.item.base.holder.BaseEssayViewHolder
import com.box.common.core.image.fresco.ImageManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_essay.view.*

/**
 * 段子的cell
 * 7.1新需求
 */
class EssayItem(mBean: Essay) : BaseEssayItem<EssayItem.ViewHolder>(mBean) {

    override fun getLayoutRes() = R.layout.item_essay

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        holder.essayTextView.setText(mBean.text)
        ImageManager.with(holder.userImg).load(mBean.sourcePic)
        holder.userName.text = mBean.sourceName
        holder.digBtn.text = mBean.digCount.toString()
        holder.buryBtn.text = mBean.buryCount.toString()
        holder.commentTxt.text = mBean.commentCount.toString()
        holder.buryBtn.isSelected = mBean.isBuried
        holder.digBtn.isSelected = mBean.isDigged

        holder.essayTextView.updateTextSize()
        holder.essayTextView.setIsExpand(mBean.isExpanded)
        holder.essayTextView.setOnExpandListener { mBean.isExpanded = true }

        //如果是上次阅读到上面一个段子，则去掉分割线
        if (position == adapter.postion0 || position == adapter.postion1) {
            holder.decoration.visibility = GONE
        } else {
            holder.decoration.visibility = VISIBLE
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @Suppress("HasPlatformType", "MemberVisibilityCanPrivate")
    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseEssayViewHolder(view, adapter) {
        val essayTextView = view.news_essay_text
        val userName = itemView.user_name_txt
        val commentTxt = itemView.comment_txt
        val digBtn = itemView.dig_btn
        val buryBtn = itemView.bury_btn
        val moreBtn = itemView.more_btn
        val userImg = itemView.user_img
        val decoration = itemView.custom_decoration

        init {
            bindItemChildViewClick(digBtn)
            bindItemChildViewClick(buryBtn)
            bindItemChildViewClick(moreBtn)

            essayTextView.setMaxLines(10)
            essayTextView.setCanExpand(true)
        }
    }
}