package com.box.app.news.ad.gcmob

import android.app.Activity
import com.box.app.news.App
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdManager.ERROR_CODE_REQUEST_TIMEOUT
import com.box.app.news.ad.AdSourceEna
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.IInterstitialAd
import com.box.app.news.debug.DebugTool
import com.box.app.news.util.ToastUtils
import com.box.common.core.CoreApp
import com.box.common.core.rx.schedulers.ioToMain
import com.jumpraw.mediation.GCAdNative
import com.jumpraw.mediation.GCAdSlot
import com.jumpraw.mediation.GCFullScreenVideoAd
import com.jumpraw.mediation.GCMedSDK
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.lang.Exception
import java.util.concurrent.TimeUnit

class JumpRawInterstitialAd(val activity: Activity, private val slotId: String) : IInterstitialAd {

    private var mGCAdManager = GCMedSDK.getAdManager()
    private var mGCAdNative: GCAdNative = mGCAdManager.createAdNative(activity)
    private var mVideoAd: GCFullScreenVideoAd? = null
    private var mAdListener: IAdListener? = null
    private var mRequestTimeout = -1L
    private var mLoadCountTimer: Disposable? = null
    private var mLoadInvalid = false
    private var mIsLoading = false
    private var mIsLoaded = false


    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoaded(): Boolean {
        return mIsLoaded
    }

    override fun isLoading(): Boolean {
        return mIsLoading
    }

    override fun loadAd() {
        mIsLoaded = false
        mIsLoading = true
        if (mRequestTimeout > 0) {
            mLoadCountTimer?.dispose()
            mLoadCountTimer = Completable.fromRunnable {}
                    .delay(mRequestTimeout, TimeUnit.SECONDS)
                    .onErrorComplete()
                    .doOnSubscribe { mLoadInvalid = false }
                    .ioToMain<Void>()
                    .subscribeBy(
                            onComplete = {
                                mLoadInvalid = true
                                mAdListener?.onAdFailedToLoad(AdError(ERROR_CODE_REQUEST_TIMEOUT))
                                if (App.isDebug() && DebugTool.mAdShowId) {
                                    ToastUtils.showNewToast("onAdFailedToLoad count_down_interval timeout")
                                }
                            }
                    )
        }

        val adSlot = GCAdSlot.Builder().setSlotId(slotId).build()
        mGCAdNative.loadFullScreenVideoAd(adSlot, object : GCAdNative.FullScreenVideoAdListener {
            override fun onFullScreenVideoAdLoad(ad: GCFullScreenVideoAd?) {
                mIsLoading = false
                if (mLoadInvalid) {
                    return
                }
                mIsLoaded = true
                mLoadCountTimer?.dispose()
                mVideoAd = ad
                mVideoAd?.setFullScreenVideoAdInteractionListener(object : GCFullScreenVideoAd.FullScreenVideoAdInteractionListener {
                    override fun onAdShow() {
                        mIsLoaded = false
                        if (mLoadInvalid) {
                            return
                        }
                        mAdListener?.onAdOpened()
                        mAdListener?.onAdImpression()
                    }

                    override fun onAdVideoBarClick() {
                        if (mLoadInvalid) {
                            return
                        }
                        mAdListener?.onAdClicked()
                    }

                    override fun onVideoComplete() {
                        if (mLoadInvalid) {
                            return
                        }
                    }

                    override fun onAdClose() {
                        mIsLoaded = false
                        if (mLoadInvalid) {
                            return
                        }
                        mAdListener?.onAdClosed()
                    }

                })
                mAdListener?.onAdLoaded()
            }

            override fun onError(error: String?) {
                mIsLoaded = false
                mIsLoading = false
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdFailedToLoad(AdError(-1, error, null))
            }

        })
    }

    override fun show() {
        try {
            if (mVideoAd == null) {
                mAdListener?.onAdFailedToShow(AdError(-2, "null", null))
                return
            }
            mVideoAd?.showFullScreenVideoAd(CoreApp.getLastActivityInstance())
        } catch (e: Exception) {
            mAdListener?.onAdFailedToShow(AdError(-1, "try catch", null))
        }
    }

    override fun setRequestTimeout(seconds: Long) {
        this.mRequestTimeout = seconds
    }

    override fun getInternal(): GCAdNative {
        return mGCAdNative;
    }

    override fun getAdSourceEna(): AdSourceEna {
        return AdSourceEna.JUMPRAW
    }

    override fun destroy() {

    }

    override fun setMuted(muted: Boolean) {
    }

}
