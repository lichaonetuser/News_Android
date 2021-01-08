package com.mynews.app.news.page.mvp.layer.main.dialog.login

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsHelper
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataDictionary.AccountPlatform
import com.mynews.app.news.data.source.remote.http.url.WebNewsUrls
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.login.LoginEvent
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.mynews.common.core.CoreApp
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter
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

