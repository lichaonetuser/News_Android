package com.mynews.common.core.widget

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.Scroller
import com.mynews.common.core.log.Logger
//import com.crashlytics.android.Crashlytics

class CoreViewPager
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null)
    : ViewPager(context, attrs) {

    var isScrollable: Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        try {
            return if (isScrollable) {
                super.onInterceptTouchEvent(ev)
            } else {
                false
            }
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            return if (isScrollable) {
                super.onTouchEvent(ev)
            } else {
                true// 可行,消费,拦截事件
            }
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
        return true
    }

    /*---------------------*/
    /* ADJUST SCROLL SPEED */
    /*---------------------*/

    private var mScroller: FixedSpeedScroller? = null
    private val mDefaultScroller: Scroller? = null

    private fun initSpeedScroller() {
        try {
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            mScroller = FixedSpeedScroller(context, AccelerateDecelerateInterpolator())
            field.set(this, mScroller)
        } catch (e: Exception) {
            Logger.w("can not fix ViewPager speed!")
        }

    }

    fun setScrollSpeed(duration: Int) {
        if (mScroller == null) {
            initSpeedScroller()
        }
        mScroller!!.duration = duration
    }

    inner class FixedSpeedScroller : Scroller {
        private var mDuration = 500

        constructor(context: Context) : super(context) {}

        constructor(context: Context, interpolator: Interpolator) : super(context, interpolator) {}

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        fun setDuration(time: Int) {
            mDuration = time
        }
    }

    /*--------------------------------*/
    /* ADJUST SCROLL MINIMUM VELOCITY */
    /*--------------------------------*/

    fun setMinimumVelocity(minimumVelocity: Int) {
        if (minimumVelocity <= 0) {
            return
        }

        try {
            val field = ViewPager::class.java.getDeclaredField("mMinimumVelocity")
            field.isAccessible = true
            field.setInt(this, minimumVelocity)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}