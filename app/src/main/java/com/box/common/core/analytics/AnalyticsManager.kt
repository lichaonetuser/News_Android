package com.box.common.core.analytics

import android.text.TextUtils
import com.box.app.news.BuildConfig
import com.box.app.news.R
import com.box.app.news.util.UDIDUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.platform.AppsFlyerAnalytics
import com.box.common.core.analytics.platform.FirebaseAnalytics
import com.box.common.core.analytics.platform.UmengAnalytics
import com.box.common.core.log.Logger
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.firebase.perf.metrics.AddTrace
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

object AnalyticsManager {

    var defaultOnAppsFlyer = false
    var defaultOnUmeng = true
    var defaultOnFirebase = true

    @AddTrace(name = "InitAnalyticsManager", enabled = true)
    fun init(onAppsFlyer: Boolean = false,
             onUmeng: Boolean = true,
             onFirebase: Boolean = true) {
        this.defaultOnAppsFlyer = onAppsFlyer
        this.defaultOnUmeng = onUmeng
        this.defaultOnFirebase = onFirebase
        initFirebase()
        initAppsFlyer()
        initUmeng()
    }

    fun initFirebase() {
        try {
            com.google.firebase.analytics.FirebaseAnalytics.getInstance(CoreApp.getInstance())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initAppsFlyer() {
        AppsFlyerLib.getInstance().waitForCustomerUserId(false)
        AppsFlyerLib.getInstance().init(CoreApp.getInstance().getString(R.string.appsflyer_dev_key), object : AppsFlyerConversionListener {
            override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
            override fun onAttributionFailure(p0: String?) {}
            override fun onInstallConversionDataLoaded(p0: MutableMap<String, String>?) {}
            override fun onInstallConversionFailure(p0: String?) {}
        }, CoreApp.getInstance())
        val udid = UDIDUtils.getUniqueDeviceId()
        if (!TextUtils.isEmpty(udid)) {
            AppsFlyerAnalytics.setCustomerUserId(udid)
        }
        AppsFlyerLib.getInstance().startTracking(CoreApp.getInstance())
    }

    fun initUmeng() {
        UMConfigure.init(CoreApp.getInstance(), UMConfigure.DEVICE_TYPE_PHONE, null)
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO) //使用旧版的自动模式兼容
        MobclickAgent.setCatchUncaughtExceptions(false) //关闭错误统计
        UMConfigure.setLogEnabled(BuildConfig.BUILD_TYPE != "release") //是否输出logcat
        UMConfigure.setEncryptEnabled(true) //是否加密传输
//        MobclickAgent.setScenarioType(CoreApp.getInstance(), MobclickAgent.EScenarioType.E_UM_NORMAL)
//        MobclickAgent.openActivityDurationTrack(false) //关闭ActivityDuration追踪以追踪Fragment时长和跳转
    }

    fun logEvent(event: String, vararg parameters: String,
                 onAppsFlyer: Boolean = defaultOnAppsFlyer,
                 onUmeng: Boolean = defaultOnUmeng,
                 onFirebase: Boolean = defaultOnFirebase) {
        if (event.isBlank()) {
            return
        }

        if (onAppsFlyer) {
            AppsFlyerAnalytics.logEvent(event, *parameters)
        }
        if (onUmeng) {
            UmengAnalytics.logEvent(event, *parameters)
        }
        if (onFirebase) {
            FirebaseAnalytics.logEvent(event, *parameters)
        }
        if (parameters.isEmpty()) {
            Logger.tag("LOG_EVENT").d("event : $event\nplatform : onAppsFlyer = $onAppsFlyer | onUmeng = $onUmeng | onFirebase = $onFirebase")
        } else {
            parameters.forEach {
                Logger.tag("LOG_EVENT").d("event : $event | parameter : $it\nplatform : onAppsFlyer = $onAppsFlyer | onUmeng = $onUmeng | onFirebase = $onFirebase")
            }
        }
    }

    fun logEvent(event: String, parameter: String, value: String,
                 onAppsFlyer: Boolean = defaultOnAppsFlyer,
                 onUmeng: Boolean = defaultOnUmeng,
                 onFirebase: Boolean = defaultOnFirebase) {
        if (event.isBlank()) {
            return
        }

        if (onAppsFlyer) {
            AppsFlyerAnalytics.logEvent(event, parameter, value)
        }
        if (onUmeng) {
            UmengAnalytics.logEvent(event, parameter, value)
        }
        if (onFirebase) {
            FirebaseAnalytics.logEvent(event, parameter, value)
        }
        Logger.tag("LOG_EVENT").d("event : $event | parameters : $parameter | value : $value\nplatform : onAppsFlyer = $onAppsFlyer | onUmeng = $onUmeng | onFirebase = $onFirebase")
    }
}