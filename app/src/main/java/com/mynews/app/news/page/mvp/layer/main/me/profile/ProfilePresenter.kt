package com.mynews.app.news.page.mvp.layer.main.me.profile

import android.os.Bundle
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Account
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.AccountChangeEvent
import com.mynews.app.news.page.mvp.layer.main.me.profile.edit.ProfileEditFragment
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ProfilePresenter : MVPBasePresenter<ProfileContract.View>(),
        ProfileContract.Presenter<ProfileContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setAccount(AccountManager.account ?: Account())
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickProfile() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_EDIT_PROFILE)
        mView?.goFromRoot(ProfileEditFragment::class.java)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAccountChangeEvent(event: AccountChangeEvent) {
        mView?.setAccount(event.account)
    }


}

