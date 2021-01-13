package com.mynews.app.news

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import com.mynews.app.news.ad.AdManager
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.remote.http.url.HttpBaseUrls
import com.mynews.app.news.debug.DebugTool
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.processor.EventBusIndex
import com.mynews.app.news.util.*
import com.mynews.app.news.video.VideoManager
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.crash.CrashManager
import com.mynews.common.core.debug.DebugManager
import com.mynews.common.core.environment.EnvCoreHttpCommon
import com.mynews.common.core.environment.EnvIOSApp
import com.mynews.common.core.environment.EnvUnique
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.log.LogManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.net.http.HttpManager
import com.mynews.common.core.net.http.interceptor.HttpUserAgentInterceptor
import com.mynews.common.core.twitter.TwitterManager
import com.mynews.common.core.util.ResUtils
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.AddTrace
import com.kongzue.dialog.v2.DialogSettings
import com.kongzue.dialog.v2.DialogSettings.STYLE_IOS
import com.kongzue.dialog.v2.DialogSettings.THEME_LIGHT
import com.pixplicity.easyprefs.library.Prefs
import okhttp3.OkHttpClient

class App : CoreApp() {

    companion object {
        fun isDebug() = true
        fun getInstance() = CoreApp.getInstance() as App
    }

    @AddTrace(name = "AppOnCreateTrace", enabled = true)
    override fun onCreate() {
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            FirebasePerformance.getInstance().isPerformanceCollectionEnabled = true
        }
        super.onCreate()
        Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(packageName)
                .setUseDefaultSharedPreference(true)
                .build()
    }

    override fun init() {
        super.init()
        /**
         * 注意各模块初始化之间的依赖关系和顺序
         * 有时间编写为build模式保证初始化顺序的依赖安全性
         */
        LogManager.init()
        CrashManager.init(enable = true)
        AnalyticsManager.init(onFirebase = true, onUmeng = true, onAppsFlyer = false)
        GoogleServiceUtils.analyticsGoogleServiceMissing()

        HttpManager.init(baseUrl = HttpBaseUrls.getReleaseUrl(),
                commonParams = hashMapOf(
                        *EnvCoreHttpCommon.toPairArray(),
                        *EnvUnique.toPairArray(),
                        *EnvIOSApp.toPairArray(),
                        *LocationUtils.getLocationPairs(),
                        TextSizeUtils.getTextSizePair(),
                        UDIDUtils.getUniqueDeviceIdPair(),
                        AdvertisingIdUtils.getAdvertisingIdPair(),
                        SessionUtils.getSessionPair(),
                        NetWorkAccessUtils.getNetWorkAccess(),
                        CityUtils.getCityCodePair()))
        UIDUtils.updateUIdToHttpParams()
        HttpManager.setBaseOkHttpClient { builder: OkHttpClient.Builder ->
            builder.addNetworkInterceptor(HttpUserAgentInterceptor("${EnvIOSApp.appname}_${EnvIOSApp.v}"))
                    .build()
        }
        AdvertisingIdUtils.updateAdvertisingIdToHttpParams()

        ImageManager.init()
        EventManager.init(subscriberInfoIndex = EventBusIndex())
        TwitterManager.init(BuildConfig.DEBUG)
        VideoManager.init()
        DebugManager.init(enable = BuildConfig.DEBUG)
        DataManager.init()
        AdManager.initZcoup(this, getString(R.string.zcoup_slot_id))
//        AdManager.initVungle()
        AdManager.initJumpRaw(this, getString(R.string.jumpraw_slot_id), ResUtils.getString(R.string.app_name))

        initIOSDialogStyle()
        DebugTool.init()

        Logger.d("App init finish")

//        注册监听网络变化的广播
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED")
        filter.addAction("android.net.wifi.STATE_CHANGE")
        registerReceiver(NetworkConnectChangedReceiver(), filter)
    }

    fun initIOSDialogStyle() {
        DialogSettings.use_blur = false
        DialogSettings.style = STYLE_IOS
        DialogSettings.tip_theme = THEME_LIGHT
    }


    // 广告特殊处理
    /**
     * 我们在跳转其它页面的时候不做广告显示
     * 体验太差
     */
    var mIsAdBlockFlagByStartActivity = false

    override fun startActivity(intent: Intent?) {
        AdManager.setAdBlockFlag(true)
        mIsAdBlockFlagByStartActivity = true
        super.startActivity(intent)
    }
    // 广告特殊处理

}
