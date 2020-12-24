package com.box.app.news.item

import android.view.LayoutInflater
import android.view.View
import com.box.app.news.R
import com.box.app.news.ad.*
import com.box.app.news.ad.mopub.MoPubAdManger
import com.box.app.news.analytics.AnalyticsKey
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import com.mopub.nativeads.*
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.item_mopub_native_ad_video.view.*
import org.jetbrains.anko.findOptional

class MoPubNativeAdVideoItem<Bean : INativeAdConfig>(mBean: Bean) : BaseItem<Bean, MoPubNativeAdVideoItem.ViewHolder>(mBean) {

    private var mNativeAd: INativeAd? = null
    private var mAdapterHelper: AdapterHelper? = null
    private var mNativeAdView: View? = null

    override fun getLayoutRes() = R.layout.item_mopub_native_ad_video

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        if (mNativeAd == null) {
            val googlePlayServicesAdRenderer = GooglePlayServicesAdRenderer(
                    MediaViewBinder.Builder(R.layout.item_mopub_native_ad_mediation_admob_video)
                            .mediaLayoutId(R.id.ad_media) // bind to your `com.mopub.nativeads.MediaLayout` element
                            .titleId(R.id.ad_headline_txt)
                            .callToActionId(R.id.ad_action_btn)
                            .privacyInformationIconImageId(R.id.ad_privacy_information_icon_img)
                            .build())
            val facebookAdRenderer = FacebookAdRenderer(
                    FacebookAdRenderer.FacebookViewBinder.Builder(R.layout.item_mopub_native_ad_mediation_facebook_video)
                            .titleId(R.id.ad_headline_txt)
                            .mediaViewId(R.id.ad_primary_img)
                            .callToActionId(R.id.ad_action_btn)
                            .adChoicesRelativeLayoutId(R.id.ad_privacy_information_icon_layout)
                            .build())

            val moPubVideoNativeAdRenderer = MoPubVideoNativeAdRenderer(MediaViewBinder.Builder(R.layout.item_mopub_native_ad_video_internal)
                    .mediaLayoutId(R.id.ad_media)
                    .textId(R.id.ad_headline_txt)
                    .callToActionId(R.id.ad_action_btn)
                    .privacyInformationIconImageId(R.id.ad_privacy_information_icon_img)
                    .build())

            val moPubStaticNativeAdRenderer = MoPubStaticNativeAdRenderer(ViewBinder.Builder(R.layout.item_mopub_native_ad_large_img_internal)
                    .mainImageId(R.id.ad_primary_img)
                    .textId(R.id.ad_headline_txt)
                    .callToActionId(R.id.ad_action_btn)
                    .privacyInformationIconImageId(R.id.ad_privacy_information_icon_img)
                    .build())

            val rendererList: List<MoPubAdRenderer<out BaseNativeAd>> = arrayListOf(googlePlayServicesAdRenderer, facebookAdRenderer, moPubVideoNativeAdRenderer, moPubStaticNativeAdRenderer)
            mNativeAd = MoPubAdManger.getNativeAd(holder.ad_layout.context, mBean, AdManager.getConfig(), rendererList)
            if (mNativeAd == null) {
                holder.ad_layout.visibility = View.GONE
                return
            }
            if (mAdapterHelper == null) {
                mAdapterHelper = AdapterHelper(holder.ad_layout.context, 0, 2)
            }
        }
        val nativeAd = mNativeAd ?: return
        val adapterHelper = mAdapterHelper ?: return
        holder.ad_layout.visibility = View.VISIBLE
        if (nativeAd.isLoaded()) {
            val any = nativeAd.getInternal()
            if (any is NativeAd) {
                bindViewHolder(holder, any)
            }
            return
        }

        if (nativeAd.isLoading()) {
            resetViewHolder(holder)
            return
        }

        resetViewHolder(holder)
        nativeAd.setAdListener(object : IAdListener {
            override fun onAdImpression() {
                val parameter = "${mBean.adSource}_${mBean.adType}_${AnalyticsKey.Parameter.IMPRESSION}"
                AnalyticsManager.logEvent(AnalyticsKey.Event.FEED_AD, parameter)
            }

            override fun onAdFailedToLoad(error: AdError) {
                val parameter = "${mBean.adSource}_${mBean.adType}_${AnalyticsKey.Parameter.REQUEST_FAIL}"
                AnalyticsManager.logEvent(AnalyticsKey.Event.FEED_AD, parameter)
            }

            override fun onAdFailedToShow(error: AdError) {
            }

            override fun onAdLoaded(any: Any?) {
                val parameter = "${mBean.adSource}_${mBean.adType}_${AnalyticsKey.Parameter.REQUEST_DONE}"
                AnalyticsManager.logEvent(AnalyticsKey.Event.FEED_AD, parameter)
                if (any is NativeAd && nativeAd == holder.ad_layout.tag) {
                    bindViewHolder(holder, any)
                }
            }

            override fun onAdOpened() {
            }

            override fun onAdClicked() {
                val parameter = "${mBean.adSource}_${mBean.adType}_${AnalyticsKey.Parameter.CLICK}"
                AnalyticsManager.logEvent(AnalyticsKey.Event.FEED_AD, parameter)
            }

            override fun onAdClosed() {
            }
        })
        val parameter = "${mBean.adSource}_${mBean.adType}_${AnalyticsKey.Parameter.REQUEST}"
        AnalyticsManager.logEvent(AnalyticsKey.Event.FEED_AD, parameter)
        holder.ad_layout.tag = nativeAd
        nativeAd.loadAd()
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    private fun bindViewHolder(holder: ViewHolder, any: NativeAd) {
        if (mNativeAdView == null) {
            val view = mAdapterHelper?.getAdView(null, holder.ad_layout, any, ViewBinder.Builder(0).build())
                    ?: return
            val mediaView = view.findOptional<MediaLayout>(R.id.ad_media)
            mediaView?.setMuteState(MediaLayout.MuteState.MUTED)
            mNativeAdView = view
        }
        holder.ad_layout.removeAllViews()
        holder.ad_layout.addView(mNativeAdView)
    }

    private fun resetViewHolder(holder: ViewHolder) {
        holder.reset()
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mBean)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter, val mBean: INativeAdConfig) : BaseViewHolder(view, adapter) {
        val ad_layout = view.ad_layout
        private val mPlaceHolderVIew = LayoutInflater.from(view.context).inflate(R.layout.item_mopub_native_ad_video_internal, null)

        fun reset() {
            ad_layout.removeAllViews()
            ad_layout.addView(mPlaceHolderVIew)
        }

    }

}