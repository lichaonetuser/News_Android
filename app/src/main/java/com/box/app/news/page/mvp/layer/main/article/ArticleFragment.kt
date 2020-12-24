package com.box.app.news.page.mvp.layer.main.article

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.bean.Channel
import com.box.app.news.data.DataDictionary
import com.box.app.news.page.mvp.layer.main.article.headline.HeadlineChannelFragment
import com.box.app.news.page.mvp.layer.main.list.news.NewsListFragment
import com.box.app.news.page.mvp.layer.main.list.news.NewsListPresenterAutoBundle
import com.box.app.news.util.ReddotUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBaseFragment
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.CommonNavigator
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.IPagerIndicator
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.abs.IPagerTitleView
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.indicators.LinePagerIndicator
import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView
import kotlinx.android.synthetic.main.fragment_article.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import java.util.*
import org.jetbrains.anko.dip

open class ArticleFragment<in V : ArticleContract.View,
        out P : ArticleContract.Presenter<V>>
    : MVPBaseFragment<V, ArticleContract.Presenter<V>>(), ArticleContract.View {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter: P = ArticlePresenter<V>() as P
    override val mLayoutRes = R.layout.fragment_article
    override var mDispatchBack = false
    open lateinit var mIndicatorNavigator: CommonNavigator

    open val mArticleTab = true

    private var newChannelTipIndex = LinkedList<Int>()

    companion object {
        private val COLOR_LIST = arrayOf(
                ResUtils.getColor(R.color.color_22),
                ResUtils.getColor(R.color.color_24),
                ResUtils.getColor(R.color.color_26),
                ResUtils.getColor(R.color.color_28),
                ResUtils.getColor(R.color.color_19))
    }

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            status_bar.setBackgroundColor(ResUtils.getColor(R.color.color_1))
        }

        initContainer()

        if (mArticleTab) {
            mPresenter.getSearchHotwords(DataDictionary.SearchType.ARTICLE.value)
            setSearchClick()
        }

        if (mArticleTab) { //只在文章页面展示频道编辑入口
            new_channel_button.visibility = View.VISIBLE
            new_channel_button.setOnClickListener{ openSelectChannelActivity() }
            new_channel_indicator.setOnClickListener{ onClickNewChannelTip() }
        }

        //频道编辑页按钮左侧的渐变,文章频道才有用
        channel_gradient_shadow.visibility = if (mArticleTab) VISIBLE else GONE

        search_hint_txt.setDefaultText(ResUtils.getString(R.string.Query_TypeInSearchKeyword))
    }

    private fun initContainer() {
        mIndicatorNavigator = CommonNavigator(_mActivity)
        mIndicatorNavigator.leftPadding = CoreApp.getInstance().dip(-2.5f)
        mIndicatorNavigator.rightPadding = CoreApp.getInstance().dip(-2.5f)
        container_indicator.navigator = mIndicatorNavigator
        container_indicator.bind(container_vp)
        container_vp.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mPresenter.onPageSelected(position)

                //滑动到有新频道提醒的页面时，执行取消提醒逻辑（同标题点击）
                if (mArticleTab) {
                    val titleView = mIndicatorNavigator.getTitleViewAt(position)
                    if (titleView != null && titleView is TextView) {
                        val redDot = titleView.tag as String
                        if (!ReddotUtils.containRedDot(redDot)) {
                            ReddotUtils.addRedDot(redDot)
                            titleView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

                            newChannelTipIndex.remove(position)

                            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.SWITCH_CHANNEL_WITH_NEW)
                        }
                    }
                }
            }
        })

        if (mArticleTab) {
            //当标题控件完成布局时，检查「滑动到新频道按钮」是否应该展示
            mIndicatorNavigator.setOnLayoutFinishListener {
                if (!newChannelTipIndex.isEmpty()) {
                    val lastTitleIndex = newChannelTipIndex.last
                    if (mIndicatorNavigator.getChildLeftAt(lastTitleIndex) - mIndicatorNavigator.currentScrollX > container_indicator.width) {
                        new_channel_indicator.visibility = VISIBLE
                    } else {
                        new_channel_indicator.visibility = GONE
                    }
                } else {
                    new_channel_indicator.visibility = GONE
                }

                //监听标题控件的滑动，如果「滑动到新频道按钮」为展示状态，当最后一个新频道出现时，隐藏「滑动到新频道按钮」
                mIndicatorNavigator.setOnScrollListener { l, _, _, _ ->
                    if (new_channel_indicator.visibility != GONE) {
                        try {
                            val lastTitleIndex = newChannelTipIndex.last
                            if (l + container_indicator.width > mIndicatorNavigator.getChildLeftAt(lastTitleIndex)) {
                                new_channel_indicator.visibility = GONE
                            }
                        } catch (e: Exception) {
                            new_channel_indicator.visibility = GONE
                        }
                    }
                }
            }
        }
        OverScrollDecoratorHelper.setUpOverScroll(container_vp)
    }

    //打开频道编辑界面
    private fun openSelectChannelActivity() {
        if (new_icon_article.visibility == VISIBLE) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_CHANNEL_EDIT_WITH_NEW)
        }
        mPresenter.onChannelEditClick()
    }

    //点击滑到最后一个reddot的位置
    private fun onClickNewChannelTip() {
        if (newChannelTipIndex.isEmpty()) {
            return
        }
        val lastTitleIndex = newChannelTipIndex.last
        val right = mIndicatorNavigator.getChildRightAt(lastTitleIndex)
        val scrollDelta = right - container_indicator.width + CoreApp.getInstance().dip(22)
        if (lastTitleIndex == mIndicatorNavigator.titleViewCount - 1) {
            mIndicatorNavigator.fullScrollToRight()
        } else {
            mIndicatorNavigator.scrollTo(scrollDelta)
        }
        new_channel_indicator.visibility = GONE

        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.CLICK_BUBBLE_SHOW_NEW_CHANNEL)
    }

    override fun setChannels(channels: List<Channel>) {
        if (mIndicatorNavigator.adapter == null) {
            mIndicatorNavigator.adapter = NewsListNavigatorAdapter(channels)
        } else {
            mIndicatorNavigator.adapter.notifyDataSetChanged()
        }
        container_vp.adapter = getListPagerAdapter(channels)
    }

    override fun getCurrentPagePosition(): Int {
        return container_vp.currentItem
    }

    override fun setCurrentPagePosition(position: Int) {
        container_vp.currentItem = position
    }

    override fun setSearchData(currentHotword: String) {
        if (currentHotword.isEmpty()) {
            search_hint_txt.setDefaultText(ResUtils.getString(R.string.Query_TypeInSearchKeyword))
        } else {
            search_hint_txt.nextText(currentHotword)
        }
    }

    fun switchSearchHotwords() {
        mPresenter.switchSearchHotwords()
    }

    open fun getTitleView(channels: List<Channel>, index: Int): IPagerTitleView {
        val titleView = ScaleTransitionPagerTitleView(context)
        titleView.normalColor = ResUtils.getColor(R.color.color_43)
        titleView.selectedColor = COLOR_LIST[index % 5]
        titleView.text = channels[index].name
        titleView.textSize = 17f
        titleView.minScale = 0.88f
        titleView.typeface = Typeface.DEFAULT_BOLD
        titleView.setOnClickListener {
            mPresenter.onClickTab(container_vp.currentItem, index)
            if (Math.abs(container_vp.currentItem - index) > 1) {
                container_vp.setCurrentItem(index, false)
            } else {
                container_vp.currentItem = index
            }

            //点击有新频道提醒的标题时，执行取消提醒逻辑(同viewpager滑动)
            if (mArticleTab) {
                val redDot = titleView.tag as String
                if (!ReddotUtils.containRedDot(redDot)) {
                    ReddotUtils.addRedDot(redDot)
                    titleView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

                    newChannelTipIndex.remove(index)

                    AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS, AnalyticsKey.Parameter.SWITCH_CHANNEL_WITH_NEW)
                }
            }
        }

        if (!mArticleTab) {
            return titleView
        }
        //如果是文章列表，如果有reddot符合条件就展示icon
        //如果不是默认值 && 从来没点过
        val reddotString = channels[index].redDot.toString()
        titleView.tag = reddotString
        if (!ReddotUtils.containRedDot(reddotString)) {
            val img = ResUtils.getDrawable(R.drawable.newiconlong)
            titleView.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
            newChannelTipIndex.add(index)
            val params = new_icon_article.layoutParams
            params.width = img.intrinsicWidth
            params.height = img.intrinsicWidth
        }
        return titleView
    }

    open fun getIndicator(context: Context?): IPagerIndicator {
        val indicator = LinePagerIndicator(context)
        indicator.setColors(*COLOR_LIST)
        indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
        indicator.lineHeight = CoreApp.getInstance().dip(2).toFloat()
        indicator.roundRadius = CoreApp.getInstance().dip(1).toFloat()
        indicator.yOffset = CoreApp.getInstance().dip(4).toFloat()
        return indicator
    }

    inner class NewsListNavigatorAdapter(private val channels: List<Channel>)
        : CommonNavigatorAdapter() {

        init {
            newChannelTipIndex.clear()
        }

        override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
            return getTitleView(channels, index)
        }

        override fun getIndicator(context: Context?): IPagerIndicator {
            return this@ArticleFragment.getIndicator(context)
        }

        override fun getCount(): Int {
            return channels.size
        }

    }

    open fun getListPagerAdapter(channels: List<Channel>): FragmentStatePagerAdapter {
        return ListPagerAdapter(channels, childFragmentManager, true)
    }

    //7.1 热词滚动需求 ： 新增switchSearchHint参数用于指示该列表的刷新操作是否应该更新搜索区域的热词
    class ListPagerAdapter(var channels: List<Channel>, fm: FragmentManager, private val switchSearchHint: Boolean = false)
        : FragmentStatePagerAdapter(fm), IChannelPagerAdapter {

        override fun getItem(position: Int): Fragment {
            val channel = channels[position]
            //7.1 headline变为频道  如果该频道不是headline，用通用的列表布局，否则，用新的headline布局
            if (channel.chid != DataDictionary.CHANNEL_ID_HEADLINE) {
                val bundle = NewsListPresenterAutoBundle
                        .builder(channels[position], "feed", AnalyticsKey.Event.NEWS)
                        .mSwitchSearchHint(switchSearchHint)
                        .bundle()
                return CoreBaseFragment.instantiate(NewsListFragment::class.java, bundle)
            } else {
                channel.channelType = DataDictionary.ChannelType.ARTICLE.value
                val bundle = NewsListPresenterAutoBundle
                        .builder(channel, "public_feed", AnalyticsKey.Event.HEADLINE)
//                        .mRefer(AppLog.Refer
//                                .newBuilder()
//                                .setItemId(channel.chid)
//                                .setName(AppLogKey.Refer.MAIN)
//                                .build())
//                        .mItemRefer(AppLog.Refer
//                                .newBuilder()
//                                .setItemId(channel.chid)
//                                .setName(AppLogKey.Refer.HEADLINE)
//                                .build())
//                        .mPositionRefer(AppLog.Refer
//                                .newBuilder()
//                                .setItemId(channel.chid)
//                                .setName(AppLogKey.Refer.HEADLINE)
//                                .build())
//                        .mPositionSourceRefer(AppLog.Refer
//                                .newBuilder()
//                                .setName(AppLogKey.Refer.MAIN)
//                                .build())
                        .mSwitchSearchHint(switchSearchHint)
                        .bundle()
                return CoreBaseFragment.instantiate(HeadlineChannelFragment::class.java, bundle)
            }

        }

        override fun getCount(): Int {
            return channels.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return channels[position].name
        }

        override fun getPageChannels(): List<Channel> {
            return channels
        }

    }

    interface IChannelPagerAdapter {
        fun getPageChannels(): List<Channel>
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setDispatchBack(dispatchBack: Boolean) {
        mDispatchBack = dispatchBack
    }

    fun setCurrentTabFromChannelId(channelId: String) {
        val adapter = container_vp.adapter
        if (adapter is IChannelPagerAdapter) {
            val channels = adapter.getPageChannels()
            val index = channels.indexOfFirst { it.chid == channelId }
            if (index > -1) {
                container_vp.currentItem = index
            }
        }
    }

    fun hideSearchLayout() {
        header_layout?.visibility = View.GONE
    }

    private fun setSearchClick() {
        search_bar_layout.setOnClickListener {
            mPresenter.onSearchClick()
        }
    }

    //注意：子类父类该方法完全不一致
    //更新换过标题位置的导航头,文章跟视频应该独立
    override fun updateChannels(channels: List<Channel>, position: Int, forceUpdatePostion: Boolean) {
        if (container_vp.adapter is ListPagerAdapter) {
            container_vp.adapter = getListPagerAdapter(channels)
            container_vp.currentItem = position
            mIndicatorNavigator.adapter = NewsListNavigatorAdapter(channels)
            if (forceUpdatePostion) {
                mIndicatorNavigator.onPageSelected(position)
            }
            updateNewChannelTip(channels, position)
        }
    }

    //频道编辑后，有可能新频道的位置发生了变化，需要重新检查一下
    private fun updateNewChannelTip(channels: List<Channel>, selectPostion: Int) {
        newChannelTipIndex.clear()
        repeat(mIndicatorNavigator.titleViewCount, {
            val view = mIndicatorNavigator.getTitleViewAt(it)
            if (view is TextView) {
                val redDot = channels[it].redDot.toString()
                if (!ReddotUtils.containRedDot(redDot) && it != selectPostion) {

                    val img = ResUtils.getDrawable(R.drawable.newiconlong)
                    view.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                    newChannelTipIndex.add(it)
                    val params = new_icon_article.layoutParams
                    params.width = img.intrinsicWidth
                    params.height = img.intrinsicWidth
                } else {
                    view.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        })
    }

    override fun hideOrShowChannelEditTip(show: Boolean) {
        val img = ResUtils.getDrawable(R.drawable.newiconlong)
        val params = new_icon_article.layoutParams
        params.width = img.intrinsicWidth
        params.height = img.intrinsicWidth
        new_icon_article.visibility = if (show) VISIBLE else GONE
    }
}