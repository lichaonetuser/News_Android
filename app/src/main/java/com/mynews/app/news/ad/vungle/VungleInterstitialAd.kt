//package com.box.app.news.ad.vungle
//
//import android.content.Context
//import com.box.app.news.App
//import com.box.app.news.ad.*
//import com.box.app.news.debug.DebugTool
//import com.box.app.news.util.ToastUtils
//import com.box.common.core.log.Logger
//import com.box.common.core.rx.schedulers.ioToMain
//import com.vungle.warren.AdConfig
//import com.vungle.warren.LoadAdCallback
//import com.vungle.warren.PlayAdCallback
//import com.vungle.warren.Vungle
//import com.vungle.warren.error.VungleException
//import io.reactivex.Completable
//import io.reactivex.disposables.Disposable
//import io.reactivex.rxkotlin.subscribeBy
//import java.util.concurrent.TimeUnit
//
//class VungleInterstitialAd(val context: Context, val id: String) : IInterstitialAd {
//
//    private var mAdListener: IAdListener? = null
//    private var mMuted = true
//    private var mRequestTimeout = -1L
//    private var mLoadCountTimer: Disposable? = null
//    private var mLoadInvalid = false
//    private var mLoading = false
//
//    override fun setAdListener(l: IAdListener) {
//        mAdListener = l
//    }
//
//    override fun setMuted(muted: Boolean) {
//        mMuted = muted
//    }
//
//    override fun isLoaded(): Boolean {
//        return Vungle.canPlayAd(id)
//    }
//
//    override fun loadAd() {
//        if (isLoading()) {
//            return
//        }
//
//        if (mRequestTimeout > 0) {
//            mLoadInvalid = false
//            mLoadCountTimer?.dispose()
//            mLoadCountTimer = Completable.fromRunnable {}
//                    .delay(mRequestTimeout, TimeUnit.SECONDS)
//                    .onErrorComplete()
//                    .doOnSubscribe { mLoadInvalid = false }
//                    .ioToMain<Void>()
//                    .subscribeBy(
//                            onComplete = {
//                                mLoading = false
//                                mLoadInvalid = true
//                                mAdListener?.onAdFailedToLoad(AdError(AdManager.ERROR_CODE_REQUEST_TIMEOUT))
//                                if (App.isDebug() && DebugTool.mAdShowId) {
//                                    ToastUtils.showNewToast("onAdFailedToLoad count_down_interval timeout")
//                                }
//                            }
//                    )
//        }
//        mLoading = true
//        Vungle.loadAd(id, object : LoadAdCallback {
//            override fun onAdLoad(p0: String?) {
//                mLoadCountTimer?.dispose()
//                mLoading = false
//                if (mLoadInvalid) {
//                    return
//                }
//                mAdListener?.onAdLoaded()
//            }
//
//            override fun onError(p0: String?, p1: Throwable?) {
//                mLoadCountTimer?.dispose()
//                mLoading = false
//                if (mLoadInvalid) {
//                    return
//                }
//                mAdListener?.onAdFailedToLoad(AdError(-2, p0, p1))
//            }
//        })
//    }
//
//    override fun show() {
//        if (mLoadInvalid) {
//            return
//        }
//        if (Vungle.canPlayAd(id)) {
//            val config = AdConfig()
//            config.setMuted(mMuted)
//            Vungle.playAd(id, config, object : PlayAdCallback {
//                override fun onAdStart(placementReferenceId: String) {
//                    mAdListener?.onAdOpened()
//                    mAdListener?.onAdImpression()
//                }
//
//                override fun onAdEnd(placementReferenceId: String, completed: Boolean, isCTAClicked: Boolean) {
//                    mAdListener?.onAdClosed()
//                    if (isCTAClicked){
//                        mAdListener?.onAdClicked()
//                    }
//                }
//
//                override fun onError(placementReferenceId: String, throwable: Throwable) {
//                    mAdListener?.onAdFailedToShow(AdError(-1, placementReferenceId, throwable))
//                    try {
//                        val ex = throwable as VungleException
//                        if (ex.exceptionCode == VungleException.VUNGLE_NOT_INTIALIZED) {
//                            VungleAdManager.init()
//                        }
//                    } catch (cex: ClassCastException) {
//                        Logger.e(cex)
//                    }
//                }
//            })
//        }
//    }
//
//    override fun isLoading(): Boolean {
//        return mLoading
//    }
//
//    override fun setRequestTimeout(seconds: Long) {
//        mRequestTimeout = seconds
//    }
//
//    override fun getInternal(): Any {
//        return Any()
//    }
//
//    override fun getAdSourceEna(): AdSourceEna {
//        return AdSourceEna.VUNGLE
//    }
//
//    override fun destroy() {
//    }
//
//}
