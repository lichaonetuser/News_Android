package com.box.app.news.item.base

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.Gravity
import android.view.View
import com.box.app.news.R
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataManager
import com.box.app.news.item.payload.NewsPayload
import com.box.app.news.util.TimeUtils
import com.box.app.news.widget.TagView
import com.box.common.core.CoreApp
import com.box.common.core.anim.AnimatorProperty
import com.box.common.core.log.Logger
import com.box.common.core.util.ResUtils
import com.box.common.core.widget.CoreImageView
import com.box.common.core.widget.CoreTextView
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import kotlinx.android.synthetic.main.item_news_large_img.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.findOptional

abstract class BaseNewsItem<BEAN : BaseNewsBean, VH : BaseNewsItem.ViewHolder>(mBean: BEAN)
    : BaseItem<BEAN, VH>(mBean) {

    companion object {
        const val ONE_MIN_MILLIS = 60 * 1000
        const val ONE_HOUR_MILLIS = 60 * ONE_MIN_MILLIS
        const val ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS
        val dp46 = CoreApp.getInstance().dip(46).toFloat()
    }

    open var isFavoriteStyle = false

    protected open fun getDisplayTime(): Long {
        return when {
            isFavoriteStyle -> mBean.favoriteTime
            mBean.headline -> mBean.emitTime
            else -> mBean.displayTime
        }
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: VH, position: Int, payloads: MutableList<Any?>?) {
        if (payloads?.contains(NewsPayload.UPDATE_READ) == true) {
            if (mBean.isRead) {
                holder.news_title_txt?.setTitleColor(ResUtils.getColor(R.color.color_3))
            } else {
                holder.news_title_txt?.setTitleColor(ResUtils.getColor(R.color.color_1))
            }
            return
        }
        if (payloads?.contains(NewsPayload.UPDATE_EDIT) == true) {
            holder.playToggleEditingAnim()
            return
        }

        holder.news_title_txt?.updateTitleTextSize()
        holder.news_title_txt?.setTitle(mBean.title)
        holder.news_source_txt?.text = mBean.sourceName
        holder.news_emit_time_txt?.text = TimeUtils.getDisplayTimeString(getDisplayTime())
        Log.d("mytime", holder.news_emit_time_txt?.text.toString())
        Log.d("mytime", holder.news_source_txt?.text.toString())
        if (mBean.isRead) {
            holder.news_title_txt?.setTitleColor(ResUtils.getColor(R.color.color_3))
        } else {
            holder.news_title_txt?.setTitleColor(ResUtils.getColor(R.color.color_1))
        }
        holder.select_btn?.isSelected = adapter.isSelected(position)
        if (adapter.isEditing()) {
            holder.select_btn?.visibility = View.VISIBLE
            holder.select_btn?.translationX = 0f
            holder.select_btn?.alpha = 1f
            holder.news_item_layout?.translationX = dp46
        } else {
            holder.select_btn?.visibility = View.GONE
            holder.select_btn?.translationX = -dp46
            holder.select_btn?.alpha = 0f
            holder.news_item_layout?.translationX = 0f
        }

//        if (mBean.showTimerTitle) {
//
//            holder.time_header_layout?.visibility = View.VISIBLE
//            holder.time_txt?.text = TimeUtils.getHeadlineTimeString(mBean.emitTime)
//            holder.news_emit_time_txt?.visibility = View.INVISIBLE
//            holder.news_source_txt?.visibility = View.VISIBLE
//        } else {
//            holder.time_header_layout?.visibility = View.GONE
//            holder.news_emit_time_txt?.visibility = View.VISIBLE
//        }


        if (mBean.showTimerTitle && !mBean.headline) {
            holder.time_header_layout?.visibility = View.VISIBLE
            holder.time_txt?.visibility = View.VISIBLE
            holder.time_txt?.text = TimeUtils.getHeadlineTimeString(mBean.emitTime)
            holder.news_emit_time_txt?.text=""
            holder.news_emit_time_txt?.visibility = View.GONE
            holder.news_source_txt?.visibility = View.VISIBLE
            return
        }


        holder.time_txt?.visibility = View.GONE
        holder.time_header_layout?.visibility = View.GONE


        if (mBean.headline) {
            holder.news_source_txt?.visibility = View.VISIBLE
            holder.news_emit_time_txt?.visibility = View.INVISIBLE
            holder.news_source_txt?.text = TimeUtils.getDisplayTimeString(getDisplayTime())
        } else {
            holder.news_source_txt?.text = mBean.sourceName
            holder.news_source_txt?.visibility = View.VISIBLE
            holder.news_emit_time_txt?.visibility = View.VISIBLE
            holder.news_emit_time_txt?.text = TimeUtils.getDisplayTimeString(getDisplayTime())
        }
    }

    open class ViewHolder(view: View, adapter: CommonRecyclerAdapter)
        : BaseViewHolder(view, adapter) {

        val temp:View? = itemView.findOptional(R.id.news_title_txt)
        val news_title_txt = temp as TitleInterface?
        val news_source_txt = itemView.findOptional<CoreTextView>(R.id.news_source_txt)
        val news_emit_time_txt = itemView.findOptional<CoreTextView>(R.id.news_emit_time_txt)
        val remove_btn = itemView.findOptional<CoreImageView>(R.id.remove_btn)
        val space = itemView.findOptional<View>(R.id.space)
        val root_layout = itemView.root_layout
        val news_item_layout = itemView.findOptional<View>(R.id.news_item_layout)
        val select_btn = itemView.findOptional<View>(R.id.select_btn)
        val time_header_layout = itemView.findOptional<View>(R.id.time_header_layout)
        val time_txt = itemView.findOptional<CoreTextView>(R.id.time_txt)
        val newsTag = itemView.findOptional<TagView>(R.id.news_tags)
        val commentCount = itemView.findOptional<CoreTextView>(R.id.news_comment_count)

        var mIsVisible = false
        var mAttachedListener: ((isAttached: Boolean) -> Unit)? = null

        companion object {
            val dp14 = CoreApp.getInstance().dip(14)
        }

        init {
            if (DataManager.Memory.getAppConfig().enableNotInterested) {
                bindItemChildViewClick(remove_btn)
                remove_btn?.visibility = View.VISIBLE
                news_emit_time_txt?.gravity = Gravity.TOP or Gravity.START
                val params = space?.layoutParams
                params?.width = dp14
                space?.layoutParams = params
            } else {
                remove_btn?.visibility = View.GONE
                news_emit_time_txt?.gravity = Gravity.TOP or Gravity.END
                val params = space?.layoutParams
                params?.width = 0
                space?.layoutParams = params
            }
        }

        fun bindItemAttachedListener(listener: ((isAttached: Boolean) -> Unit)?) {
            this.mAttachedListener = listener
        }

        override fun onViewAttachedToWindow() {
            super.onViewAttachedToWindow()
            mAttachedListener?.invoke(true)
            Logger.tag("ON_VIEW_ATTACHED_TO_WINDOW").d("onViewAttachedToWindow\n${news_title_txt?.getTitle()}")
        }

        override fun onViewDetachedFromWindow() {
            super.onViewDetachedFromWindow()
            mAttachedListener?.invoke(false)
            Logger.tag("ON_VIEW_DETACHED_FROM_WINDOW").d("onViewDetachedFromWindow\n${news_title_txt?.getTitle()}")
        }

        override fun scrollAnimators(animators: MutableList<Animator>, position: Int, isForward: Boolean) {
            if (isForward) {
                AnimatorHelper.slideInFromBottomAnimator(animators, itemView, mAdapter.recyclerView)
            } else {
                AnimatorHelper.slideInFromTopAnimator(animators, itemView, mAdapter.recyclerView)
            }
        }

        override fun toggleActivation() {
            super.toggleActivation()
            select_btn?.isSelected = mAdapter.isSelected(flexibleAdapterPosition)
        }

        private var mAnimSet: AnimatorSet? = null

        fun playToggleEditingAnim() {
            mAnimSet?.end()
            mAnimSet = AnimatorSet()
            if (mCommonAdapter.isEditing()) {
                val layoutTranslationAnim = ObjectAnimator.ofFloat(news_item_layout, AnimatorProperty.TRANSLATION_X, 0f, dp46)
                val selectTranslationAnim = ObjectAnimator.ofFloat(select_btn, AnimatorProperty.TRANSLATION_X, -dp46, 0f)
                val selectAlphaAnim = ObjectAnimator.ofFloat(select_btn, AnimatorProperty.ALPHA, 0f, 1f)
                mAnimSet?.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        news_item_layout?.translationX = dp46
                        select_btn?.visibility = View.VISIBLE
                        select_btn?.translationX = 0f
                        select_btn?.alpha = 1f
                    }

                    override fun onAnimationCancel(animation: Animator?) {

                    }

                    override fun onAnimationStart(animation: Animator?) {
                        select_btn?.visibility = View.VISIBLE
                    }
                })
                mAnimSet?.duration = 200
                mAnimSet?.playTogether(layoutTranslationAnim, selectTranslationAnim, selectAlphaAnim)
                mAnimSet?.start()
            } else {
                val layoutTranslationAnim = ObjectAnimator.ofFloat(news_item_layout, AnimatorProperty.TRANSLATION_X, dp46, 0f)
                val selectTranslationAnim = ObjectAnimator.ofFloat(select_btn, AnimatorProperty.TRANSLATION_X, 0f, -dp46)
                val selectAlphaAnim = ObjectAnimator.ofFloat(select_btn, AnimatorProperty.ALPHA, 1f, 0f)
                mAnimSet?.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {}

                    override fun onAnimationEnd(animation: Animator?) {
                        select_btn?.visibility = View.GONE
                        select_btn?.translationX = -dp46
                        select_btn?.alpha = 0f
                        news_item_layout?.translationX = 0f
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                    }

                    override fun onAnimationStart(animation: Animator?) {
                    }
                })
                mAnimSet?.duration = 200
                mAnimSet?.playTogether(layoutTranslationAnim, selectTranslationAnim, selectAlphaAnim)
                mAnimSet?.start()
            }
        }

    }

}