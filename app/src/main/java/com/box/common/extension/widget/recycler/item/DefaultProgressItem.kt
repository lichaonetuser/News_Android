package com.box.common.extension.widget.recycler.item

import android.animation.Animator
import android.view.View

import com.box.app.news.R
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.helpers.AnimatorHelper
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.core_item_default_progress.view.*

class DefaultProgressItem(id: Any) : BaseItem<Any, DefaultProgressItem.ViewHolder>(id) {

    companion object {
        const val PAYLOADS_UPDATE_FOR_RETRY = 0
    }

    var isLoadMore = true

    override fun getLayoutRes(): Int {
        return R.layout.core_item_default_progress
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        if (isLoadMore) {
            holder.showProgress()
        } else {
            holder.showRetry()
        }

        mOnClickRetryBtnListener.holder = holder
        mOnClickRetryBtnListener.adapter = adapter
        holder.retry_btn.setOnClickListener(mOnClickRetryBtnListener)
    }

    private val mOnClickRetryBtnListener = OnClickRetryBtnListener()

    inner class OnClickRetryBtnListener : View.OnClickListener {

        var holder: ViewHolder? = null
        var adapter: CommonRecyclerAdapter? = null

        override fun onClick(v: View?) {
            isLoadMore = true
            holder?.showProgress()
            adapter?.callOnLoadMore()
        }
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder, position: Int) {
        super.unbindViewHolder(adapter, holder, position)
        mOnClickRetryBtnListener.adapter = null
        mOnClickRetryBtnListener.holder = null
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseViewHolder(view, adapter) {

        val retry_btn = itemView.retry_btn
        val progress_bar = itemView.progress_bar

        fun showRetry() {
            progress_bar.visibility = View.GONE
            retry_btn.visibility = View.VISIBLE
        }

        fun showProgress() {
            progress_bar.visibility = View.VISIBLE
            retry_btn.visibility = View.GONE
        }

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            AnimatorHelper.alphaAnimator(animators, itemView, 0f)
        }
    }

}