package com.mynews.app.news.page.mvp.layer.main.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.mynews.app.news.R
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.date.ClassificationDataBean
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.item.adapter.HomePagerAdapter
import com.mynews.app.news.page.activity.MainActivity
import com.mynews.app.news.util.LoopTransformer
import com.mynews.app.news.view.MyViewpager
import com.mynews.app.news.view.PullToRefreshView
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_article.search_bar_layout
import kotlinx.android.synthetic.main.fragment_article.search_hint_txt
import kotlinx.android.synthetic.main.fragment_article.status_bar
import kotlinx.android.synthetic.main.fragment_classification.*
import java.util.*

open class ClassificationFragment<in V : ClassificationContract.View,
        out P : ClassificationContract.Presenter<V>>
    : MVPBaseFragment<V, ClassificationContract.Presenter<V>>(),
    ClassificationContract.View , PullToRefreshView.OnFooterRefreshListener,
    PullToRefreshView.OnHeaderRefreshListener , PullToRefreshView.OnItemLeft , PullToRefreshView.OnItemRight,
    PullToRefreshView.OnItemClick,
    ViewPager.OnPageChangeListener {

    @Suppress("UNCHECKED_CAST")
    override val mPresenter: P = ClassificationPresenter<V>() as P
    override val mLayoutRes = R.layout.fragment_classification
    override var mDispatchBack = false
//    open lateinit var mIndicatorNavigator: CommonNavigator
    lateinit var mPagerAdapter: HomePagerAdapter
    var ListData : MutableList<ClassificationDataBean> = ArrayList<ClassificationDataBean>()
    var position :Int = 0
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
        if (mArticleTab) {
            mPresenter.getSearchHotwords(DataDictionary.SearchType.ARTICLE.value)
            setSearchClick()
        }
        search_hint_txt.setDefaultText(ResUtils.getString(R.string.Query_TypeInSearchKeyword))
        pull_to_refresh.setOnFooterRefreshListener(this)
        pull_to_refresh.setOnHeaderRefreshListener(this)
        pull_to_refresh.setOnItemLeft(this)
        pull_to_refresh.setOnItemRight(this)
        pull_to_refresh.setOnItemClick(this)
        mPagerAdapter = HomePagerAdapter(_mActivity)
        vp_home_navigation.adapter = mPagerAdapter
        vp_home_navigation.offscreenPageLimit = 3//预加载3个页面
        vp_home_navigation.setPageTransformer(false, LoopTransformer())
        vp_home_navigation.setOnPageChangeListener(this);
    }


    override fun setChannels(channels: List<Channel>) {
        Log.d("ADB",channels.toString())
        var mClassDataBean : ClassificationDataBean
        ListData = ArrayList<ClassificationDataBean>()
        for (i in channels){
            mClassDataBean = ClassificationDataBean()
            when (i.name) {
                getString(R.string.channel_ForYou) -> {
                    mClassDataBean.name = getString(R.string.channel_ForYou)
                    mClassDataBean.viewid = R.mipmap.channel_foryou
                }
                getString(R.string.girl_ForYou) -> {
                    mClassDataBean.name = getString(R.string.girl_ForYou)
                    mClassDataBean.viewid = R.mipmap.girl_foryou
                }
                getString(R.string.music_ForYou) -> {
                    mClassDataBean.name = getString(R.string.music_ForYou)
                    mClassDataBean.viewid = R.mipmap.music_foryou
                }
                getString(R.string.cartoon_ForYou) -> {
                    mClassDataBean.name = getString(R.string.cartoon_ForYou)
                    mClassDataBean.viewid = R.mipmap.cartoon_foryou
                }
                getString(R.string.entertainment_ForYou) -> {
                    mClassDataBean.name = getString(R.string.entertainment_ForYou)
                    mClassDataBean.viewid = R.mipmap.entertainment_foryou
                }
                getString(R.string.economics_ForYou)-> {
                    mClassDataBean.name = getString(R.string.economics_ForYou)
                    mClassDataBean.viewid = R.mipmap.economics_foryou
                }
                getString(R.string.car_ForYou)-> {
                    mClassDataBean.name = getString(R.string.car_ForYou)
                    mClassDataBean.viewid = R.mipmap.car_foryou
                }
                getString(R.string.politics_ForYou)-> {
                    mClassDataBean.name = getString(R.string.politics_ForYou)
                    mClassDataBean.viewid = R.mipmap.politics_foryou
                }
                getString(R.string.sports_ForYou)-> {
                    mClassDataBean.name = getString(R.string.sports_ForYou)
                    mClassDataBean.viewid = R.mipmap.sports_foryou
                }
                getString(R.string.gif_ForYou)-> {
                    mClassDataBean.name = getString(R.string.gif_ForYou)
                    mClassDataBean.viewid = R.mipmap.gif_foryou
                }
                getString(R.string.illustration_ForYou)-> {
                    mClassDataBean.name = getString(R.string.illustration_ForYou)
                    mClassDataBean.viewid = R.mipmap.illustration_foryou
                }
                getString(R.string.headlines_ForYou)-> {
                    mClassDataBean.name = getString(R.string.headlines_ForYou)
                    mClassDataBean.viewid = R.mipmap.headlines_foryou
                }
                getString(R.string.restaurant_ForYou)-> {
                    mClassDataBean.name = getString(R.string.restaurant_ForYou)
                    mClassDataBean.viewid = R.mipmap.restaurant_foryou
                }
                getString(R.string.international_ForYou)-> {
                    mClassDataBean.name = getString(R.string.international_ForYou)
                    mClassDataBean.viewid = R.mipmap.international_foryou
                }
                getString(R.string.domestic_ForYou)-> {
                    mClassDataBean.name = getString(R.string.domestic_ForYou)
                    mClassDataBean.viewid = R.mipmap.domestic_foryou
                }
                getString(R.string.travel_ForYou)-> {
                    mClassDataBean.name = getString(R.string.travel_ForYou)
                    mClassDataBean.viewid = R.mipmap.travel_foryou
                }
                getString(R.string.summary_ForYou)-> {
                    mClassDataBean.name = getString(R.string.summary_ForYou)
                    mClassDataBean.viewid = R.mipmap.summary_foryou
                }
                getString(R.string.it_ForYou)-> {
                    mClassDataBean.name = getString(R.string.it_ForYou)
                    mClassDataBean.viewid = R.mipmap.it_foryou
                }
                getString(R.string.report_ForYou)-> {
                    mClassDataBean.name = getString(R.string.report_ForYou)
                    mClassDataBean.viewid = R.mipmap.report_foryou
                }
                getString(R.string.woman_ForYou)-> {
                    mClassDataBean.name = getString(R.string.woman_ForYou)
                    mClassDataBean.viewid = R.mipmap.woman_foryou
                }
                getString(R.string.healing_ForYou)-> {
                    mClassDataBean.name = getString(R.string.healing_ForYou)
                    mClassDataBean.viewid = R.mipmap.healing_foryou
                }
                getString(R.string.beauty_ForYou)-> {
                    mClassDataBean.name = getString(R.string.beauty_ForYou)
                    mClassDataBean.viewid = R.mipmap.beauty_foryou
                }
                getString(R.string.interesting_ForYou)-> {
                    mClassDataBean.name = getString(R.string.interesting_ForYou)
                    mClassDataBean.viewid = R.mipmap.interesting_foryou
                }
                getString(R.string.beauty18_ForYou)-> {
                    mClassDataBean.name = getString(R.string.beauty18_ForYou)
                    mClassDataBean.viewid = R.mipmap.beauty18_foryou
                }
                getString(R.string.story_ForYou)-> {
                    mClassDataBean.name = getString(R.string.story_ForYou)
                    mClassDataBean.viewid = R.mipmap.story_foryou
                }
                getString(R.string.healed_ForYou)-> {
                    mClassDataBean.name = getString(R.string.healed_ForYou)
                    mClassDataBean.viewid = R.mipmap.healed_foryou
                }
                getString(R.string.bytheway_ForYou)-> {
                    mClassDataBean.name = getString(R.string.bytheway_ForYou)
                    mClassDataBean.viewid = R.mipmap.bytheway_foryou
                }
                getString(R.string.life_ForYou)-> {
                    mClassDataBean.name = getString(R.string.life_ForYou)
                    mClassDataBean.viewid = R.mipmap.life_foryou
                }
                getString(R.string.video_ForYou)-> {
                    mClassDataBean.name = getString(R.string.video_ForYou)
                    mClassDataBean.viewid = R.mipmap.video_foryou
                }
                getString(R.string.game_ForYou)-> {
                    mClassDataBean.name = getString(R.string.game_ForYou)
                    mClassDataBean.viewid = R.mipmap.game_foryou
                }
            }
            ListData.add(mClassDataBean)
        }
        mPagerAdapter.addAll(ListData)
        Log.d("ADB",ListData.toString())
    }

    override fun getCurrentPagePosition(): Int {
        return 0
    }

    override fun setCurrentPagePosition(position: Int) {
    }

    override fun setSearchData(currentHotword: String) {
        if (currentHotword.isEmpty()) {
            search_hint_txt.setDefaultText(ResUtils.getString(R.string.Query_TypeInSearchKeyword))
        } else {
            search_hint_txt.nextText(currentHotword)
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setDispatchBack(dispatchBack: Boolean) {
        mDispatchBack = dispatchBack
    }

    private fun setSearchClick() {
        search_bar_layout.setOnClickListener {
            mPresenter.onSearchClick()
        }
    }

    override fun onFooterRefresh(view: PullToRefreshView?) {
        pull_to_refresh.onFooterRefreshComplete()
        _mActivity.startActivity( Intent(_mActivity, MainActivity::class.java))
    }

    override fun onHeaderRefresh(view: PullToRefreshView?) {
        pull_to_refresh.onHeaderRefreshComplete()
    }

    override fun onItemLeft() {
        if (position < ListData.size) {
            vp_home_navigation.currentItem = ++position
        }
    }

    override fun onItemRight() {
        if (position > 0) {
            vp_home_navigation.currentItem = --position
        }
    }

    override fun onItemItemClick() {
        var intent = Intent(_mActivity,MainActivity::class.java)
        var bundle = Bundle()
        bundle.putInt("index",position)
        intent.putExtras(bundle)
        _mActivity.startActivity(intent)
    }

    /**
     *
     */
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(index: Int, positionOffset: Float, positionOffsetPixels: Int) {
        position = index
    }

    override fun onPageSelected(position: Int) {

    }
}