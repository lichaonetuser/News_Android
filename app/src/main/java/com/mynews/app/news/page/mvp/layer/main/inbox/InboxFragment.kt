package com.mynews.app.news.page.mvp.layer.main.inbox

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.inbox.history.HistoryFragment
import com.mynews.app.news.page.mvp.layer.main.inbox.message.MessageFragment
import com.mynews.common.core.CoreApp
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.CommonNavigator
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.IPagerIndicator
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.IPagerTitleView
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.indicators.LinePagerIndicator
import com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.titles.SimplePagerTitleView
import kotlinx.android.synthetic.main.fragment_inbox.*
import org.jetbrains.anko.dip

open class InboxFragment<in V : InboxContract.View,
        out P : InboxContract.Presenter<V>>
    : MVPBaseFragment<V, InboxContract.Presenter<V>>(), InboxContract.View {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter: P = InboxPresenter<V>() as P
    override val mLayoutRes = R.layout.fragment_inbox
    override var mDispatchBack = true
    override val mAttachSwipeBack = true
    private var mIndicatorNavigator: CommonNavigator? = null
    private val mFragments = arrayOfNulls<CoreBaseFragment>(2)

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initFragments()
        initContainer()
    }

    private fun initFragments() {
        mFragments[0] = findChildFragment(MessageFragment::class.java)
        if (mFragments[0] != null) {
            mFragments[1] = findChildFragment(HistoryFragment::class.java)
        } else {
            mFragments[0] = CoreBaseFragment.instantiate(MessageFragment::class.java)
            mFragments[1] = CoreBaseFragment.instantiate(HistoryFragment::class.java)
        }
    }

    private fun initContainer() {
        mIndicatorNavigator = CommonNavigator(_mActivity)
        mIndicatorNavigator?.isAdjustMode = true
        container_indicator.navigator = mIndicatorNavigator
        container_indicator.bind(container_vp)
        if (mIndicatorNavigator?.adapter == null) {
            mIndicatorNavigator?.adapter = InboxNavigatorAdapter()
        } else {
            mIndicatorNavigator?.adapter?.notifyDataSetChanged()
        }
    }

    override fun loadPages(){
        container_vp.adapter = ListPagerAdapter()
        container_vp.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                setSwipeBackEnable(position == 0)
                mPresenter.onPageSelected(position)
            }

        })
    }

    override fun setCurrentPagePosition(position: Int) {
        container_vp.currentItem = position
    }

    inner class InboxNavigatorAdapter : CommonNavigatorAdapter() {

        override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
            val titleView = SimplePagerTitleView(context)
            titleView.normalColor = ResUtils.getColor(R.color.color_1)
            titleView.selectedColor = ResUtils.getColor(R.color.color_1)
            titleView.text = mFragments[index]?.getPageTitle()
            titleView.setOnClickListener {
                mPresenter.onClickTab(container_vp.currentItem, index)
            }
            return titleView
        }

        override fun getIndicator(context: Context?): IPagerIndicator {
            val indicator = LinePagerIndicator(context)
            indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
            indicator.setColors(ResUtils.getColor(R.color.color_42))
            indicator.lineHeight = CoreApp.getInstance().dip(2).toFloat()
            return indicator
        }

        override fun getCount(): Int {
            return mFragments.size
        }

    }

    inner class ListPagerAdapter : FragmentStatePagerAdapter(childFragmentManager) {

        override fun getItem(position: Int): Fragment {
            return mFragments[position] ?: Fragment()
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragments[position]?.getPageTitle() ?: ""
        }

        override fun getCount(): Int {
            return mFragments.size
        }

    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

}