package com.box.app.news.widget.nested

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import com.box.common.core.log.Logger

class NestedChildRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), INestedChild {

    var mNestedParent: NestedParentScrollView? = null
    var maxHeight = View.MeasureSpec.EXACTLY

    override fun isScrolledToTop(): Boolean {
        return !canScrollVertically(-1)
    }

    override fun isScrolledToBottom(): Boolean {
        return !canScrollVertically(1)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val hSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, hSpec)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        findNestedParent(parent)
        mNestedParent?.fling(0)
        return super.dispatchTouchEvent(ev)
    }

    fun findNestedParent(parent: ViewParent?) {
        if (mNestedParent != null) {
            return
        }
        if (parent is NestedParentScrollView) {
            mNestedParent = parent
        } else {
            findNestedParent(parent?.parent)
        }
    }

}