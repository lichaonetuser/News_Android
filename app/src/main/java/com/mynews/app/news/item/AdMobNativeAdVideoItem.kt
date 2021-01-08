package com.mynews.app.news.item

import android.view.View
import com.mynews.app.news.App
import com.mynews.app.news.R
import com.mynews.app.news.ad.*
import com.mynews.app.news.ad.admob.AdMobAdManger
import com.mynews.app.news.ad.admob.AdmobDebugTool
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.debug.DebugTool
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.debug.DebugManager
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.mynews.common.extension.widget.recycler.holder.BaseViewHolder
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.google.android.gms.ads.formats.UnifiedNativeAd
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.*

class AdMobNativeAdVideoItem<Bean : INativeAdConfig>(mBean: Bean) : BaseItem<Bean, AdMobNativeAdVideoItem.ViewHolder>(mBean) {

    private var mNativeAd: INativeAd? = null

    override fun getLayoutRes() = R.layout.item_admob_native_ad_video

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        if (mNativeAd == null) {
            mNativeAd = AdMobAdManger.getNativeAd(holder.ad_layout.context, mBean, AdManager.getConfig())
        }
        val nativeAd = mNativeAd
        if (nativeAd == null) {
            holder.ad_layout.visibility = View.GONE
            return
        }

        holder.ad_layout.visibility = View.VISIBLE
        if (nativeAd.isLoaded()) {
            val any = nativeAd.getInternal()
            if (any is UnifiedNativeAd) {
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
                if (any is UnifiedNativeAd && nativeAd == holder.ad_layout.tag) {
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
        val unifiedNativeAd = mNativeAd?.getInternal()
        if (unifiedNativeAd is UnifiedNativeAd && mNativeAd == holder?.ad_layout?.tag) {
            unifiedNativeAd.videoController.pause()
        }
    }

    private fun resetViewHolder(holder: ViewHolder) {
        holder.ad_headline_txt.setText(ResUtils.getString(R.string.Common_Loading))
        holder.ad_action_btn.text = ResUtils.getString(R.string.Common_Loading)
        ImageManager.with(holder.ad_primary_img).load(R.drawable.default_news_pic_img)
        holder.ad_media.visibility = View.INVISIBLE
    }

    private fun bindViewHolder(holder: ViewHolder, unifiedNativeAd: UnifiedNativeAd) {
        val unifiedNativeAdView = holder.ad_layout
        holder.ad_headline_txt.setText(unifiedNativeAd.headline)
        unifiedNativeAdView.headlineView = holder.ad_headline_txt
        holder.ad_action_btn.text = unifiedNativeAd.callToAction
        unifiedNativeAdView.callToActionView = holder.ad_action_btn
        holder.ad_media.visibility = View.VISIBLE
        ImageManager.with(holder.ad_primary_img).setNeedBlur(true).load(unifiedNativeAd.images?.getOrNull(0)?.uri?.toString())
        unifiedNativeAdView.mediaView = holder.ad_media
        unifiedNativeAdView.setNativeAd(unifiedNativeAd)
        unifiedNativeAd.videoController.mute(true)
        if (App.isDebug() && DebugManager.enable && DebugTool.mListAdTest) {
            AdmobDebugTool.setDebugTextViewText(holder.ad_layout,
                    "aid : ${mBean.aid}\n" +
                            "adSource : ${mBean.adSource}\n" +
                            "adType : ${mBean.adType}\n" +
                            "adSourceId : ${mBean.adSourceId}\n" +
                            "headline : ${unifiedNativeAd.headline}")
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mBean)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter, val mBean: INativeAdConfig) : BaseViewHolder(view, adapter) {
        val ad_layout = view.ad_layout
        val ad_headline_txt = view.ad_headline_txt
        val ad_media = view.ad_media
        val ad_action_btn = view.ad_action_btn
        val ad_primary_img = view.ad_primary_img

        init {
            AdmobDebugTool.addDebugTextView(ad_layout)
        }

    }

}