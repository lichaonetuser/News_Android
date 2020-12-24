package com.box.app.news.ad.unity

import android.app.Activity
import com.box.app.news.App
import com.box.app.news.ad.*
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds

class UnityInterstitialAd(val activity: Activity, val placementId: String) : IInterstitialAd {

    private var mAdListener: IAdListener? = null
    private val listener = object : IUnityAdsListener {
        override fun onUnityAdsStart(placement: String?) {
            if (placement != placementId) {
                return
            }
            mAdListener?.onAdOpened()
        }

        override fun onUnityAdsFinish(placement: String?, p1: UnityAds.FinishState?) {
            if (placement != placementId) {
                return
            }
            mAdListener?.onAdClosed()
        }

        override fun onUnityAdsError(error: UnityAds.UnityAdsError?, placement: String?) {
            if (placement != placementId) {
                return
            }
            mAdListener?.onAdFailedToShow(AdError(error?.ordinal ?: -999, error?.name ?: "UNKNOWN"))
        }

        override fun onUnityAdsReady(placement: String?) {
            if (placement != placementId) {
                return
            }
            mAdListener?.onAdLoaded()
        }

    }

    init {
        UnityAds.addListener(listener)
        if (!UnityAds.isInitialized()) {
            UnityAdManager.init(activity, App.isDebug())
        }
    }

    override fun isLoaded(): Boolean {
        return UnityAds.isReady(placementId)
    }

    override fun isLoading(): Boolean {
        return UnityAds.isReady(placementId)
    }

    override fun setAdListener(l: IAdListener) {
        mAdListener = l

    }

    override fun loadAd() {
        UnityAds.load(placementId)
    }

    override fun show() {
        if (UnityAds.isReady(placementId)) {
            UnityAds.show(activity, placementId)
        }
    }

    override fun setRequestTimeout(seconds: Long) {
    }

    override fun getInternal(): Any {
        return Unit
    }

    override fun getAdSourceEna(): AdSourceEna {
        return AdSourceEna.UNITY
    }

    override fun destroy() {
        UnityAds.removeListener(listener)
    }

    override fun setMuted(muted: Boolean) {
    }

}
