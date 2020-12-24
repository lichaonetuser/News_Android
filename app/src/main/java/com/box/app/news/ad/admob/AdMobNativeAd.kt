package com.box.app.news.ad.admob

import com.box.app.news.App
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdIdCenter
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.INativeAd
import com.box.app.news.debug.DebugTool
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd

class AdMobNativeAd(val mId: String, val mNativeAdOptions: NativeAdOptions) : INativeAd {

    private var mAdLoader: AdLoader? = null
    private var mAdListener: IAdListener? = null
    private var mAdLoaded = false
    private var mUnifiedNativeAd: UnifiedNativeAd? = null

    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoading(): Boolean {
        return mAdLoader?.isLoading ?: false
    }

    override fun isLoaded(): Boolean {
        return mAdLoaded
    }

    override fun loadAd(vararg any: Any?) {
        mAdLoader = AdLoader.Builder(App.getInstance(), mId)
                .withNativeAdOptions(mNativeAdOptions)
                .withAdListener(object : AdListener() {
                    override fun onAdClosed() {
                        mAdListener?.onAdClosed()
                    }

                    override fun onAdFailedToLoad(errorCode: Int) {
                        mAdListener?.onAdFailedToLoad(AdError(errorCode))
                    }

                    override fun onAdLeftApplication() {

                    }

                    override fun onAdOpened() {
                        mAdListener?.onAdOpened()
                    }

                    override fun onAdLoaded() {
                        // listener by forUnifiedNativeAd
                    }

                    override fun onAdClicked() {
                        mAdListener?.onAdClicked()
                    }

                    override fun onAdImpression() {
                        mAdListener?.onAdImpression()
                    }
                })
                .forUnifiedNativeAd { ad ->
                    mAdLoaded = true
                    mUnifiedNativeAd = ad
                    mAdListener?.onAdLoaded(ad)
                }
                .build()
        if (App.isDebug() && DebugTool.mAdTestDevice) {
            mAdLoader?.loadAd(AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(AdIdCenter.ADMOB_TEST_DEVICE_ID)
                    .build())
        } else {
            mAdLoader?.loadAd(AdRequest.Builder().build())
        }
    }

    override fun destroy() {
        mAdListener = null
        mAdLoader = null
        mUnifiedNativeAd?.destroy()
        mUnifiedNativeAd = null

    }

    override fun getInternal(): Any? {
        return mUnifiedNativeAd
    }

}