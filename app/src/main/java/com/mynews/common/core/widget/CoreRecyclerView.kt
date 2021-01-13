package com.mynews.common.core.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
//import com.crashlytics.android.Crashlytics

open class CoreRecyclerView
@JvmOverloads
constructor(context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    private var mScrollToTopPosition = -1
    private var mScrollingToTop = false

    var noTouch = false

    init {
        isMotionEventSplittingEnabled = false
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //在这里进行第二次滚动（最后的距离）
                if (mScrollingToTop) {
                    try {
                        mScrollingToTop = false
                        if (layoutManager !is LinearLayoutManager) {
                            return
                        }
                        val linearLayoutManager: LinearLayoutManager = layoutManager as LinearLayoutManager
                        //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                        val n = mScrollToTopPosition - linearLayoutManager.findFirstVisibleItemPosition()
                        if (n in 0..(childCount - 1)) {
                            //获取要置顶的项顶部离RecyclerView顶部的距离
                            val top = getChildAt(n).top
                            //最后的移动
                            smoothScrollBy(0, top)
                        }
                    } catch (e: Exception) {
//                        Crashlytics.logException(e)
                    }
                }
            }
        })
    }

    fun scrollToPositionTop(position: Int) {
        try {
            if (layoutManager !is LinearLayoutManager) {
                return
            }
            val linearLayoutManager: LinearLayoutManager = layoutManager as LinearLayoutManager
            //滑动到指定的item
            this.mScrollToTopPosition = position;//记录一下 在第三种情况下会用到
            //拿到当前屏幕可见的第一个position跟最后一个postion
            val firstItem = linearLayoutManager.findFirstVisibleItemPosition();
            val lastItem = linearLayoutManager.findLastVisibleItemPosition();
            //区分情况
            when {
                position <= firstItem -> //当要置顶的项在当前显示的第一个项的前面时
                    smoothScrollToPosition(position)
                position <= lastItem -> {
                    //当要置顶的项已经在屏幕上显示时
                    val top = getChildAt(position - firstItem).top
                    smoothScrollBy(0, top)
                }
                else -> {
                    //这里这个变量是用在RecyclerView滚动监听里面的
                    mScrollingToTop = true
                    //当要置顶的项在当前显示的最后一项的后面时
                    smoothScrollToPosition(position)
                }
            }
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (noTouch){
            return false
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        if (noTouch){
            return false
        }
        return super.onInterceptTouchEvent(e)
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        if (noTouch){
            return false
        }
        return super.onTouchEvent(e)
    }



}


