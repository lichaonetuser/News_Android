package com.mynews.common.core.app.activity

abstract class CoreSplashActivity : CoreBaseActivity() {

    protected open var mAnalyticsPageName = javaClass.simpleName

    override fun onResume() {
//        MobclickAgent.onPageStart(mAnalyticsPageName)
        super.onResume()
    }

    override fun onPause() {
//        MobclickAgent.onPageEnd(mAnalyticsPageName)
        super.onPause()
    }

}
