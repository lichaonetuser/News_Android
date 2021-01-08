package com.mynews.app.news.item.base

import android.animation.Animator
import android.view.View
import com.mynews.app.news.bean.Feedback
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.extension.format2DateString
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import kotlinx.android.synthetic.main.item_feedback_service.view.*

abstract class BaseFeedbackItem<VH : BaseFeedbackItem.ViewHolder>(mBean: Feedback)
    : BaseItem<Feedback, VH>(mBean) {

    private val timeString = (mBean.ctime * 1000).format2DateString("yyyy-MM-dd HH:mm:ss")

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        holder.time_txt.text = timeString
        holder.content_txt.text = mBean.content
        ImageManager.with(holder.avatar_img).load(mBean.avatarUrl)
        val imgs = mBean.image.urls
        if (imgs.isNotEmpty()) {
            holder.content_img.visibility = View.VISIBLE
            ImageManager.with(holder.content_img).load(imgs[0])
        } else {
            holder.content_img.visibility = View.GONE
        }
    }

    open class ViewHolder(view: View, adapter: CommonRecyclerAdapter)
        : BaseViewHolder(view, adapter) {

        val time_txt = itemView.time_txt
        val avatar_img = itemView.avatar_img
        val content_txt = itemView.content_txt
        val content_img = itemView.content_img

        init {
            bindItemChildViewClick(content_img)
        }

        override fun scrollAnimators(animators: MutableList<Animator>, position: Int, isForward: Boolean) {
            if (isForward) {
                AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
            } else {
                AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }

    }

}