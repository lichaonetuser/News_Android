package com.mynews.app.news.page.mvp.layer.main

import android.os.Bundle
import androidx.annotation.DrawableRes
import android.view.View
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.MainTabEnum.NEWS
import com.mynews.app.news.page.mvp.layer.main.article.ArticleFragment
import com.mynews.app.news.page.mvp.layer.main.me.MeFragment
import com.mynews.app.news.page.mvp.layer.main.video.VideoFragment
import com.mynews.app.news.widget.navigation.MainBottomNavigationItemView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_main.*
import me.majiajie.pagerbottomtabstrip.NavigationController
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
import me.yokeyword.fragmentation.SupportFragment

class MainFragment : MVPBaseFragment<MainContract.View,
        MainContract.Presenter<MainContract.View>>(),
        MainContract.View {

    override val mPresenter = MainPresenter()
    override val mLayoutRes = R.layout.fragment_main
    override var mDispatchBack = false

    private val mFragments: Array<SupportFragment?> =
            arrayOfNulls(MainTabEnum.values().size)
//    private val mBottomTabs: Array<BaseTabItem?> = arrayOfNulls(MainTabEnum.values().size)

    private val mDefaultTab = NEWS
//    private var mBottomNavigation: NavigationController? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initFragment()
        initBottomNavigationBar()
    }

    /************************* 初始化Fragment *************************/

    private fun initFragment() {
        //如果新闻页面找不到，说明还没有初始化过，反之则表明已经有加载
        val newsFragment = findChildFragment(ArticleFragment::class.java)
        if (newsFragment == null) {
            //首次加载各个页面
            mFragments[MainTabEnum.NEWS.getPosition()] = CoreBaseFragment.instantiate(ArticleFragment::class.java)
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



