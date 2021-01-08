package com.mynews.app.news.page.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.mynews.app.news.App
import com.mynews.app.news.ad.*
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.ExtraInfo
import com.mynews.app.news.bean.Interest
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.local.LocalKeys
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.UDIDUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.analytics.platform.AppsFlyerAnalytics
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.rx.schedulers.obOnIo
import com.mynews.common.core.rx.schedulers.obOnMain
import com.crashlytics.android.Crashlytics
import com.pixplicity.easyprefs.library.Prefs
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.yatatsu.autobundle.AutoBundle
import com.yatatsu.autobundle.AutoBundleField
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy

class SplashActivity : BaseAdActivity() {

    private var mIdDisposable: Disposable? = null
    @AutoBundleField(required = false)
    var mBreakStartMain = true
    @AutoBundleField(required = false)
    var mPushOpenUrl: String? = null

    var mStartLoadInterstitialTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /**
         * 全局上下文不可用，理论上不应出现此情况
         * 如果出现的话尝试恢复,失败的场合关闭进程
         */
        if (!CoreApp.isApplicationAvailability()) {
            when {
                application is App -> {
                    (application as App).init()
                    Crashlytics.logException(Exception("CoreApp is NOT AVAILABILITY.But reset application init success."))
                }
                applicationContext is App -> {
                    (applicationContext as App).init()
                    Crashlytics.logException(Exception("CoreApp is NOT AVAILABILITY.But reset applicationContext init success."))
                }
                else -> {
                    Fabric.with(this, Crashlytics())
                    Crashlytics.logException(Exception("CoreApp is NOT AVAILABILITY.Can not launch App."))
                    finish()
                    android.os.Process.killProcess(android.os.Process.myPid())
                    return
                }
            }
        }

        AdManager.refreshAdConfig() //更新广告配置

        /**
         * 通过判断Intent的Bundle有无Push对象判断是否是push跳转
         * 如果是Push跳转
         */
        if (mPushOpenUrl != null) {
            /**
             * 有包含Splash在内多于一个Activity在记录队列中
             * 或者Splash不是Task栈的栈底，栈内存在其它Activity
             * 立刻跳转
             * 这是指用户已经打开应用的状态
             */
            if (CoreApp.coreBaseActivities.size > 1 || !isTaskRoot) {
                /**
                 * 记录ENTER事件并跳转
                 */
                checkAndLogPushEvent(AppLogKey.Label.ENTER)
                startMain()
                return
            } else {
                /**
                 * 记录LAUNCH事件，之后正常流程启动
                 */
                checkAndLogPushEvent(AppLogKey.Label.LAUNCH)
            }
        }

        /**
         * 当安装完成时，部分设备可能会出现在最近任务中重复进入Splash的问题
         * 通过检查splash是否在栈底来回避这个问题
         * Android launched another instance of the root activity into an existing task
         * so just quietly finish and go away, dropping the user back into the activity
         * at the top of the stack (ie: the last state of this task)
         */
        if (!isTaskRoot) {
            finish()
            return
        }

        /**
         * 初始化配置数据
         */
        DataManager.Init.initAppConfigWithAB()
        DataManager.Init.initAppLogConfig()

        /**
         * 上报安装应用信息
         */
//        uploadDeviceAppInfo()


