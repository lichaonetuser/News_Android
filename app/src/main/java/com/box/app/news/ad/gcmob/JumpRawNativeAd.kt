package com.box.app.news.ad.gcmob

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import com.box.app.news.App
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdManager
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.INativeAd
import com.box.app.news.debug.DebugTool
import com.box.app.news.util.ToastUtils
import com.box.common.core.rx.schedulers.ioToMain
import com.jumpraw.mediation.GCAdNative
import com.jumpraw.mediation.GCAdSlot
import com.jumpraw.mediation.GCFeedAd
import com.jumpraw.mediation.GCMedSDK
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit


class JumpRawNativeAd(activity: Activity, val slotId: String) : INativeAd {

    var adContainer: ViewGroup? = null
    var adClickView: View? = null
    private var mGCAdManager = GCMedSDK.getAdManager()
    private var mGCAdNative: GCAdNative = mGCAdManager.createAdNative(activity)
    private var mFeedAd: GCFeedAd? = null
    private var mAdListener: IAdListener? = null
    private var mRequestTimeout = -1L
    private var mLoadCountTimer: Disposable? = null
    private var mLoadInvalid = false
    private var mIsLoading = false
    private var mIsLoaded = false

    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoading(): Boolean {
        return mIsLoading
    }

    override fun isLoaded(): Boolean {
        return mIsLoaded
    }

    override fun loadAd(vararg any: Any?) {
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
                                mAdListener?.onAdFailedToLoad(AdError(AdManager.ERROR_CODE_REQUEST_TIMEOUT))
                                if (App.isDebug() && DebugTool.mAdShowId) {
                                    ToastUtils.showNewToast("onAdFailedToLoad count_down_interval timeout")
                                }
                            }
                    )
        }

        val adSlot = GCAdSlot.Builder().setSlotId(slotId).build()
        mGCAdNative.loadFeedAd(adSlot, object : GCAdNative.FeedAdListener {
            override fun onFeedAdLoad(feedAds: MutableList<GCFeedAd>?) {
                mFeedAd = feedAds?.getOrNull(0) ?: return
                mIsLoaded = true
                mIsLoading = false
                if (mLoadInvalid) {
                    return
                }
                mFeedAd?.setVideoAdListener(object : GCFeedAd.VideoAdListener {
                    override fun onVideoAdPaused(p0: GCFeedAd?) {

                    }

                    override fun onProgressUpdate(p0: Long, p1: Long) {

                    }

                    override fun onVideoAdComplete(p0: GCFeedAd?) {

                    }

                    override fun onVideoAdStartPlay(p0: GCFeedAd?) {

                    }

                    override fun onVideoError(p0: Int, p1: Int) {
                        mAdListener?.onAdFailedToShow(AdError(-1, "onVideoError $p0 $p1"))
                    }

                    override fun onVideoAdContinuePlay(p0: GCFeedAd?) {

                    }

                    override fun onVideoLoad(p0: GCFeedAd?) {

                    }

                })
                if (adContainer != null && adClickView != null) {
                    mFeedAd?.registerViewForInteraction(adContainer!!, adClickView!!, object : GCFeedAd.AdInteractionListener {
                        override fun onAdClicked(p0: View?, p1: GCFeedAd?) {
                            mAdListener?.onAdClicked()
                        }

                        override fun onAdShow(p0: GCFeedAd?) {
                            mAdListener?.onAdImpression()
                        }

                        override fun onAdCreativeClick(p0: View?, p1: GCFeedAd?) {

                        }

                    })
                }
                mAdListener?.onAdLoaded(mFeedAd)
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

    override fun destroy() {
        mAdListener = null
        adContainer = null
        adClickView = null
    }

    override fun getInternal(): Any? {
        return mFeedAd
    }

}