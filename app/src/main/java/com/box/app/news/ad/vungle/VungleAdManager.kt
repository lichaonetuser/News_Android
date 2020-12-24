//package com.box.app.news.ad.vungle
//
//import android.content.Context
//import com.box.app.news.App
//import com.box.app.news.R
//import com.box.app.news.ad.AdManager
//import com.box.app.news.ad.IInterstitialAd
//import com.box.app.news.ad.facebook.FacebookInterstitialAd
//import com.box.app.news.bean.Ad
//import com.box.common.core.util.ResUtils
//import com.vungle.warren.InitCallback
//import com.vungle.warren.Vungle
//
//object VungleAdManager {
//
//    private val context by lazy {
//        App.getInstance()
//    }
//
//    var mInitedFinish = false
//
//    fun init() {
//        Vungle.init(ResUtils.getString(R.string.vungle_app_id), context, object : InitCallback {
//            override fun onSuccess() {
//                mInitedFinish = true
//            }
//
//            override fun onAutoCacheAdAvailable(p0: String?) {
//            }
//
//            override fun onError(p0: Throwable?) {
//                mInitedFinish = true
//            }
//        })
//    }
//
//    fun getNewVungleSplashAd(context: Context, adConfig: Ad): IInterstitialAd? {
//        if (!AdManager.checkAdSourceConfigValid(adConfig.source.vungle, false)) {
//            return null
//        }
//        if (!AdManager.checkAdSplashConfigValid(adConfig.splash.vungle)) {
//            return null
//        }
//        val splashId = adConfig.splash.vungle.id
//        val splashAd = VungleInterstitialAd(context, splashId)
//        return splashAd
//    }
//
//
//    fun getNewVungleInterstitialAd(context: Context, adConfig: Ad): IInterstitialAd? {
//        if (!AdManager.checkAdSourceConfigValid(adConfig.source.vungle, false)) {
//            return null
//        }
//        if (!AdManager.checkAdInterstitialConfigValid(adConfig.interstitial.vungle)) {
//            return null
//        }
//        val interstitialId = adConfig.interstitial.vungle.id
//        val interstitialAd = VungleInterstitialAd(context, interstitialId)
//        interstitialAd.setRequestTimeout(adConfig.splash.vungle.countDownInterval.toLong())
//        return interstitialAd
//    }
//
//}