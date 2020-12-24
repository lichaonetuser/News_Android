package com.box.app.news.ad.mopub

import android.app.Activity
import com.box.app.news.App
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdManager.ERROR_CODE_REQUEST_TIMEOUT
import com.box.app.news.ad.AdSourceEna
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.IInterstitialAd
import com.box.app.news.debug.DebugTool
import com.box.app.news.util.ToastUtils
import com.box.common.core.rx.schedulers.ioToMain
import com.mopub.mobileads.MoPubErrorCode
import com.mopub.mobileads.MoPubInterstitial
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class MoPubInterstitialAd(activity: Activity, id: String) : IInterstitialAd {

    private var mInterstitialAd: MoPubInterstitial = MoPubInterstitial(activity, id)
    private var mAdListener: IAdListener? = null
    private var mRequestTimeout = -1L
    private var mLoadCountTimer: Disposable? = null
    private var mLoadInvalid = false
    private var mLoading = false
    private var mMuted = true

    init {
        mInterstitialAd.interstitialAdListener = object : MoPubInterstitial.InterstitialAdListener {
            override fun onInterstitialLoaded(interstitial: MoPubInterstitial?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdLoaded()
            }

            override fun onInterstitialShown(interstitial: MoPubInterstitial?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdOpened()
            }

            override fun onInterstitialFailed(interstitial: MoPubInterstitial?, errorCode: MoPubErrorCode?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdFailedToLoad(AdError(errorCode?.intCode ?: -999))
                if (App.isDebug() && DebugTool.mAdShowId) {
                    ToastUtils.showNewToast("onAdFailedToLoad errorCode : ${errorCode?.intCode
                            ?: -999}")
                }
            }

            override fun onInterstitialDismissed(interstitial: MoPubInterstitial?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdClosed()
            }

            override fun onInterstitialClicked(interstitial: MoPubInterstitial?) {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdClicked()
            }

        }
    }

    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoaded(): Boolean {
        return mInterstitialAd.isReady
    }

    override fun isLoading(): Boolean {
        return mLoading
    }

    override fun loadAd() {
        if (isLoading()) {
            if (isLoaded()) {
                mLoading = false
            }
            return
        }

        if (mRequestTimeout > 0) {
            mLoadCountTimer?.dispose()
            mLoadCountTimer = Completable.fromRunnable {}
                    .delay(mRequestTimeout, TimeUnit.SECONDS)
                    .onErrorComplete()
                    .doOnSubscribe { mLoadInvalid = false }
                    .ioToMain<Void>()
                    .subscribeBy(
                            onComplete = {
                                mLoading = false
                                mLoadInvalid = true
                                mAdListener?.onAdFailedToLoad(AdError(ERROR_CODE_REQUEST_TIMEOUT))
                                if (App.isDebug() && DebugTool.mAdShowId) {
                                    ToastUtils.showNewToast("onAdFailedToLoad count_down_interval timeout")
                                }
                            }
                    )
        }
        mLoading = true
//        val extras = hashMapOf<String, Any>()
//        extras["vungleSoundEnabled"] = !mMuted
//        mInterstitialAd.localExtras = extras
        mInterstitialAd.load()
    }

    override fun show() {
        if (mInterstitialAd.isReady) {
            mInterstitialAd.show()
        } else {
            mAdListener?.onAdFailedToShow(AdError(-99999))
        }
    }

    override fun setRequestTimeout(seconds: Long) {
        this.mRequestTimeout = seconds
    }

    override fun getInternal(): MoPubInterstitial {
        return mInterstitialAd
    }

    override fun getAdSourceEna(): AdSourceEna {
        return AdSourceEna.MOPUB
    }

    override fun destroy() {
        mInterstitialAd.destroy()
    }

    override fun setMuted(muted: Boolean) {
        mMuted = muted
    }

}
