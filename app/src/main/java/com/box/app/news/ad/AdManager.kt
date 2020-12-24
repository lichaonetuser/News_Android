package com.box.app.news.ad

import android.app.Activity
import android.content.Context
import com.box.app.news.ad.admob.AdMobAdManger
import com.box.app.news.ad.facebook.FacebookAdManger
import com.box.app.news.ad.gcmob.JumpRawAdManger
import com.box.app.news.ad.mopub.MoPubAdManger
import com.box.app.news.ad.unity.UnityAdManager
import com.box.app.news.bean.*
import com.box.app.news.data.DataManager
import com.box.app.news.data.source.local.preference.PreferenceAPI
import com.box.app.news.util.LaunchUtils
import com.box.common.core.rx.schedulers.io
import com.suib.base.core.SuibSDK
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit


object AdManager {

    /**
     * 注意
     * 注意！！！
     * SplashAd是热启，InterstitialAd是冷启
     */

    private var mAdBlockFlag = false // 为true时，block广告逻辑，外部参考修改，内部不做处理
    private var mAdBlockFlagDelayDisposable: Disposable? = null

    private var mAdConfig = Ad()

    init {
        System.loadLibrary("native_lib")
    }

    fun initUnity(activity: Activity, debug: Boolean) {
        UnityAdManager.init(activity, debug)
    }

    fun initZcoup(context: Context, slotID: String) {
        SuibSDK.initialize(context, slotID)
        SuibSDK.setSchema(true)
    }

    fun initVungle() {
//        VungleAdManager.init()
    }

    fun initMoPub(context: Context, adUnitId: String) {
        return MoPubAdManger.init(context, adUnitId)
    }

    fun initJumpRaw(context: Context, slotID: String, appName: String) {
        JumpRawAdManger.init(context, slotID, appName)
    }

    fun isMopubInitedFinish(): Boolean {
        return MoPubAdManger.mInitedFinish
    }

//    fun isVungleInitedFinish(): Boolean {
//        return VungleAdManager.mInitedFinish
//    }

    fun isAdBlocking(): Boolean {
        return mAdBlockFlag
    }

    fun setAdBlockFlag(flag: Boolean, cancelDelay: Boolean = true) {
        if (cancelDelay) {
            mAdBlockFlagDelayDisposable?.dispose()
        }
        mAdBlockFlag = flag
    }

    fun setAdBlockFlagDelay(flag: Boolean, milliseconds: Long) {
        mAdBlockFlagDelayDisposable?.dispose()
        mAdBlockFlagDelayDisposable = Completable.complete()
                .delay(milliseconds, TimeUnit.MILLISECONDS)
                .onErrorComplete()
                .io()
                .subscribeBy { mAdBlockFlag = flag }
    }

    fun refreshAdConfig() {
        mAdConfig = (DataManager.Local.getAppConfig() ?: AppConfig()).ad ?: Ad()
    }

    fun getSplashAd(activity: Activity): IInterstitialAd? {
//        if (LaunchUtils.isVersionFirstLaunch) {
//            return null
//        }
        val splashSource = mAdConfig.splash.source
        if (splashSource.isEmpty()) {
            return null
        }

        if (mAdConfig.splash.disable) {
            return null
        }

        val splashSourceEna = try {
            AdSourceEna.valueOf(splashSource.toUpperCase())
        } catch (e: Exception) {
            return null
        }

        return when (splashSourceEna) {
            AdSourceEna.ADMOB -> AdMobAdManger.getNewAdmobSplashAd(activity, mAdConfig)
            AdSourceEna.FACEBOOK -> FacebookAdManger.getNewFacebookSplashAd(activity, mAdConfig)
            AdSourceEna.MOPUB -> MoPubAdManger.getNewMopubSplashAd(activity, mAdConfig)
            AdSourceEna.VUNGLE -> null
            AdSourceEna.UNITY -> UnityAdManager.getNewUnitySplashAd(activity, mAdConfig)
            AdSourceEna.JUMPRAW -> JumpRawAdManger.getNewSplashAd(activity, mAdConfig)
        }
    }

