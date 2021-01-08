package com.mynews.common.extension.widget.recycler.decoration

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import com.mynews.app.news.R
import com.mynews.common.core.CoreApp
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import org.jetbrains.anko.dip

class CommonItemDecoration(context: Context = CoreApp.getInstance()) : FlexibleItemDecoration(context) {

    private var mHide = false
    private var lastItem = false
    private var padding = -1

    companion object {
        val DEFAULT: CommonItemDecoration by lazy {
            CommonItemDecoration().withDivider(R.drawable.divider_common).withDrawDividerOnLastItem(true)
        }
    }

    private var noDrawIndexes:List<Int> = listOf()

    fun setNoDrawIndexList(list:List<Int>) {
        noDrawIndexes = list
    }

    override fun shouldDrawDivider(viewHolder: RecyclerView.ViewHolder?): Boolean {
        return !mHide && !noDrawIndexes.contains(viewHolder?.layoutPosition)
    }

    fun hide(): CommonItemDecoration {
        mHide = true
        return this
    }

    override fun withDivider(resId: Int, vararg viewTypes: Int?): CommonItemDecoration {
        return super.withDivider(resId, *viewTypes) as CommonItemDecoration
    }

    override fun withDrawDividerOnLastItem(lastItem: Boolean): CommonItemDecoration {
        this.lastItem =lastItem
        return super.withDrawDividerOnLastItem(lastItem) as CommonItemDecoration
    }

    @SuppressLint("NewApi")
    override fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                    parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val itemCount = parent.childCount
        val mDividerOnLastItem = if (lastItem) 0 else 1
        for (i in 0 until itemCount - mDividerOnLastItem) {
            val child = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(child)
            if (shouldDrawDivider(viewHolder)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.bottom + Math.round(child.translationY)
                val top = bottom - mDivider.intrinsicHeight
                if (padding == -1) {
                    mDivider.setBounds(left, top, right, bottom)
                } else {
                    mDivider.setBounds(left + padding, top, right - padding, bottom)
                }
                mDivider.draw(canvas)
            }
        }
        canvas.restore()
    }

    fun setPadding(dp:Int): CommonItemDecoration {
        if (dp >= 0) {
            this.padding = CoreApp.getInstance().dip(dp)
        } else {
            padding = -1
        }
        return this
    }
}