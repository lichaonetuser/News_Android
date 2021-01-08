package com.mynews.app.news.item

import android.app.Activity
import android.text.TextUtils
import android.view.View
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.bean.Video
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BaseVideoItem
import com.mynews.app.news.item.payload.NewsPayload
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.ShareUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.share.ContentLink
import com.mynews.common.extension.share.IShareListener
import com.mynews.common.extension.share.ShareManager
import com.mynews.common.extension.share.SharePlatform
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import kotlinx.android.synthetic.main.item_news_playable.view.*
import kotlinx.android.synthetic.main.video_news_player.view.*
import org.jetbrains.anko.dip

class VideoPlayableItem(mBean: Video) : BaseVideoItem<VideoPlayableItem.ViewHolder>(mBean) {
    val mAnalyticsEventKey: String = AnalyticsKey.Event.VIDEO_DETAIL
    var mAnalyticsKey: String = AnalyticsKey.Event.VIDEO
    var mPositionSourceRefer: AppLog.Refer = AppLog.Refer.newBuilder()
            .setName(AppLogKey.Label.LAUNCH)
            .build()
    var mPositionRefer: AppLog.Refer = AppLog.Refer.newBuilder()
            .setItemId(mBean.aid)
            .build()

    override fun getLayoutRes(): Int {
        return R.layout.item_news_playable
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == true) {
            holder.dig_btn.text = mBean.digCount.toString()
            holder.bury_btn.text = mBean.buryCount.toString()
            holder.comment_txt.text = mBean.commentCount.toString()
            holder.bury_btn.isSelected = mBean.isBuried
            holder.dig_btn.isSelected = mBean.isDigged
            return
        }
        val context = holder.itemView.context as? Activity
        if (isFavoriteStyle) {
            holder.root_view.setPadding(0, context?.dip(10)?: 30, 0, 0)
        } else {
            holder.root_view.setPadding(0, context?.dip(13)?: 39, 0, 0)
        }

        holder.video_view.setUpLazy(video = mBean,
                analyticsEventKey = mAnalyticsKey,
                positionSourceRefer = mPositionSourceRefer,
                positionRefer = mPositionRefer)
        ImageManager.with(holder.user_img).load(mBean.avatarUrl)
        holder.user_name.text = mBean.sourceName
        holder.dig_btn.text = mBean.digCount.toString()
        holder.bury_btn.text = mBean.buryCount.toString()
        holder.comment_txt.text = mBean.commentCount.toString()
        holder.bury_btn.isSelected = mBean.isBuried
        holder.dig_btn.isSelected = mBean.isDigged
        holder.video_view.list_top_layout_title_txt.updateFontSize()
        if (TextUtils.isEmpty(mBean.title)) {
            holder.title_text.visibility = View.GONE
        } else {
            holder.title_text.text = mBean.title
            holder.title_text.visibility = View.VISIBLE
        }

        holder.user_img.visibility = View.VISIBLE
        holder.user_name.visibility = View.VISIBLE
        holder.share_txt.visibility = View.GONE
        holder.share_facebook.visibility = View.GONE
        holder.share_line.visibility = View.GONE

        holder.share_facebook.setOnClickListener {
            context?: return@setOnClickListener
            val content: ContentLink = ShareUtils.getCommonContentLink(mBean, SharePlatform.FACEBOOK)
            val listener: IShareListener = ShareUtils.getCommonShareListener(
            analyticsEventKey = mAnalyticsEventKey,
            analyticsParameterKeyPrefix = "video_end_",
            itemId = mBean.aid,
            refer = AppLogKey.Refer.VIDEO_PLAYER)
            ShareManager.shareLink(context, SharePlatform.FACEBOOK, content, listener)
        }

        holder.share_line.setOnClickListener {
            context?: return@setOnClickListener
            val content: ContentLink = ShareUtils.getCommonContentLink(mBean, SharePlatform.LINE)
            val listener: IShareListener = ShareUtils.getCommonShareListener(
                    analyticsEventKey = mAnalyticsEventKey,
                    analyticsParameterKeyPrefix = "video_end_",
                    itemId = mBean.aid,
                    refer = AppLogKey.Refer.VIDEO_PLAYER)
            ShareManager.shareLink(context, SharePlatform.LINE, content, listener)
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter, mAnalyticsKey)
    }

    class ViewHolder(view: View, adapter: CommonRecyclerAdapter, val mAnalyticsKey: String) : BaseNewsItem.ViewHolder(view, adapter) {
        val video_view = itemView.news_video_view
        val user_img = itemView.user_img
        val user_name = itemView.user_name_txt
        val comment_txt = itemView.comment_txt
        val dig_btn = itemView.dig_btn
        val bury_btn = itemView.bury_btn
        val more_btn = itemView.more_btn
        val title_text = itemView.news_title_txt
        val share_txt = itemView.share_txt
        val share_facebook = itemView.share_facebook
        val share_line = itemView.share_line
        val root_view = itemView.root_layout

        var mIsPlayed = false

        init {
            bindItemChildViewClick(dig_btn)
            bindItemChildViewClick(bury_btn)
            bindItemChildViewClick(more_btn)
            bindItemChildViewClick(share_facebook)
            bindItemChildViewClick(share_line)
            video_view.playListener = {
                when(video_view?.currentState) {
                    GSYVideoView.CURRENT_STATE_PREPAREING,
                    GSYVideoView.CURRENT_STATE_PLAYING -> {
                        if (!mIsPlayed) {
                            mIsPlayed = true
                            AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.LIST_VIDEO_PLAY, onAppsFlyer = false, onFirebase = true, onUmeng = true)
                            AnalyticsManager.logEvent(AnalyticsKey.Event.APPS_FLYER_LIST_VIDEO_PLAY, onAppsFlyer = true, onFirebase = false, onUmeng = false)
                        }
                        GSYVideoManager.instance().playPosition = flexibleAdapterPosition
                        share_txt?.visibility = View.VISIBLE
                        share_facebook?.visibility = View.VISIBLE
                        share_line?.visibility = View.VISIBLE
                        user_name?.visibility = View.INVISIBLE
                        user_img?.visibility = View.INVISIBLE
                    }
                }
            }
            video_view.onClickThumbImgListener ={
                video_view.sendClickAppLogEvent()
            }
        }

        override fun onViewDetachedFromWindow() {
            super.onViewDetachedFromWindow()
            if (GSYVideoManager.instance().playPosition == flexibleAdapterPosition) {
                AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.SLIDE_STOP_VIDEO_PLAY)
                video_view.releaseOnItemDetachedFromWindow()
            }
        }
    }

}