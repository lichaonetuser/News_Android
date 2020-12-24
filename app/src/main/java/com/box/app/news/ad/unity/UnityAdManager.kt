package com.box.app.news.ad.unity

import android.app.Activity
import com.box.app.news.App
import com.box.app.news.R
import com.box.app.news.ad.AdManager
import com.box.app.news.ad.IInterstitialAd
import com.box.app.news.bean.Ad
import com.box.common.core.util.ResUtils
import com.unity3d.ads.UnityAds

object UnityAdManager {

    fun init(activity: Activity, debug: Boolean) {
        UnityAds.setDebugMode(debug)
        UnityAds.initialize(activity, ResUtils.getString(R.string.unity_game_id), App.isDebug())
    }

    fun getNewUnitySplashAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.unity, false)) {
            return null
        }
        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.unity)) {
            return null
        }
        val splashId = adConfig.splash.unity.id
        val splashAd = UnityInterstitialAd(activity, splashId)
        return splashAd
    }


    fun getNewUnityInterstitialAd(activity: Activity, adConfig: Ad): IInterstitialAd? {
//        if (!AdManager.checkAdSourceConfigValid(adConfig.source.unity, false)) {
//            return null
//        }
//        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.unity)) {
//            return null
//        }
//        val interstitialId = adConfig.interstitial.unity.id
//        val interstitialAd = UnityInterstitialAd(activity, "placementId")
//        interstitialAd.setRequestTimeout(adConfig.splash.unity.countDownInterval.toLong())
        return null
    }

}