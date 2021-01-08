package com.mynews.app.news.page.mvp.layer.main.me

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.RelativeLayout.ALIGN_BOTTOM
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.bean.InboxCountResponse
import com.mynews.app.news.page.mvp.layer.main.me.lgoin.LoginFragment
import com.mynews.app.news.page.mvp.layer.main.me.profile.ProfileFragment
import com.mynews.app.news.page.mvp.layer.main.setting.SettingFragment
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.extension.app.mvp.base.MVPBaseFragment
import kotlinx.android.synthetic.main.fragment_me.*

class MeFragment : MVPBaseFragment<MeContract.View,
        MeContract.Presenter<MeContract.View>>(),
        MeContract.View {

    override val mPresenter = MePresenter()
    override val mLayoutRes = R.layout.fragment_me
    override var mDispatchBack = false

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        if (AccountManager.isLogin()) {
            showProfile()
        } else {
            showLogin()
        }
        loadRootFragment(R.id.container_layout_setting,
                CoreBaseFragment.instantiate(SettingFragment::class.java),
                false, false)
        favorite_btn.setOnClickListener {
            mPresenter.onClickCollect()
        }
        my_comment_btn.setOnClickListener {
            mPresenter.onClickMyComment()
        }
        message_btn.setOnClickListener {
            mPresenter.onClickMessage()
            message_remind_ic.visibility = View.GONE
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setInboxUnreadCount(countResponse: InboxCountResponse) {
        val totalCount = countResponse.messageCount + countResponse.pushHistoryCount
        message_remind_ic.visibility = if (totalCount > 0) View.VISIBLE else View.GONE
    }

    override fun showLogin() {
        val fragment = findChildFragment(ProfileFragment::class.java)
        if (fragment != null) {
            fragment.replaceFragment(CoreBaseFragment.instantiate(LoginFragment::class.java), false)
        } else if (findChildFragment(LoginFragment::class.java) == null) {
            loadRootFragment(R.id.container_layout_profile,
                    CoreBaseFragment.instantiate(LoginFragment::class.java),
                    false, false)
        }
        val params = profile_bg.layoutParams as? RelativeLayout.LayoutParams
        params?.addRule(ALIGN_BOTTOM, R.id.container_layout_profile)
    }

    override fun showProfile() {
        val fragment = findChildFragment(LoginFragment::class.java)
        if (fragment != null) {
            fragment.replaceFragment(CoreBaseFragment.instantiate(ProfileFragment::class.java), false)
        } else if (findChildFragment(ProfileFragment::class.java) == null) {
            loadRootFragment(R.id.container_layout_profile,
                    CoreBaseFragment.instantiate(ProfileFragment::class.java),
                    false, false)
        }
        val params = profile_bg.layoutParams as? RelativeLayout.LayoutParams
        params?.addRule(ALIGN_BOTTOM, R.id.container_layout_user)
    }

}


