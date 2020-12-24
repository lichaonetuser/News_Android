package com.box.app.news.ad.facebook

import android.content.Context
import com.box.app.news.App
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdManager.ERROR_CODE_REQUEST_TIMEOUT
import com.box.app.news.ad.AdSourceEna
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.IInterstitialAd
import com.box.app.news.debug.DebugTool
import com.box.app.news.util.ToastUtils
import com.box.common.core.rx.schedulers.ioToMain
import com.facebook.ads.Ad
import com.facebook.ads.AdSettings
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class FacebookInterstitialAd(context: Context, id: String) : IInterstitialAd {

    private var mInterstitialAd: InterstitialAd = InterstitialAd(context, id)
    private var mAdListener: IAdListener? = null
    private var mRequestTimeout = -1L
    private var mLoadCountTimer: Disposable? = null
    private var mLoadInvalid = false
    private var mLoading = false
    private var mMuted = true

    init {
        if (!App.isDebug()) {
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE)
        }

        mInterstitialAd.setAdListener(object : InterstitialAdListener {
            override fun onInterstitialDisplayed(p0: Ad?) {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdOpened()
            }

            override fun onAdClicked(p0: Ad?) {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdClicked()
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdClosed()
            }

            override fun onError(p0: Ad?, p1: com.facebook.ads.AdError?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdFailedToLoad(AdError(p1?.errorCode ?: -999))
                if (App.isDebug() && DebugTool.mAdShowId) {
                    ToastUtils.showNewToast("onAdFailedToLoad errorCode : ${p1?.errorCode ?: -999}")
                }
            }

            override fun onAdLoaded(p0: Ad?) {
                mLoading = false
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdLoaded()
            }

            override fun onLoggingImpression(p0: Ad?) {
            }

        })
    }

    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoaded(): Boolean {
        return mInterstitialAd.isAdLoaded
    }

    override fun isLoading(): Boolean {
        return mLoading
    }

    override fun loadAd() {
        if (isLoading()) {
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
        mInterstitialAd.loadAd()
        mLoading = true
    }

    override fun show() {
        mInterstitialAd.show()
    }

    override fun setRequestTimeout(seconds: Long) {
        this.mRequestTimeout = seconds
    }

    override fun getInternal(): InterstitialAd {
        return mInterstitialAd
    }

    override fun getAdSourceEna(): AdSourceEna {
        return AdSourceEna.FACEBOOK
    }

    override fun destroy() {

    }

    override fun setMuted(muted: Boolean) {
        mMuted = muted
    }

}
