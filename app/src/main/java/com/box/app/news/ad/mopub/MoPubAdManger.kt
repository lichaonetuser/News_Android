package com.box.app.news.ad.mopub

import android.app.Activity
import android.content.Context
import com.box.app.news.App
import com.box.app.news.ad.AdManager
import com.box.app.news.ad.IInterstitialAd
import com.box.app.news.ad.INativeAd
import com.box.app.news.ad.INativeAdConfig
import com.box.app.news.bean.Ad
import com.mopub.common.MoPub
import com.mopub.common.SdkConfiguration
import com.mopub.common.logging.MoPubLog
import com.mopub.nativeads.BaseNativeAd
import com.mopub.nativeads.MoPubAdRenderer

object MoPubAdManger {

    private var mInited = false
    internal var mInitedFinish = false

    fun init(context: Context, adUnitId: String) {
        if (mInited) {
            return
        }
        synchronized(this) {
            if (mInited) {
                return
            }
            MoPub.initializeSdk(
                    context,
                    SdkConfiguration.Builder(adUnitId)
                            .withLogLevel(
                                    if (App.isDebug()) {
                                        MoPubLog.LogLevel.DEBUG
                                    } else {
                                        MoPubLog.LogLevel.NONE
                                    })
                            .withLegitimateInterestAllowed(true)
                            .build()
            ) { mInitedFinish = true }
            mInited = true
        }
    }

    fun getNewMopubSplashAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.mopub, false)) {
            return null
        }
        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.mopub)) {
            return null
        }
        val splashId = adConfig.splash.mopub.id
        init(activity, splashId)
        return MoPubInterstitialAd(activity, splashId)
    }

    fun getNewMopubInterstitialAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.mopub, false)) {
            return null
        }
        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.mopub)) {
            return null
        }
        val interstitialId = adConfig.interstitial.mopub.id
        init(activity, interstitialId)
        val interstitialAd = MoPubInterstitialAd(activity, interstitialId)
        interstitialAd.setRequestTimeout(adConfig.splash.admob.countDownInterval.toLong())
        return interstitialAd
    }

    fun <T : MoPubAdRenderer<out V>, V : BaseNativeAd> getNativeAd(context: Context?, nativeAdConfig: INativeAdConfig, adConfig: Ad, rendererList: List<T>): INativeAd? {
        if (context == null) {
            return null
        }
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.mopub)) {
            return null
        }
        if (!AdManager.checkAdNativeAdConfigValid(nativeAdConfig)) {
            return null
        }
        val nativeId = nativeAdConfig.adSourceId ?: return null
        init(context, nativeId)
        return MoPubNativeAd(context, nativeId, rendererList)
    }

    fun onCreate(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onCreate(activity)
    }

    fun onStart(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onStart(activity)
    }

    fun onPause(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onPause(activity)
    }

    fun onResume(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onResume(activity)
    }

    fun onRestart(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onRestart(activity)
    }

    fun onStop(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onStop(activity)
    }

    fun onDestroy(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onDestroy(activity)
    }

    fun onBackPressed(activity: Activity) {
        if (!mInited) {
            return
        }
        MoPub.onBackPressed(activity)
    }


}