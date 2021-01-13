package com.mynews.app.news.page.activity

import android.app.Fragment
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.WindowManager
import com.mynews.app.news.App
import com.mynews.app.news.R
import com.mynews.app.news.ab.ABManager
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.account.IAccountExtraListener
import com.mynews.app.news.ad.*
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.ExtraInfo
import com.mynews.app.news.bean.HtmlResource
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.debug.DebugTool
import com.mynews.app.news.deeplink.DeferredDeepLinkManager
import com.mynews.app.news.openurl.OpenUrlManager
import com.mynews.app.news.util.*
import com.mynews.app.news.widget.NewsVideoView
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.auto.bundle.bridge.Bridge
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.log.Logger
import com.mynews.common.core.net.http.HttpManager
import com.mynews.common.core.rx.schedulers.computationToMain
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.prompt.PromptManager
import com.mynews.common.extension.share.IShareExtraListener
import com.mynews.common.extension.share.ShareManager
import com.appsflyer.AppsFlyerLib
import com.mynews.app.news.data.source.local.preference.PreferenceAPI
//import com.crashlytics.android.Crashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.mynews.app.news.page.mvp.layer.main.MainFragment
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.keyguardManager
import java.util.concurrent.TimeUnit

class MainActivity : BaseAdActivity() {
    private val TAG_MAIN_LIFE = "MainLife"

    private var mDisposable: Disposable? = null
    private var mSplashAd: IInterstitialAd? = null
    private var mSplashAdTryShowing: Boolean = false
    private var mAdShown: Boolean = false
    private var mOnStartCalled: Boolean = false
    private var mFirstSplashFlag: Boolean = false
    lateinit var Icon :String

    @AutoBundleField(required = false)
    var mShowInterstitialAdFlag = true // 针对首次启动的插屏广告标签