        mBreakStartMain = false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (mPushOpenUrl != null && CoreApp.coreBaseActivities.size > 1) {
            startMain()
            return
        }
    }

    override fun onResume() {
        super.onResume()
        if (!mBreakStartMain) {
            mIdDisposable = UDIDUtils.loadUniqueDeviceId(2000)
                    .onErrorReturn {
                        val notFirstRun = Prefs.getBoolean(LocalKeys.NOT_FIRST_RUN, false)
                        if (!notFirstRun) {
                            Prefs.putBoolean(LocalKeys.NOT_FIRST_RUN, true)
                        }
                        startMain()
                        ""
                    }
                    .ioToMain()
                    .map {
                        val ad: IInterstitialAd? = AdManager.getInterstitialAd(this@SplashActivity)
                        return@map ad ?: Any()
                    }
                    .obOnIo()
                    .map {
                        var count = 0L
                        val step = 100L
                        if (it is IInterstitialAd && it.getAdSourceEna() == AdSourceEna.MOPUB) {
                            while (!AdManager.isMopubInitedFinish()) {
                                Thread.sleep(step)
                                Logger.d("MopubInterstitialAd Wait Init")
                                count += step
                                if (count > 1500) {
                                    return@map it
                                }
                            }
                        }
//                        if (it is IInterstitialAd && it.getAdSourceEna() == AdSourceEna.VUNGLE) {
//                            while (!AdManager.isVungleInitedFinish()) {
//                                Thread.sleep(step)
//                                Logger.d("VungleInterstitialAd Wait Init")
//                                count += step
//                                if (count > 1500) {
//                                    return@map it
//                                }
//                            }
//                        }
                        return@map it
                    }
                    .obOnMain()
                    .subscribeBy(
                            onNext = { ad: Any ->
                                //..命名原因，使用的是Interstitial的config
                                if (ad !is IInterstitialAd) {
                                    startNextPage()
                                    return@subscribeBy
                                }

                                val analyticsEventKey = "${ad.getAdSourceEna().value}_${AnalyticsKey.Event.INTERSTITIAL_AD}"
                                ad.setAdListener(object : IAdListener {
                                    override fun onAdImpression() {
                                        AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = AppLogKey.CL.IMPRESSION))
                                        AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.IMPRESSION)
                                    }

                                    override fun onAdFailedToLoad(error: AdError) {
                                        val duration = (System.currentTimeMillis() - mStartLoadInterstitialTimestamp) / 1000
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
                                            val cl = String.format("${AppLogKey.CL.LOAD_FAIL_STH_CODE_}${error.code}", ad.getAdSourceEna().value)
                                            AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = cl))
                                            AnalyticsManager.logEvent(analyticsEventKey, cl)
                                        }
                                        AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.REQUEST_FAIL)
                                        ad.destroy()
                                        startNextPage()
                                    }

                                    override fun onAdFailedToShow(error: AdError) {

                                    }

                                    override fun onAdLoaded(any: Any?) {
                                        val duration = (System.currentTimeMillis() - mStartLoadInterstitialTimestamp) / 1000
                                        val durationStr =
                                                if (duration > 10) {
                                                    "10+"
                                                } else duration.toString()
                                        AppLogManager.logEvent(ExtraInfo(cE = analyticsEventKey, cL = "${AppLogKey.CL.LOAD_DURATION_}$durationStr"))
                                        AnalyticsManager.logEvent(analyticsEventKey, "${AppLogKey.CL.LOAD_DURATION_}$durationStr")
                                        AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.REQUEST_DONE)
                                        sInterstitialAd = ad
                                        sActivity = this@SplashActivity
                                        startNextPage(ad.getAdSourceEna() != AdSourceEna.MOPUB)
                                    }

                                    override fun onAdClicked() {}

                                    override fun onAdOpened() {}

                                    override fun onAdClosed() {}
                                })
                                AnalyticsManager.logEvent(analyticsEventKey, AnalyticsKey.Parameter.REQUEST)
                                mStartLoadInterstitialTimestamp = System.currentTimeMillis()
                                ad.loadAd()
                            })
        }
    }

    override fun onPause() {
        super.onPause()
        if (!mBreakStartMain) {
            mIdDisposable?.dispose()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mIdDisposable?.dispose()
        mIdDisposable = null
    }

    private fun startNextPage(finish: Boolean = true) {
        Crashlytics.setString("UniqueDeviceId", UDIDUtils.getUniqueDeviceId())
        AppsFlyerAnalytics.setCustomerUserId(UDIDUtils.getUniqueDeviceId())
        val notFirstRun = Prefs.getBoolean(LocalKeys.NOT_FIRST_RUN, false)
        if (!notFirstRun) {
            Prefs.putBoolean(LocalKeys.NOT_FIRST_RUN, true)
        }
        val interestJson = Prefs.getString(LocalKeys.MY_INTEREST, "")
        val validInterest = if (interestJson.isEmpty()) {
            false
        } else {
            val interestBean = CoreGsonUtils.fromJson(interestJson, Interest::class.java)
            interestBean !== null && interestBean.items?.isNotEmpty() == true
        }
        if (!notFirstRun && !Prefs.getBoolean(LocalKeys.IS_SELECTED_INTEREST, false)
                && validInterest) {
            startInterestAct()
        } else if (!notFirstRun && Prefs.getBoolean(LocalKeys.IS_SELECT_GENDER_AGE, false)) {
            startGenderChoose()
        } else {
            DataManager.Init.initNewsChannelListFromRemote()
            startMain(finish)
        }
    }

    private fun startInterestAct() {
        val intent = Intent(this, InterestActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startGenderChoose() {
        GSYVideoManager.releaseAllVideos() //释放所有正在播放的视频
        val bundle = Bundle()
        AutoBundle.pack(this, bundle)
        val intent = Intent(this@SplashActivity, GenderAgeChooseActivity::class.java)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
        finish()
    }

    private fun startMain(finish: Boolean = true) {
        GSYVideoManager.releaseAllVideos() //释放所有正在播放的视频
        val bundle = Bundle()
        AutoBundle.pack(this, bundle)
        val intent = Intent(this@SplashActivity, HomeActivity::class.java)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        startActivity(intent)
        if (finish) {
            finish()
        }
    }

    /**
     * 检查并上报push事件
     */
    private fun checkAndLogPushEvent(appLogLabel: String) {
        if (mPushOpenUrl != null) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.APNS, AnalyticsKey.Parameter.OPEN)
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_APNS,
                    label = appLogLabel,
                    body = AppLog.EventBody.newBuilder()
                            .setOpenUrl(mPushOpenUrl ?: "")
                            .build())
        }
    }

    /**
     * 插屏广告
     * 静态给主页面使用
     */
    companion object {
        var sInterstitialAd: IInterstitialAd? = null
        @SuppressLint("StaticFieldLeak")
        var sActivity: Activity? = null

        fun isInterstitialAdAvailable(): Boolean = sInterstitialAd?.isLoaded() ?: false

        fun freeInterstitialAd() {
            sInterstitialAd?.destroy()
            sInterstitialAd = null
            sActivity?.finish()
            sActivity = null
        }
    }

