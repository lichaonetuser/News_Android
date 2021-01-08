package com.mynews.app.news.ad.gcmob

import android.app.Activity
import android.content.Context
import com.mynews.app.news.ad.AdManager
import com.mynews.app.news.ad.IInterstitialAd
import com.mynews.app.news.ad.INativeAd
import com.mynews.app.news.ad.INativeAdConfig
import com.mynews.app.news.bean.Ad
import com.mynews.app.news.data.DataDictionary
import com.jumpraw.mediation.GCAdConfig
import com.jumpraw.mediation.GCMedSDK

object JumpRawAdManger {

    fun init(context: Context, slotID: String, appName: String) {
        GCMedSDK.init(context, GCAdConfig.Builder()
                .setSlotId(slotID)
                .setAppName(appName)
                .build())
    }

    fun getNewSplashAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.jumpRaw,false)) {
            return null
        }
        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.jumpRaw)) {
            return null
        }
        val splashId = adConfig.splash.jumpRaw.id
        return JumpRawInterstitialAd(activity, splashId)
    }

    fun getNewInterstitialAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.jumpRaw,false)) {
            return null
        }
        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.jumpRaw)) {
            return null
        }
        val interstitialId = adConfig.interstitial.jumpRaw.id
        return JumpRawInterstitialAd(activity, interstitialId)
    }

    fun getNativeAd(activity: Activity, nativeAdConfig: INativeAdConfig, adConfig: Ad): INativeAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.jumpRaw,false)) {
            return null
        }
        if (!AdManager.checkAdNativeAdConfigValid(nativeAdConfig)) {
            return null
        }
        val nativeId = nativeAdConfig.adSourceId ?: return null
        val nativeType = nativeAdConfig.adType
        return when (nativeType) {
            DataDictionary.AdType.FEED_LARGE_IMAGE.value -> {
                JumpRawNativeAd(activity, nativeId)
            }
            DataDictionary.AdType.FEED_RIGHT_IMAGE.value -> {
                JumpRawNativeAd(activity, nativeId)
            }
            DataDictionary.AdType.FEED_VIDEO.value -> {
                JumpRawNativeAd(activity, nativeId)
            }
            else -> null
        }
    }

}