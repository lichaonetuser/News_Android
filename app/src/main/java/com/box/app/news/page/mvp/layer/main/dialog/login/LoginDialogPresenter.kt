package com.box.app.news.page.mvp.layer.main.dialog.login

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsHelper
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.data.DataDictionary.AccountPlatform
import com.box.app.news.data.source.remote.http.url.WebNewsUrls
import com.box.app.news.event.EventManager
import com.box.app.news.event.login.LoginEvent
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.box.common.core.CoreApp
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogPresenter
import me.yokeyword.fragmentation.anim.FragmentAnimator
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginDialogPresenter : MVPDialogPresenter<LoginDialogContract.View>(),
        LoginDialogContract.Presenter<LoginDialogContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    override fun onClickAgreement() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_TERM)
        mView?.goFromRoot(clazz = WebBrowserFragment::class.java,
                bundle = WebBrowserPresenterAutoBundle.builder(WebNewsUrls.getUserTermUrl(),
                        ResUtils.getString(R.string.Setting_UserTerms)).bundle(),
                hideSelf = false)
    }

    override fun onClickLoginFacebook() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_FACEBOOK)
        AccountManager.login(activity = CoreApp.activities.last(),
                accountPlatform = AccountPlatform.FACEBOOK,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

    override fun onClickLoginTwitter() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_TWITTER)
        AccountManager.login(activity = CoreApp.activities.last(),
                accountPlatform = AccountPlatform.TWITTER,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

    override fun onClickLoginGoogle() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_GOOGLE)
        AccountManager.login(activity = CoreApp.activities.last(),
                accountPlatform = AccountPlatform.GOOGLE,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

    override fun onClickClose() {
        mView?.back()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        if (event.success) {
            mView?.back()
        }
    }

}

