package com.mynews.app.news.page.mvp.layer.main.me.lgoin

import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsHelper
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.data.DataDictionary.AccountPlatform
import com.mynews.app.news.data.source.remote.http.url.WebNewsUrls
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.mynews.common.core.CoreApp
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter

class LoginPresenter : MVPBasePresenter<LoginContract.View>(),
        LoginContract.Presenter<LoginContract.View> {

    override fun onClickAgreement() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_TERM)
        mView?.goFromRoot(WebBrowserFragment::class.java,
                WebBrowserPresenterAutoBundle.builder(WebNewsUrls.getUserTermUrl(),
                        ResUtils.getString(R.string.Setting_UserTerms)
                ).bundle())
    }

    override fun onClickFacebook() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_FACEBOOK)
        AccountManager.login(activity = CoreApp.coreBaseActivities.last(),
                accountPlatform = AccountPlatform.FACEBOOK,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

    override fun onClickTwitter() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_TWITTER)
        AccountManager.login(activity = CoreApp.coreBaseActivities.last(),
                accountPlatform = AccountPlatform.TWITTER,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

    override fun onClickGoogle() {
        AnalyticsHelper.logLoginEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.CLICK_LOGIN_GOOGLE)
        AccountManager.login(activity = CoreApp.coreBaseActivities.last(),
                accountPlatform = AccountPlatform.GOOGLE,
                analyticsEventKey = AnalyticsKey.Event.SETTING)
    }

}

