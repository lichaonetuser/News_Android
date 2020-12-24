package com.box.app.news.ad.mopub

import android.content.Context
import android.view.View
import com.box.app.news.ad.AdError
import com.box.app.news.ad.AdManager
import com.box.app.news.ad.IAdListener
import com.box.app.news.ad.INativeAd
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.mopub.nativeads.*
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy

class MoPubNativeAd<T : MoPubAdRenderer<out V>, V : BaseNativeAd>(val mContext: Context, val mId: String, val mRendererList: List<T>) : INativeAd {

    private var mAdListener: IAdListener? = null
    private var mAdLoaded = false
    private var mAdLoading = false
    private var mMoPubNative: MoPubNative? = null
    private var mNative: NativeAd? = null

    override fun setAdListener(l: IAdListener) {
        mAdListener = l
    }

    override fun isLoading(): Boolean {
        return mAdLoading
    }

    override fun isLoaded(): Boolean {
        return mAdLoaded
    }

    override fun loadAd(vararg any: Any?) {
        mAdLoading = true
        if (AdManager.isMopubInitedFinish()) {
            makeRequest()
        } else {
            Completable.fromAction {
                while (!AdManager.isMopubInitedFinish()) {
                    Thread.sleep(100)
                    Logger.d("MopubNativeAd Wait Init")
                }
                Thread.sleep(50)
            }.ioToMain<Void>()
                    .subscribeBy(
                            onComplete = {
                                makeRequest()
                            },
                            onError = {
                                mAdLoading = false
                            }
                    )
        }
    }

    private fun makeRequest() {
        mMoPubNative = MoPubNative(mContext, mId, object : MoPubNative.MoPubNativeNetworkListener {
            override fun onNativeLoad(nativeAd: NativeAd?) {
                mAdLoading = false
                val ad = nativeAd ?: return

                mAdLoaded = true
                ad.setMoPubNativeEventListener(object : NativeAd.MoPubNativeEventListener {
                    override fun onClick(view: View?) {
                        mAdListener?.onAdClicked()
                    }

                    override fun onImpression(view: View?) {
                        mAdListener?.onAdImpression()
                    }
                })

                mNative = nativeAd
                mAdListener?.onAdLoaded(ad)
            }

            override fun onNativeFail(errorCode: NativeErrorCode?) {
                mAdLoaded = false
                mAdLoading = false
                Logger.d("onNativeFail : $errorCode")
                mAdListener?.onAdFailedToLoad(AdError(errorCode?.intCode ?: -999))
            }
        })
        mRendererList.forEach {
            mMoPubNative?.registerAdRenderer(it)
        }
        mMoPubNative?.makeRequest()
    }

    override fun destroy() {
        mMoPubNative?.destroy()
        mMoPubNative = null
        mAdListener = null
    }

    override fun getInternal(): Any? {
        return mNative
    }

}