package com.box.app.news.widget.vp

import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.ViewGroup
import java.util.*

abstract class LoopPagerAdapter(private val mViewPager: ViewPager) : PagerAdapter() {

    private val mViewList = ArrayList<View>()

    override fun notifyDataSetChanged() {
        mViewList.clear()
        initPosition()
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    private fun initPosition() {
        if (mViewPager.currentItem == 0 && getRealCount() > 0) {
            val half = Integer.MAX_VALUE / 2
            val start = half - half % getRealCount()
            setCurrent(start)
        }
    }

    private fun setCurrent(index: Int) {
        try {
            val field = ViewPager::class.java.getDeclaredField("mCurItem")
            field.isAccessible = true
            field.set(mViewPager, index)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val realPosition = position % getRealCount()
        val itemView = findViewByPosition(container, realPosition)
        container.addView(itemView)
        return itemView
    }

    private fun findViewByPosition(container: ViewGroup, position: Int): View {
        for (view in mViewList) {
            if (view.tag as Int == position && view.parent == null) {
                return view
            }
        }
        val view = getView(container, position)
        view.tag = position
        mViewList.add(view)
        return view
    }

    @Deprecated("无限滑动使用最大值方案", ReplaceWith("realCount"))
    override fun getCount(): Int {
        return if (getRealCount() <= 0) getRealCount() else Integer.MAX_VALUE
    }

    abstract fun getRealCount(): Int

    abstract fun getView(container: ViewGroup, position: Int): View

}
