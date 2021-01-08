package com.mynews.common.extension.widget.recycler.item

import android.animation.Animator
import android.view.View

import com.mynews.app.news.R
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder

import eu.davidea.flexibleadapter.helpers.AnimatorHelper

class DefaultNoMoreItem(id: Any = "NoMore") : BaseItem<Any, DefaultNoMoreItem.ViewHolder>(id) {

    override fun getLayoutRes(): Int {
        return R.layout.core_item_default_no_more
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return spanCount
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseViewHolder(view, adapter) {

        override fun scrollAnimators(animators: List<Animator>, position: Int, isForward: Boolean) {
            AnimatorHelper.alphaAnimator(animators, itemView, 0f)
        }
    }

}