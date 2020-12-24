package com.box.common.extension.widget.recycler.holder

import android.view.View
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import eu.davidea.viewholders.FlexibleViewHolder

open class BaseViewHolder @JvmOverloads constructor(
        view: View,
        val mCommonAdapter: CommonRecyclerAdapter,
        stickyHeader: Boolean = false)
    : FlexibleViewHolder(view, mCommonAdapter, stickyHeader) {

    fun bindItemChildViewClick(view: View?) {
        view?.setOnClickListener {
            val position = flexibleAdapterPosition
            mCommonAdapter.mItemChildClickListener?.onItemChildClick(position, view)
        }
    }

    open fun onViewAttachedToWindow() {

    }

    open fun onViewDetachedFromWindow() {

    }

    open fun onViewRecycled() {

    }

}