//    private fun uploadDeviceAppInfo() {
//        //1小时内不重新上报
//        val lastUploadTime = DataManager.Local.getLastUploadDeviceAppInfoTimeFast()
//        if (System.currentTimeMillis() - lastUploadTime < 60 * 60 * 1000) {
//            return
//        }
//        Observable.just(DeviceAppManager.getDeviceAppRequestInfo(false))
//                .delay {
//                    //等待UDID结果
//                    val uniqueDeviceId = UDIDUtils.getUniqueDeviceId()
//                    if (uniqueDeviceId.isEmpty()) {
//                        mUDIDObservable
//                    } else {
//                        Observable.just(uniqueDeviceId)
//                    }
//                }
//                .flatMap {
//                    //UDID获取失败的场合不进行上报
//                    if (UDIDUtils.getUniqueDeviceId().isEmpty()) {
//                        Observable.just(1)
//                    } else {
//                        DataManager.Remote.postDeviceAppMap(it)
//                    }
//                }
//                .io()
//                .subscribeBy(
//                        onNext = {
//                            DataManager.Local.saveLastUploadDeviceAppInfoTimeFast(System.currentTimeMillis())
//                        },
//                        onError = {
//                            Logger.e("uploadDeviceAppInfo fail : $it")
//                        })
//    }

}