    fun getInterstitialAd(activity: Activity): IInterstitialAd? {
        if (LaunchUtils.isVersionFirstLaunch) {
            return null
        }

        val interstitialSource = mAdConfig.interstitial.source
        if (interstitialSource.isEmpty()) {
            return null
        }

        if (mAdConfig.interstitial.disable) {
            return null
        }

        val interstitialSourceEna = try {
            AdSourceEna.valueOf(interstitialSource.toUpperCase())
        } catch (e: Exception) {
            return null
        }

        //检查两次显示的时间差
        val lastShowSplashAdTimestamp = PreferenceAPI.getLastShowSplashAdTimestamp()
        val minShowInterval: Long = ((System.currentTimeMillis() - lastShowSplashAdTimestamp) / 1000)
        if (minShowInterval <= mAdConfig.interstitial.minShowInterval) {
            return null
        }
        PreferenceAPI.saveLastShowSplashAdTimestamp(System.currentTimeMillis())

        return when (interstitialSourceEna) {
            AdSourceEna.ADMOB -> AdMobAdManger.getNewAdmobInterstitialAd(activity, mAdConfig)
            AdSourceEna.FACEBOOK -> FacebookAdManger.getNewFacebookInterstitialAd(activity, mAdConfig)
            AdSourceEna.MOPUB -> MoPubAdManger.getNewMopubInterstitialAd(activity, mAdConfig)
            AdSourceEna.VUNGLE -> null
            AdSourceEna.UNITY -> UnityAdManager.getNewUnityInterstitialAd(activity, mAdConfig)
            AdSourceEna.JUMPRAW -> JumpRawAdManger.getNewInterstitialAd(activity, mAdConfig)
        }
    }

    fun getNativeAdSourceEna(nativeAdConfig: INativeAdConfig): AdSourceEna? {
        val source = nativeAdConfig.adSource ?: ""
        if (source.isEmpty()) {
            return null
        }

        return try {
            AdSourceEna.valueOf(source.toUpperCase())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @return true为合法有效
     */
    fun checkAdSourceConfigValid(config: AdSourceConfig, checkAppID: Boolean = true): Boolean {
        if (config.disable) {
            return false
        }
        if (!checkAppID) {
            return true
        }

        val applicationId = config.applicationId
        val applicationCheckId = config.checkId
        if (!checkIDValid(applicationId, applicationCheckId)) {
            return false
        }
        return true
    }

    /**
     * @return true为合法有效
     */
    fun checkAdInterstitialConfigValid(config: AdInterstitialConfig): Boolean {
        val id = config.id
        val checkId = config.checkId
        if (!checkIDValid(id, checkId)) {
            return false
        }
        return true
    }

    /**
     * @return true为合法有效
     */
    fun checkAdSplashConfigValid(config: AdSplashConfig): Boolean {
        val id = config.id
        val checkId = config.checkId
        if (!checkIDValid(id, checkId)) {
            return false
        }
        return true
    }

    /**
     * @return true为合法有效
     */
    fun checkAdNativeAdConfigValid(config: INativeAdConfig): Boolean {
        val id = config.adSourceId ?: ""
        val checkId = config.adCheckId ?: ""
        if (!checkIDValid(id, checkId)) {
            return false
        }
        return true
    }

    /**
     * @return true为合法有效
     */
    external fun checkIDValid(adId: String, checkId: String): Boolean

    fun getConfig(): Ad {
        return mAdConfig
    }

    val ERROR_CODE_REQUEST_TIMEOUT = -10123

    fun onCreate(activity: Activity) {
        MoPubAdManger.onCreate(activity)
    }

    fun onStart(activity: Activity) {
        MoPubAdManger.onStart(activity)
    }

    fun onPause(activity: Activity) {
        MoPubAdManger.onPause(activity)
    }

    fun onResume(activity: Activity) {
        MoPubAdManger.onResume(activity)
    }

    fun onRestart(activity: Activity) {
        MoPubAdManger.onRestart(activity)
    }

    fun onStop(activity: Activity) {
        MoPubAdManger.onStop(activity)
    }

    fun onDestroy(activity: Activity) {
        MoPubAdManger.onDestroy(activity)
    }

    fun onBackPressed(activity: Activity) {
        MoPubAdManger.onBackPressed(activity)
    }


}
