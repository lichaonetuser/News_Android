package com.mynews.common.core.util

import android.view.MotionEvent
import android.view.View

object TouchEventUtils {

    /**
     * 判断点击事件是否在指定视图范围内
     * @return true，如果在区域内
     */
    fun isTouchInViewLocation(view: View?, event: MotionEvent): Boolean {
        if (view != null) {
            val outLocation = intArrayOf(0, 0)
            view.getLocationInWindow(outLocation)
            val left = outLocation[0]
            val top = outLocation[1]
            val bottom = top + view.height
            val right = left + view.width
            return !(event.x > left && event.x < right
                    && event.y > top && event.y < bottom)
        }
        return false
    }

}