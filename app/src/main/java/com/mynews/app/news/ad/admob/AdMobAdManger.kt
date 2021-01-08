package com.mynews.app.news.ad.admob

import android.content.Context
import com.mynews.app.news.ad.*
import com.mynews.app.news.ad.mopub.MoPubAdManger
import com.mynews.app.news.bean.Ad
import com.mynews.app.news.data.DataDictionary
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.NativeAdOptions.ORIENTATION_LANDSCAPE

object AdMobAdManger {

    fun init(context: Context, adConfig: Ad) {
        val applicationId = adConfig.source.admob.applicationId
        MobileAds.initialize(context, applicationId)
        val mediation = adConfig.source.admob.mediation
        mediation.forEach {
            when (it.source) {
                AdSourceEna.MOPUB.value -> {
                    if (it.disable) {
                        return
                    }
                    if (AdManager.checkIDValid(it.id, it.checkId)) {
                        MoPubAdManger.init(context, it.id)
                    }
                }
            }
        }
    }

    fun getNewAdmobSplashAd(context: Context, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.admob)) {
            return null
        }
        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.admob)) {
            return null
        }
        val splashId = adConfig.splash.admob.id
        init(context, adConfig)
        val splashAd = AdMobInterstitialAd(context,adConfig.splash.admob.mediation)
        splashAd.getInternal().adUnitId = splashId
        return splashAd
    }

    fun getNewAdmobInterstitialAd(context: Context, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.admob)) {
            return null
        }
        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.admob)) {
            return null
        }
        val interstitialId = adConfig.interstitial.admob.id
        init(context, adConfig)
        val interstitialAd = AdMobInterstitialAd(context,adConfig.interstitial.admob.mediation)
        interstitialAd.getInternal().adUnitId = interstitialId
        interstitialAd.setRequestTimeout(adConfig.interstitial.admob.countDownInterval.toLong())
        return interstitialAd
    }

    fun getNativeAd(context: Context, nativeAdConfig: INativeAdConfig, adConfig: Ad): INativeAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.admob)) {
            return null
        }
        if (!AdManager.checkAdNativeAdConfigValid(nativeAdConfig)) {
            return null
        }
        init(context, adConfig)
        val nativeId = nativeAdConfig.adSourceId ?: return null
        val nativeType = nativeAdConfig.adType
        return when (nativeType) {
            DataDictionary.AdType.FEED_LARGE_IMAGE.value -> {
                val adOptions = NativeAdOptions.Builder()
                        .setReturnUrlsForImageAssets(true)
                        .setImageOrientation(ORIENTATION_LANDSCAPE)
                        .build()
                AdMobNativeAd(nativeId, adOptions)
            }
            DataDictionary.AdType.FEED_RIGHT_IMAGE.value -> {
                val adOptions = NativeAdOptions.Builder()
                        .setReturnUrlsForImageAssets(true)
                        .setImageOrientation(ORIENTATION_LANDSCAPE)
                        .build()
                AdMobNativeAd(nativeId, adOptions)
            }
            DataDictionary.AdType.FEED_VIDEO.value -> {
                val videoOptions = VideoOptions.Builder()
                        .setStartMuted(true)
                        .build()
                val adOptions = NativeAdOptions.Builder()
                        .setImageOrientation(ORIENTATION_LANDSCAPE)
                        .setVideoOptions(videoOptions)
                        .build()
                AdMobNativeAd(nativeId, adOptions)
            }
            else -> null
        }
    }

}