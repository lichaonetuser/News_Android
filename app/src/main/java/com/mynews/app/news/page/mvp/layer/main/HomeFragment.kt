package com.mynews.app.news.page.mvp.layer.main

import android.os.Bundle
import androidx.annotation.DrawableRes
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.page.mvp.layer.main.MainTabEnum.NEWS
import com.mynews.app.news.page.mvp.layer.main.home.ClassificationFragment
import com.mynews.app.news.page.mvp.layer.main.me.MeFragment
import com.mynews.app.news.page.mvp.layer.main.video.VideoFragment
import com.mynews.app.news.widget.navigation.MainBottomNavigationItemView
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.yokeyword.fragmentation.SupportFragment

class HomeFragment : MVPBaseFragment<MainContract.View,
        MainContract.Presenter<MainContract.View>>(),
        MainContract.View {

    override val mPresenter = MainPresenter()
    override val mLayoutRes = R.layout.fragment_home
    override var mDispatchBack = false

    private val mFragments: Array<SupportFragment?> =
            arrayOfNulls(MainTabEnum.values().size)
    private val mBottomTabs: Array<BaseTabItem?> = arrayOfNulls(MainTabEnum.values().size)

    private val mDefaultTab = NEWS
//    private var mBottomNavigation: NavigationController? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        initFragment()
    }

    /************************* 初始化Fragment *************************/

    private fun initFragment() {
        //如果新闻页面找不到，说明还没有初始化过，反之则表明已经有加载
        val newsFragment = findChildFragment(ClassificationFragment::class.java)
        if (newsFragment == null) {
            //首次加载各个页面
            mFragments[MainTabEnum.NEWS.getPosition()] = CoreBaseFragment.instantiate(ClassificationFragment::class.java)
            mFragments[MainTabEnum.VIDEO.getPosition()] = CoreBaseFragment.instantiate(VideoFragment::class.java)
            mFragments[MainTabEnum.ME.getPosition()] = CoreBaseFragment.instantiate(MeFragment::class.java)
            //加载根布局
            loadMultipleRootFragment(R.id.fl_content_home, mDefaultTab.getPosition(), *mFragments)
        } else {
            mFragments[MainTabEnum.NEWS.getPosition()] = newsFragment
            mFragments[MainTabEnum.VIDEO.getPosition()] = findChildFragment(VideoFragment::class.java)
            mFragments[MainTabEnum.ME.getPosition()] = findChildFragment(MeFragment::class.java)
        }
//        if (findFragment(ClassificationFragment::class.java) == null) {
//            loadRootFragment(R.id.container_layout, ClassificationFragment(), false, false)
//        }
    }

    /************************* 下方导航栏相相关逻辑 *************************/

    /**
     * 获取当前选中的Tab
     */
    override fun getCurrentTab(): IMainTab {
        return MainTabEnum.positionOf(0)
    }

    override fun getTabIco(ico: String) {

    }

    /**
     * 切换Tab，注意使用的枚举类
     */
    override fun changeTab(tab: IMainTab) {
    }

    /**
     * 设置是否有反馈
     */
    override fun setHasUnreadFeedback(hasUnread: Boolean) {
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



