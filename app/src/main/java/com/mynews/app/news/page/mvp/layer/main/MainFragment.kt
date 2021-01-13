package com.mynews.app.news.page.mvp.layer.main

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.MainTabEnum.NEWS
import com.mynews.app.news.page.mvp.layer.main.article.ArticleFragment
import com.mynews.app.news.page.mvp.layer.main.me.MeFragment
import com.mynews.app.news.page.mvp.layer.main.video.VideoFragment
import com.mynews.app.news.widget.navigation.MainBottomNavigationItemView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.yokeyword.fragmentation.SupportFragment

class MainFragment constructor(Icon: String) : MVPBaseFragment<MainContract.View,
        MainContract.Presenter<MainContract.View>>(),
        MainContract.View {

    override val mPresenter = MainPresenter()
    override val mLayoutRes = R.layout.fragment_main
    override var mDispatchBack = false
    private var Icon :String = Icon
    private val mFragments: Array<SupportFragment?> =
            arrayOfNulls(MainTabEnum.values().size)
//    private val mBottomTabs: Array<BaseTabItem?> = arrayOfNulls(MainTabEnum.values().size)

    private val mDefaultTab = NEWS
//    private var mBottomNavigation: NavigationController? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        iv_video1.setBackgroundResource(getBackgroundIco(Icon))
        initFragment()
        initBottomNavigationBar()
    }

    /************************* 初始化Fragment *************************/

    private fun initFragment() {
        //如果新闻页面找不到，说明还没有初始化过，反之则表明已经有加载
        val newsFragment = findChildFragment(ArticleFragment::class.java)
        if (newsFragment == null) {
            //首次加载各个页面
            var bundle = Bundle()
            bundle.putString("Icon",Icon)
            mFragments[MainTabEnum.NEWS.getPosition()] = CoreBaseFragment.instantiate(ArticleFragment::class.java,bundle)
            mFragments[MainTabEnum.VIDEO.getPosition()] = CoreBaseFragment.instantiate(VideoFragment::class.java)
            mFragments[MainTabEnum.ME.getPosition()] = CoreBaseFragment.instantiate(MeFragment::class.java)
            //加载根布局
            loadMultipleRootFragment(R.id.container_layout, mDefaultTab.getPosition(), *mFragments)
        } else {
            mFragments[MainTabEnum.NEWS.getPosition()] = newsFragment
            mFragments[MainTabEnum.VIDEO.getPosition()] = findChildFragment(VideoFragment::class.java)
            mFragments[MainTabEnum.ME.getPosition()] = findChildFragment(MeFragment::class.java)
        }
    }

    /************************* 下方导航栏相相关逻辑 *************************/

    private fun initBottomNavigationBar() {
        ll_Refresh.setOnClickListener {
            _mActivity.finish()
        }
        rl_ic.setOnClickListener {
            showHideFragment(mFragments[0], mFragments[2])
        }
        ll_Me.setOnClickListener {
            showHideFragment(mFragments[2], mFragments[0])
        }



//        mBottomTabs[MainTabEnum.NEWS.getPosition()] = newBottomNavigationItem(R.drawable.main_bottombar_news_selector, ResUtils.getString(R.string.News_News),
//                checkedTitle = ResUtils.getString(R.string.Common_Refresh))
//        mBottomTabs[MainTabEnum.VIDEO.getPosition()] = newBottomNavigationItem(R.drawable.main_bottombar_video_selector, ResUtils.getString(R.string.Video_Video))
//        mBottomTabs[MainTabEnum.ME.getPosition()] = newBottomNavigationItem(R.drawable.main_bottombar_me_selector, ResUtils.getString(R.string.Setting_Me),
//                indicatorPaddingBottomDp = 6)


//        mBottomNavigation = bottom_navigation_bar.custom()
//                .addItem(mBottomTabs[MainTabEnum.NEWS.getPosition()])
//                .addItem(mBottomTabs[MainTabEnum.VIDEO.getPosition()])
//                .addItem(mBottomTabs[MainTabEnum.ME.getPosition()])
//                .build()

        //切换和重新选中的监听
//        mBottomNavigation?.addTabItemSelectedListener(object : OnTabItemSelectedListener {
//            override fun onSelected(index: Int, old: Int) {
//                mBottomNavigation?.setHasMessage(index, false)
//                mPresenter.onTabSelected(old, index)
//                if (old == -1) {
//                    showHideFragment(mFragments[index])
//                } else {
//                    showHideFragment(mFragments[index], mFragments[old])
//                }
//
////                if (index == MainTabEnum.NEWS.getPosition()) {
////                    val newsFragment = findChildFragment(ArticleFragment::class.java)
////                    newsFragment?.switchSearchHotwords()
////                }
//            }
//
//            override fun onRepeat(index: Int) {
//                mBottomNavigation?.setHasMessage(index, false)
//                mPresenter.onTabReselected(index)
//            }
//        })
    }

    /**
     * 创建下方导航栏Item
     */
    private fun newBottomNavigationItem(@DrawableRes iconRes: Int,
                                        normalTitle: String,
                                        checkedTitle: String = normalTitle,
                                        indicatorPaddingLeftDp: Int = 0,
                                        indicatorPaddingTopDp: Int = 0,
                                        indicatorPaddingRightDp: Int = 0,
                                        indicatorPaddingBottomDp: Int = 0): BaseTabItem {
        val itemVIew = MainBottomNavigationItemView(_mActivity)
        itemVIew.load(iconRes, normalTitle, checkedTitle,
                indicatorPaddingLeftDp, indicatorPaddingTopDp, indicatorPaddingRightDp, indicatorPaddingBottomDp)
        return itemVIew
    }

    /**
     * 获取当前选中的Tab
     */
    override fun getCurrentTab(): IMainTab {
//        val selectedPosition = mBottomNavigation?.selected ?: 0
        return MainTabEnum.positionOf(2)
    }

    /**
     * 切换Tab，注意使用的枚举类
     */
    override fun changeTab(tab: IMainTab) {
//        mBottomNavigation?.setSelect(tab.getPosition())
    }

    override fun getTabIco(name: String) {
        iv_video1.setBackgroundResource(getBackgroundIco(name))
    }

    private fun getBackgroundIco(name : String) : Int{
        var ico : Int
        if (name == getString(R.string.channel_ForYou)) {
            ico = R.mipmap.recommend_h
        } else if (name == getString(R.string.girl_ForYou)) {
            ico = R.mipmap.girl_h;
        } else if (name == getString(R.string.music_ForYou)) {
            ico = R.mipmap.music_h
        } else if (name == getString(R.string.cartoon_ForYou)) {
            ico = R.mipmap.cartoon_h
        } else if (name == getString(R.string.entertainment_ForYou)) {
            ico = R.mipmap.entertainment_h
        } else if (name == getString(R.string.economics_ForYou)) {
            ico = R.mipmap.economics_h
        } else if (name == getString(R.string.car_ForYou)) {
            ico = R.mipmap.car_h
        } else if (name == getString(R.string.politics_ForYou)) {
            ico = R.mipmap.politics_h
        } else if (name == getString(R.string.sports_ForYou)) {
            ico = R.mipmap.sports_h
        } else if (name == getString(R.string.gif_ForYou)) {
            ico = R.mipmap.gif_h
        } else if (name == getString(R.string.illustration_ForYou)) {
            ico = R.mipmap.illustration_h
        } else if (name == getString(R.string.headlines_ForYou)) {
            ico = R.mipmap.headlines_h
        } else if (name == getString(R.string.restaurant_ForYou)) {
            ico = R.mipmap.restaurant_h
        } else if (name == getString(R.string.international_ForYou)) {
            ico = R.mipmap.international_h
        } else if (name == getString(R.string.domestic_ForYou)) {
            ico = R.mipmap.domestic_h
        } else if (name == getString(R.string.travel_ForYou)) {
            ico = R.mipmap.travel_h
        } else if (name == getString(R.string.summary_ForYou)) {
            ico = R.mipmap.whole_h
        } else if (name == getString(R.string.it_ForYou)) {
            ico = R.mipmap.it_technology_h
        } else if (name == getString(R.string.report_ForYou)) {
            ico = R.mipmap.quick_report_h
        } else if (name == getString(R.string.woman_ForYou)) {
            ico = R.mipmap.female_h
        } else if (name == getString(R.string.healing_ForYou)) {
            ico = R.mipmap.be_cured_h
        } else if (name == getString(R.string.beauty_ForYou)) {
            ico = R.mipmap.beauty_h
        } else if (name == getString(R.string.interesting_ForYou)) {
            ico = R.mipmap.international_h
        } else if (name == getString(R.string.beauty18_ForYou)) {
            ico = R.mipmap.prohibit18_h
        } else if (name == getString(R.string.story_ForYou)) {
            ico = R.mipmap.funny_h
        } else if (name == getString(R.string.healed_ForYou)) {
            ico = R.mipmap.cure_h
        } else if (name == getString(R.string.bytheway_ForYou)) {
            ico = R.mipmap.comic_h
        } else if (name == getString(R.string.life_ForYou)) {
            ico = R.mipmap.life_h
        } else if (name == getString(R.string.video_ForYou)) {
            ico = R.mipmap.video_h
        } else if (name == getString(R.string.game_ForYou)) {
            ico = R.mipmap.game_h
        }else{
            ico = R.mipmap.recommend_h
        }
        return ico
    }

    /**
     * 设置是否有反馈
     */
    override fun setHasUnreadFeedback(hasUnread: Boolean) {
//        mBottomNavigation?.setHasMessage(MainTabEnum.ME.getPosition(), hasUnread)
    }

    fun setCurrentTabAndChannel(tabIndex: Int, channelId: String) {
//        val tabCount = mBottomNavigation?.itemCount ?: -1
//        if (tabIndex < 0 || tabIndex >= tabCount) {
//            return
//        }
//        val fragmentsSize = mFragments.size
//        if (tabIndex >= fragmentsSize) {
//            return
//        }
//        mBottomNavigation?.setSelect(tabIndex)
//        val fragment = mFragments[tabIndex] as? ArticleFragment<*, *>
//        fragment?.setCurrentTabFromChannelId(channelId)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

}



