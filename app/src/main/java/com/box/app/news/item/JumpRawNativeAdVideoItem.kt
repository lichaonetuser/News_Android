package com.box.app.news.item

import android.view.View
import com.box.app.news.App
import com.box.app.news.R
import com.box.app.news.ad.*
import com.box.app.news.ad.admob.AdMobAdManger
import com.box.app.news.ad.admob.AdmobDebugTool
import com.box.app.news.ad.gcmob.JumpRawAdManger
import com.box.app.news.ad.gcmob.JumpRawNativeAd
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.debug.DebugTool
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.debug.DebugManager
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.box.common.extension.widget.recycler.holder.BaseViewHolder
import com.box.common.extension.widget.recycler.item.BaseItem
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.jumpraw.mediation.GCFeedAd
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.ad_action_btn
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.ad_content_layout
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.ad_headline_txt
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.ad_layout
import kotlinx.android.synthetic.main.item_admob_native_ad_video.view.ad_primary_img
import kotlinx.android.synthetic.main.item_jumpraw_native_ad_video.view.*

class JumpRawNativeAdVideoItem<Bean : INativeAdConfig>(mBean: Bean) : BaseItem<Bean, JumpRawNativeAdVideoItem.ViewHolder>(mBean) {

    private var mNativeAd: INativeAd? = null

    override fun getLayoutRes() = R.layout.item_jumpraw_native_ad_video

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        if (mNativeAd == null) {
            val activity = CoreApp.getLastActivityInstance()
            if (activity == null) {
                holder.ad_layout.visibility = View.GONE
                return
            }
            mNativeAd = JumpRawAdManger.getNativeAd(activity, mBean, AdManager.getConfig())
        }
        val nativeAd = mNativeAd
        if (nativeAd == null) {
            holder.ad_layout.visibility = View.GONE
            return
        }

        if (nativeAd is JumpRawNativeAd) {
            nativeAd.adContainer = holder.ad_layout
            nativeAd.adClickView = holder.ad_content_layout
        }

        holder.ad_layout.visibility = View.VISIBLE
        if (nativeAd.isLoaded()) {
            val any = nativeAd.getInternal()
            if (any is GCFeedAd) {
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
                if (any is GCFeedAd && nativeAd == holder.ad_layout.tag) {
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

    private fun resetViewHolder(holder: ViewHolder) {
        holder.ad_headline_txt.text = ResUtils.getString(R.string.Common_Loading)
        holder.ad_action_btn.text = ResUtils.getString(R.string.Common_Loading)
        ImageManager.with(holder.ad_primary_img).load(R.drawable.default_news_pic_img)
        holder.ad_media_layout.visibility = View.INVISIBLE
    }

    private fun bindViewHolder(holder: ViewHolder, gcFeedAd: GCFeedAd) {
        holder.ad_headline_txt.text = gcFeedAd.title
        holder.ad_action_btn.text = gcFeedAd.buttonText
        holder.ad_media_layout.visibility = View.VISIBLE
        ImageManager.with(holder.ad_primary_img).setNeedBlur(true).load(gcFeedAd.imageList?.getOrNull(0)?.imageUrl)
        holder.ad_media_layout.removeAllViews()
        holder.ad_media_layout.addView(gcFeedAd.adView)
        if (App.isDebug() && DebugManager.enable && DebugTool.mListAdTest) {
            AdmobDebugTool.setDebugTextViewText(holder.ad_layout,
                    "aid : ${mBean.aid}\n" +
                            "adSource : ${mBean.adSource}\n" +
                            "adType : ${mBean.adType}\n" +
                            "adSourceId : ${mBean.adSourceId}\n")
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mBean)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter, val mBean: INativeAdConfig) : BaseViewHolder(view, adapter) {
        val ad_layout = view.ad_layout
        val ad_content_layout = view.ad_content_layout
        val ad_headline_txt = view.ad_headline_txt
        val ad_media_layout = view.ad_media_layout
        val ad_action_btn = view.ad_action_btn
        val ad_primary_img = view.ad_primary_img

        init {
            AdmobDebugTool.addDebugTextView(ad_layout)
        }

    }

}