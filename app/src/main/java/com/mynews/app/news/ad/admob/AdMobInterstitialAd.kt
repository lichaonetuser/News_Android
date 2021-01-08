package com.mynews.app.news.ad.admob

import android.content.Context
import com.mynews.app.news.App
import com.mynews.app.news.ad.AdError
import com.mynews.app.news.ad.AdManager.ERROR_CODE_REQUEST_TIMEOUT
import com.mynews.app.news.ad.AdSourceEna
import com.mynews.app.news.ad.IAdListener
import com.mynews.app.news.ad.IInterstitialAd
import com.mynews.app.news.bean.AdMediation
import com.mynews.app.news.debug.DebugTool
import com.mynews.app.news.util.MD5Utils
import com.mynews.app.news.util.ToastUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.environment.EnvSecure
import com.mynews.common.core.rx.schedulers.ioToMain
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import io.reactivex.Completable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.anko.activityManager
import org.jetbrains.anko.keyguardManager
import org.jetbrains.anko.powerManager
import java.util.concurrent.TimeUnit

class AdMobInterstitialAd(context: Context, val mediation: AdMediation) : IInterstitialAd {

    private var mInterstitialAd: InterstitialAd = InterstitialAd(context)
    private var mAdListener: IAdListener? = null
    private var mRequestTimeout = -1L
    private var mLoadCountTimer: Disposable? = null
    private var mLoadInvalid = false

    init {
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdClosed()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdFailedToLoad(AdError(errorCode))
                if (App.isDebug() && DebugTool.mAdShowId) {
                    ToastUtils.showNewToast("onAdFailedToLoad errorCode : $errorCode")
                }
            }

            override fun onAdLeftApplication() {

            }

            override fun onAdOpened() {
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdOpened()
            }

            override fun onAdLoaded() {
                if (mLoadInvalid) {
                    return
                }
                mLoadCountTimer?.dispose()
                mAdListener?.onAdLoaded()
            }

            override fun onAdImpression() {
                super.onAdImpression()
                if (mLoadInvalid) {
                    return
                }
                mAdListener?.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
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
        return mInterstitialAd.isLoaded
    }

    override fun isLoading(): Boolean {
        return mInterstitialAd.isLoading
    }

    override fun loadAd() {
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

//        val extras = VungleExtrasBuilder(mediation.vungle.placements.toTypedArray())
//                .setSoundEnabled(false)
//                .build()

        if (App.isDebug()) {
            if (DebugTool.mAdTestDevice) {
                val testDeviceId = MD5Utils.md5ToString(EnvSecure.ANDROID_ID).toUpperCase()
                mInterstitialAd.loadAd(AdRequest.Builder()
                        .addTestDevice(testDeviceId)
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                        .addNetworkExtrasBundle(VungleInterstitialAdapter::class.java, extras)
                        .build())
            } else {
                mInterstitialAd.loadAd(AdRequest.Builder()
//                        .addNetworkExtrasBundle(VungleInterstitialAdapter::class.java, extras)
                        .build())
            }
            if (DebugTool.mAdShowId) {
                ToastUtils.showNewToast(mInterstitialAd.adUnitId ?: "null")
            }
        } else {
            mInterstitialAd.loadAd(AdRequest.Builder()
//                    .addNetworkExtrasBundle(VungleInterstitialAdapter::class.java, extras)
                    .build())
        }
    }

    override fun show() {
        if (canShow()) {
            mInterstitialAd.show()
        } else {
            mAdListener?.onAdFailedToShow(AdError(-99999))
        }
    }

    override fun setRequestTimeout(seconds: Long) {
        this.mRequestTimeout = seconds
    }

    override fun getInternal(): InterstitialAd {
        return mInterstitialAd
    }

    override fun getAdSourceEna(): AdSourceEna {
        return AdSourceEna.ADMOB
    }

    fun canShow(): Boolean {
        try {
            val activityManager = CoreApp.getInstance().activityManager
            val keyguardManager = CoreApp.getInstance().keyguardManager
            val runningAppProcesses = activityManager.runningAppProcesses ?: return false
            for (runningAppProcessInfo in runningAppProcesses) {
                if (android.os.Process.myPid() == runningAppProcessInfo.pid) {
                    return runningAppProcessInfo.importance == 100 && !keyguardManager.inKeyguardRestrictedInputMode() && zzr()
                }
            }
            return false
        } catch (th: Throwable) {
            return false
        }
    }

    /**
     * 算了……名字这么留着好了，等查GMS的广告的时候有用……
     * canShow -> zzO
     */
    private fun zzr(): Boolean {
        val powerManager = CoreApp.getInstance().powerManager
        return powerManager.isScreenOn
    }

    override fun destroy() {

    }

    override fun setMuted(muted: Boolean) {
    }

}