    var mStartLoadSplashTimestamp: Long = 0 // 开始加载splash广告的时间戳

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.no_anim, R.anim.no_anim)
        mSplashAd = AdManager.getSplashAd(this)
        if (savedInstanceState == null) {
            SaveInstanceStateHelper.clear(CoreApp.getInstance())
        } else {
            val bundle = SaveInstanceStateHelper.restore(this)
            if (bundle != null) {
                savedInstanceState.putAll(bundle)
            }
        }
        super.onCreate(savedInstanceState)
        var bundle = this.intent.extras
        if(bundle != null ){
            Icon = bundle.get("Icon") as String
        }
        preInit()
        // 如果有启动广告，展示启动广告，否则正常启动
        if (mShowInterstitialAdFlag && SplashActivity.isInterstitialAdAvailable()) {
            tryShowInterstitialAd()
        } else {
            mShowInterstitialAdFlag = false
            SplashActivity.freeInterstitialAd()
            init()
            hideAdMask()
        }
    }

    private fun preInit() {
        // 加载根布局
        setContentView(R.layout.activity_main)
        val display = windowManager.defaultDisplay
        val point = Point()
        val height = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point)
            if (point.y > EnvDisplayMetrics.HEIGHT_PIXELS) {
                point.y
            } else {
                EnvDisplayMetrics.HEIGHT_PIXELS
            }
        } else {
            EnvDisplayMetrics.HEIGHT_PIXELS
        }
        ad_mask.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                height)

        //处理键盘adjustResize时无效的情况
        AndroidBug5497Workaround.assistActivity(findViewById(android.R.id.content))
    }

    private fun init() {
        initWindow()
        // 总保存状态恢复的时候刷新tag
        if (isRestore()) {
            SessionUtils.tag = MainActivity::class.java.simpleName
        }
        // 清除状态数据
        if (isNotRestore()) {
            Bridge.clearAll(this)
        }
        // 透明状态栏
        useTransparentStatusBar()
        // 防止过度绘制，最底层背景在MainFragment上
        setDecorViewBackgroundColor(Color.TRANSPARENT)
        // 所有Fragment默认均使用透明背景，背景色以XML为准（引用Style）
        setDefaultFragmentBackground(R.color.transparent)
        // 加载根碎片
        if (findFragment(MainFragment::class.java) == null) {
            loadRootFragment(R.id.container_layout, MainFragment(Icon), false, false)
        }

        // 处理推送
        // 这个逻辑只有在真冷启的时候进行
        // 如果是从因为跳转并且是保存状态恢复的话会同时触发onCreate和onNewIntent
        // 这个时候在onNewIntent中处理这些逻辑，防止相同逻辑被连续处理两次
        if (isNotRestore()) {
            mDisposable = Completable.fromCallable { }
                    .delay(1, TimeUnit.SECONDS)
                    .computationToMain<Unit>()
                    .subscribeBy(
                            onComplete = {
                                // 处理推送
                                checkPush(intent)
                                // 处理深度连接
                                checkDeepLink(intent)
                            },
                            onError = {}
                    )
        }
        // 检查Prompt
        PromptManager.loadPrompt(this, DataManager.Remote.getPrompt(), { id ->
            AnalyticsManager.logEvent(AnalyticsKey.Event.APP_ALERT, String.format(AnalyticsKey.Parameter.APP_ALERT_SHOW_ID, id))
        }, { id, index, openUrl ->
            AnalyticsManager.logEvent(AnalyticsKey.Event.APP_ALERT, String.format(AnalyticsKey.Parameter.APP_ALERT_CLICK_ID_BUTTONX, id, index))
            OpenUrlManager.checkOpenUrl(openUrl)
        })

        // 更新配置资源
        updateAppConfigResource()
        // 同步登录状态
        syncProfileStatus()
        // 延迟深度连接侦测
        DeferredDeepLinkManager.detectDeepLink()
        // 上报最近进程
        // uploadDeviceAppInfo()
        // AppsFlyerLib追踪
        if (AppsFlyerLib.getInstance().isTrackingStopped) {
            AppsFlyerLib.getInstance().stopTracking(false, CoreApp.getInstance())
        }
        // 设置分享时阻止广告逻辑
        ShareManager.mIShareExtraListener = object : IShareExtraListener {
            override fun onPreShare() {
                AdManager.setAdBlockFlag(true)
            }

            override fun onFinishShare() {
                AdManager.setAdBlockFlagDelay(false, 333)
            }
        }
        // 设置登录时阻止广告逻辑
        AccountManager.mIAccountExtraListener = object : IAccountExtraListener {
            override fun onPreLogin() {
                AdManager.setAdBlockFlag(true)
            }

            override fun onFinishLogin() {
                AdManager.setAdBlockFlagDelay(false, 333)
            }
        }
        AdManager.setAdBlockFlag(false)
    }

    private fun initWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    }

    private fun requestDismissKeyguard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager.requestDismissKeyguard(this, null)
        }
    }

    private fun updateAppConfigResource() {
        DataManager.Remote.getAppConfigResource()
                .io()
                .subscribeBy(
                        onNext = {
                            var css = ""
                            var js = ""
                            var jsLang = ""

                            val cssInfo = DataManager.Local.getArticleTransCodeCssInfo()
                            val jsInfo = DataManager.Local.getArticleTransCodeJsInfo()
                            val jsLangInfo = DataManager.Local.getArticleTransCodeJsLangInfo()

                            //如果有一个url不为null的资源下载或MD5校验失败，则不更新
                            if (it.css.url.isNotBlank() && it.css.md5 != cssInfo.md5) {
                                css = downloadNewAppConfigResource(it.css)
                                if (css.isBlank()) {
                                    Logger.d("updateJS_download css fail")
                                    return@subscribeBy
                                }
                                Logger.d("updateJS_download css success")
                            }
                            if (it.js.url.isNotBlank() && it.js.md5 != jsInfo.md5) {
                                js = downloadNewAppConfigResource(it.js)
                                if (js.isBlank()) {
                                    Logger.d("updateJS_download js fail")
                                    return@subscribeBy
                                }
                                Logger.d("updateJS_download js success")
                            }
                            if (it.jsLang.url.isNotBlank() && it.jsLang.md5 != jsLangInfo.md5) {
                                jsLang = downloadNewAppConfigResource(it.jsLang)
                                if (jsLang.isBlank()) {
                                    Logger.d("updateJS_download jsLang fail")
                                    return@subscribeBy
                                }
                                Logger.d("updateJS_download jsLang success")
                            }

                            //更新url不为null的资源
                            //此方法中含有并发锁防止极端情况
                            AppConfigResourceUtils.updateHtmlResource(it, css, js, jsLang)
                        },
                        onComplete = {
                            Logger.d("updateJS_Run updateAppConfigResource complete!")
                        },
                        onError = { error ->
                            Logger.d("updateJS_Run updateAppConfigResource error: $error")
                        })
    }

    private fun syncProfileStatus() {
        if (!AccountManager.isLogin()) {
            return
        }
        DataManager.Remote.syncProfileStatus()
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            Logger.d("sync profile status code : ${it.profileStatus.code}")
                            when (it.profileStatus.code) {
                                DataDictionary.ProfileStatus.OK.value -> return@subscribeBy
                                else -> {
                                    AccountManager.logout(activity = this,
                                            showTip = false,
                                            analyticsEventKey = AnalyticsKey.Event.SETTING)
                                    if (it.profileStatus.tip.isBlank()) {
                                        ToastUtils.showToast(ResUtils.getString(R.string.Tip_OverDateReLogin))
                                    } else {
                                        ToastUtils.showToast(it.profileStatus.tip)
                                    }
                                }
                            }
                        },
                        onError = {

                        }
                )
    }

    // 失败时返回""
    private fun downloadNewAppConfigResource(res: HtmlResource): String {
        try {
            return DataManager.Remote
                    .download(res.url)
                    .map {
                        val downloadBytes = it.bytes()
                        val downloadMd5 = MD5Utils.bytesToHex(MD5Utils.md5(downloadBytes))
                        if (res.md5 == downloadMd5) {
                            String(downloadBytes)
                        } else {
                            ""
                        }
                    }
                    .onErrorReturn { "" }
                    .blockingFirst()
        } catch (e: Exception) {
            return ""
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 处理推送
        checkPush(intent)
        // 处理深度连接
        checkDeepLink(intent)
    }

    private fun checkPush(intent: Intent?) {
        try {
            Logger.d("mPush :${intent?.extras}")
            val pushOpenUrl: String? = intent?.extras?.getString("mPushOpenUrl")
            Logger.d("mPush :$pushOpenUrl")
            if (!pushOpenUrl.isNullOrBlank()) {
                OpenUrlManager.checkOpenUrl(this, pushOpenUrl ?: return)
            }
            val lockActivity = CoreApp.coreBaseActivities.find {
                it::class.java == LockScreenDialogActivity::class.java
            }
            lockActivity?.finish()
        } catch (e: Exception) {
//            Crashlytics.logException(e)
        }
    }

    private fun checkDeepLink(intent: Intent?) {
        Logger.d("DeepLink :${intent?.data}")
        val uri = intent?.data ?: return
        val openUrl: String = uri.toString()
        Logger.d("DeepLink :$openUrl")
        if (openUrl.isNotBlank()) {
            OpenUrlManager.checkOpenUrl(this, openUrl)
        }
    }

    override fun onStart() {
        super.onStart()
        runOnStart()
    }

    private fun runOnStart() {
        if (mShowInterstitialAdFlag) {
            return
        }
        Logger.tag(TAG_MAIN_LIFE).d("onStart")
        if (SessionUtils.tag != null) {
            HttpManager.putCommonParams(SessionUtils.getSessionPair())
        } else {
            SessionUtils.tag = MainActivity::class.java.simpleName
        }
        if (App.isDebug()) {
            DebugTool.startDetector()
        }
        mOnStartCalled = true
    }

    var isPaused = false
    private var lastFetchConfigTime = System.currentTimeMillis()
    override fun onStop() {
        super.onStop()
        if (mShowInterstitialAdFlag) {
            return
        }
        Logger.tag(TAG_MAIN_LIFE).d("onStop")
        if (App.isDebug()) {
            DebugTool.stopDetector()
        }
        saveArticleContentCacheToDisk()

        val now = System.currentTimeMillis()
        if ((now - lastFetchConfigTime) > 2 * 60 * 1000) {
            //从远程获取更新
            DataManager.Remote.getAppConfig().io().subscribeBy(
                    onNext = {
                        DataManager.Local.saveAppConfig(it) //更新本地
                        ABManager.updateABInfo(it.abInfo)
                    },
                    onError = {
                        Logger.e(it)
//                        Crashlytics.logException(it)
                    }
            )
            lastFetchConfigTime = now
        }
    }

    override fun onResume() {
        super.onResume()
        if (mShowInterstitialAdFlag) {
            return
        }
        Logger.tag(TAG_MAIN_LIFE).d("onResume")
        if (!mSplashAdTryShowing) {
            GSYVideoManager.onResume()
        }
        if (shouldSendLogResume) {
            DataManager.Remote.reportDau(0).io().subscribe()
        }
        shouldSendLogResume = true
        isPaused = false
        tryShowSplashAd(false, loadAd = mFirstSplashFlag)
        mFirstSplashFlag = true
        // 当我们既没有登录也没有分享时，并且之前做过跳转，才恢复广告
        if (App.getInstance().mIsAdBlockFlagByStartActivity &&
                !ShareManager.mIsSharing &&
                !AccountManager.mIsDoLogin) {
            AdManager.setAdBlockFlag(false)
        }
    }

    override fun onPause() {
        isPaused = true
        mOnStartCalled = false
        if (!AdManager.isAdBlocking()) {
            showAdMask()
        }
        if (App.isDebug() && DebugTool.mAdShowId && AdManager.isAdBlocking()) {
            ToastUtils.showNewToast("MAIN AD IS BLOCKING!")
        }
        super.onPause()
        if (mShowInterstitialAdFlag) {
            return
        }
        Logger.tag(TAG_MAIN_LIFE).d("onPause")
        GSYVideoManager.onPause()
        if (shouldSendLogPause) {
            submitPushToken()
            AppLogManager.sendAppLog()
            DataManager.Remote.reportDau(1).io().subscribe()
        }
        shouldSendLogPause = true
        tryRefreshSplashAd()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mShowInterstitialAdFlag) {
            return
        }
        GSYVideoManager.releaseAllVideos() // 主界面销毁的时候释放所有视频
        if (false == mDisposable?.isDisposed) {
            mDisposable?.dispose()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ShareManager.handleActivityResult(requestCode, resultCode, data ?: Intent())
        AccountManager.handleActivityResult(requestCode, resultCode, data ?: Intent())
    }

    override fun onBackPressedSupport() {
        if (NewsVideoView.FullScreen.backFromWindowFull(this)) {
            return
        }
        if (supportFragmentManager.backStackEntryCount > 1) {
            pop()
        } else {
            this.finish()
//            QuitUtil.quit(this)
        }
    }

    private fun submitPushToken() {
        try {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                val deviceToken = instanceIdResult.token
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
                DataManager.submitPushToken(deviceToken)
            }
        } catch (e: Exception) {
            Logger.e(e)
//            Crashlytics.logException(e)
        }

    }

    private fun saveArticleContentCacheToDisk() {
        Completable.fromCallable {
            DataManager.Cache.transferArticleContentCacheToDisk()
        }.io().subscribeBy(
                onComplete = {
                    Logger.d("saveArticleContentCacheToDisk Success")
                },
                onError = {
                    Logger.d("saveArticleContentCacheToDisk Fail")
                }
        )
    }

    private fun tryShowSplashAd(withPushIntent: Boolean, loadAd: Boolean = true) {
        mSplashAdTryShowing = false
        // 没有配置项，跳过
        if (mSplashAd == null) {
            hideAdMask()
            GSYVideoManager.onResume()
            return
        }
        val analyticsEventKey = "${mSplashAd?.getAdSourceEna()?.value
                ?: ""}_${AnalyticsKey.Event.SPLASH_AD}"

        // 已加载打开，未加载读取
        if (mSplashAd?.isLoaded() == true) {
            /**
             * 当前有广告正在显示
             */
            if (mAdShown) {
                return
            }

            /**
             * 两次展示的最小间隔时间小于设置间隔，不展示
             */
            val lastShowInterstitialAdTimestamp = PreferenceAPI.getLastShowInterstitialAdTimestamp()
            val minShowInterval: Long = (System.currentTimeMillis() - lastShowInterstitialAdTimestamp) / 1000
            if (minShowInterval <= AdManager.getConfig().splash.minShowInterval && lastShowInterstitialAdTimestamp > 0) {
                hideAdMask()
                return
            }

            /**
             * 调用前没有调用过onStart方法
             * 说明这不是一个界面性质的切换（比如弹窗）
             */
            if (!mOnStartCalled) {
                hideAdMask()
                return
            }

            /**
             * 广告被特殊逻辑阻止，不展示
             */
            if (AdManager.isAdBlocking()) {
                hideAdMask()
                return
            }

            mSplashAdTryShowing = true // 开始尝试展示广告
            mSplashAd?.setAdListener(object : IAdListener {
                override fun onAdImpression() {
                    AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = AppLogKey.CL.IMPRESSION))
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.IMPRESSION)
                }

                override fun onAdFailedToLoad(error: AdError) {
                    val duration = (System.currentTimeMillis() - mStartLoadSplashTimestamp) / 1000
                    val durationStr =
                            if (duration > 10) {
                                "10+"
                            } else duration.toString()
                    AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = "${AppLogKey.CL.LOAD_FAIL_DURATION_}$durationStr"))
                    AnalyticsManager.logEvent(analyticsEventKey, "${AppLogKey.CL.LOAD_FAIL_DURATION_}$durationStr")
                    if (error.code == AdManager.ERROR_CODE_REQUEST_TIMEOUT) {
                        AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = AppLogKey.CL.LOAD_TIMEOUT))
                        AnalyticsManager.logEvent(analyticsEventKey, AppLogKey.CL.LOAD_TIMEOUT)
                    } else {
                        val cl = String.format("${AppLogKey.CL.LOAD_FAIL_STH_CODE_}${error.code}", mSplashAd?.getAdSourceEna()?.value
                                ?: "unknown")
                        AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = cl))
                        AnalyticsManager.logEvent(analyticsEventKey, cl)
                    }
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.REQUEST_FAIL)
                }

                override fun onAdFailedToShow(error: AdError) {
                    onAdClosed()
                }

                override fun onAdLoaded(any: Any?) {
                    val duration = (System.currentTimeMillis() - mStartLoadSplashTimestamp) / 1000
                    val durationStr =
                            if (duration > 10) {
                                "10+"
                            } else duration.toString()
                    AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = "${AppLogKey.CL.LOAD_DURATION_}$durationStr"))
                    AnalyticsManager.logEvent(analyticsEventKey, "${AppLogKey.CL.LOAD_DURATION_}$durationStr")
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.REQUEST_DONE)
                }

                override fun onAdClicked() {
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.CLICKED)
                }

                override fun onAdOpened() {
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.SHOW)
                    mAdShown = true
                    ad_mask?.postDelayed({
                        hideAdMask()
                    }, 333)
                }

                override fun onAdClosed() {
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.CLOSE)
                    hideAdMask()
                    if (withPushIntent) {
                        // 处理推送
                        checkPush(intent)
                        // 处理深度连接
                        checkDeepLink(intent)
                    }
                    GSYVideoManager.onResume()
                    AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.PRELOAD)
                    mStartLoadSplashTimestamp = System.currentTimeMillis()
                    mSplashAd?.loadAd()
                    mAdShown = false
                }
            })

            /*
             * 延迟100毫秒处理，因为直接运行admob广告可能无法显示
             * 内部有一个仿GMS检查的安全逻辑
             * 如果还是无法显示，那么回调onAdFailedToShow取消这次展示
             */
            mSplashAd?.show()
            PreferenceAPI.saveLastShowInterstitialAdTimestamp(System.currentTimeMillis())
        } else {
            hideAdMask()
            if (loadAd) {
                tryRefreshSplashAd()
            }
        }
    }

    private fun tryRefreshSplashAd() {
        if (mSplashAd?.isLoaded() == true) {
            return
        }
        if (mSplashAd?.isLoading() == true) {
            return
        }
        val analyticsEventKey = "${mSplashAd?.getAdSourceEna()?.value
                ?: ""}_${AnalyticsKey.Event.SPLASH_AD}"
        AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.PRELOAD)
        mStartLoadSplashTimestamp = System.currentTimeMillis()
        mSplashAd?.loadAd()
    }

    private fun tryShowInterstitialAd() {
        val ad = SplashActivity.sInterstitialAd
        if (ad == null) {
            SplashActivity.freeInterstitialAd()
            return
        }
        val analyticsEventKey = "${ad.getAdSourceEna().value}_${AnalyticsKey.Event.INTERSTITIAL_AD}"
        ad.setAdListener(object : IAdListener {
            override fun onAdImpression() {
                AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = AppLogKey.CL.IMPRESSION))
                AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.IMPRESSION)
            }

            override fun onAdFailedToLoad(error: AdError) {
            }

            override fun onAdFailedToShow(error: AdError) {
                onAdClosed()
            }

            override fun onAdLoaded(any: Any?) {
            }

            override fun onAdClicked() {
                AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.CLICKED)
            }

            override fun onAdOpened() {
                AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.SHOW)
                mAdShown = true
                mShowInterstitialAdFlag = false
                init()
                hideAdMask()
            }

            override fun onAdClosed() {
                AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.DISMISS)
                SplashActivity.freeInterstitialAd()
                runOnStart()
                mAdShown = false
            }
        })
        ad.show()
    }

    private fun hideAdMask() {
        ad_mask.alpha = 0f
    }

    private fun showAdMask() {
        // 没有配置项，没必要展示
        if (mSplashAd == null) {
            hideAdMask()
            return
        }
        ad_mask.alpha = 1f
    }

    // 广告特殊处理
    /**
     * 我们在跳转其它页面的时候不做广告显示
     * 体验太差
     * 其实重写startActivityForResult就可以，保险都写了吧
     */
    override fun startActivity(intent: Intent?) {
        AdManager.setAdBlockFlag(true)
        App.getInstance().mIsAdBlockFlagByStartActivity = true
        super.startActivity(intent)
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        AdManager.setAdBlockFlag(true)
        App.getInstance().mIsAdBlockFlagByStartActivity = true
        super.startActivityForResult(intent, requestCode)
    }

    override fun startActivityFromFragment(fragment: androidx.fragment.app.Fragment?, intent: Intent?, requestCode: Int, options: Bundle?) {
        AdManager.setAdBlockFlag(true)
        App.getInstance().mIsAdBlockFlagByStartActivity = true
        super.startActivityFromFragment(fragment, intent, requestCode, options)
    }

    override fun startActivityFromFragment(fragment: Fragment, intent: Intent?, requestCode: Int, options: Bundle?) {
        AdManager.setAdBlockFlag(true)
        App.getInstance().mIsAdBlockFlagByStartActivity = true
        super.startActivityFromFragment(fragment, intent, requestCode, options)
    }

    // 广告特殊处理

    companion object {
        var shouldSendLogResume = true
        var shouldSendLogPause = true
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val bundle = outState ?: return
        val size = SaveInstanceStateHelper.save(this, bundle)
        if (size > 2048) {
            SaveInstanceStateHelper.clear(this)
//            Crashlytics.logException(Exception("onSaveInstanceState too large.size : $size"))
        }
        outState.clear()
    }

//    private fun uploadDeviceAppInfo() {
//        //1小时内不重新上报
//        val lastUploadTime = DataManager.Local.getLastUploadDeviceAppInfoTimeAll()
//        if (System.currentTimeMillis() - lastUploadTime < 60 * 60 * 1000) {
//            return
//        }
//        //没有UDID不上报
//        if (UDIDUtils.getUniqueDeviceId().isEmpty()) {
//            return
//        }
//        Single.fromCallable { DeviceAppManager.getDeviceAppRequestInfo(true) }
//                .flatMapObservable { DataManager.Remote.postDeviceAppMap(it) }
//                .io()
//                .subscribeBy(
//                        onNext = {
//                            DataManager.Local.saveLastUploadDeviceAppInfoTimeAll(System.currentTimeMillis())
//                        },
//                        onError = {
//                            Logger.e("uploadDeviceAppInfo(has label) fail : $it")
//                        })
//    }
}
