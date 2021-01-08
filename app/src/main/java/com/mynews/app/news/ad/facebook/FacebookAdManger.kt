package com.mynews.app.news.ad.facebook

import android.content.Context
import com.mynews.app.news.ad.AdManager
import com.mynews.app.news.ad.IInterstitialAd
import com.mynews.app.news.bean.Ad

object FacebookAdManger {

    fun getNewFacebookSplashAd(context: Context, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.facebook, false)) {
            return null
        }
        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.facebook)) {
            return null
        }
        val splashId = adConfig.splash.facebook.id
        return FacebookInterstitialAd(context, splashId)
    }

    fun getNewFacebookInterstitialAd(context: Context, adConfig: Ad): IInterstitialAd? {
        if (!AdManager.checkAdSourceConfigValid(adConfig.source.facebook, false)) {
            return null
        }
        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.facebook)) {
            return null
        }
        val interstitialId = adConfig.interstitial.facebook.id
        val interstitialAd = FacebookInterstitialAd(context, interstitialId)
        interstitialAd.setRequestTimeout(adConfig.splash.facebook.countDownInterval.toLong())
        return interstitialAd
    }


}