package com.box.app.news.item.base.holder

import android.view.View
import com.box.app.news.item.base.BaseNewsItem
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlin.collections.LinkedHashSet

/**
 *
 */
open class BaseEssayViewHolder(view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {

    private val adapterHashcode = adapter.hashCode()

    companion object {
        val expandedPosition: LinkedHashMap<Int, LinkedHashSet<String>> = linkedMapOf()
    }

    init {
        if (!expandedPosition.contains(adapterHashcode))
            expandedPosition.put(adapterHashcode, linkedSetOf())
    }

    fun isExpanded(aid: String):Boolean {
        return expandedPosition[adapterHashcode]?.contains(aid) ?: false
    }

    fun onClickExpand(aid: String) {
        expandedPosition[adapterHashcode]?.add(aid)
    }
}