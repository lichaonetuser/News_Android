package com.box.app.news

import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import com.box.app.news.ad.AdManager
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.remote.http.url.HttpBaseUrls
import com.box.app.news.debug.DebugTool
import com.box.app.news.event.EventManager
import com.box.app.news.processor.EventBusIndex
import com.box.app.news.util.*
import com.box.app.news.video.VideoManager
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.crash.CrashManager
import com.box.common.core.debug.DebugManager
import com.box.common.core.environment.EnvCoreHttpCommon
import com.box.common.core.environment.EnvIOSApp
import com.box.common.core.environment.EnvUnique
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.log.LogManager
import com.box.common.core.log.Logger
import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.interceptor.HttpUserAgentInterceptor
import com.box.common.core.twitter.TwitterManager
import com.box.common.core.util.ResUtils
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
        fun isDebug() = BuildConfig.DEBUG
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
