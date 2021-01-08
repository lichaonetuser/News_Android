package com.mynews.app.news.item

import android.content.Context
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.GIF
import com.mynews.app.news.item.base.BaseGifItem
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.payload.NewsPayload
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.mynews.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.app.activity.CoreBaseActivity
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import kotlinx.android.synthetic.main.item_gif_detail_mp4.view.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.shuyu.gsyvideoplayer.GSYVideoManager
import org.jetbrains.anko.alignParentLeft
import org.jetbrains.anko.dip

class GifVideoItem(val gif: GIF) : BaseGifItem<BaseNewsItem.ViewHolder>(gif) {

    override fun getLayoutRes(): Int {
        return R.layout.item_gif_detail_mp4
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: BaseNewsItem.ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (holder is ViewHolder) {
            holder.bindInfo(gif, isFavoriteStyle)
            if (!isFavoriteStyle) {
                if (payloads?.contains(NewsPayload.UPDATE_INFORMATION) == false) {
                    holder.bindVideo(gif, isFavoriteStyle)
                }
            }
            holder.titleView.updateFontSize()
        }
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(gif, view, adapter)
    }

    class ViewHolder(val gif: GIF, val view: View, val adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        val titleView = itemView.gif_title

        private var onClickPlay = false

        private val bandwidthMeter = DefaultBandwidthMeter()
        private val videoTackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        private val trackSelector = DefaultTrackSelector(videoTackSelectionFactory)
        private val loadControl = DefaultLoadControl()

        private var simpleExoPlayer:SimpleExoPlayer? = null

        private val dataSourceFactory = DefaultDataSourceFactory(view.context, Util.getUserAgent(view.context, "gif_player"), bandwidthMeter)
        private val extractorsFactory = DefaultExtractorsFactory()

        private var videoSource: ExtractorMediaSource? = null
        private var loopingSource: LoopingMediaSource? = null

        private val mAnalyticsKey: String = AnalyticsKey.Event.GIF_PLAY

        private var playPos = -1

        fun bindInfo(gif: GIF, isFavoriteStyle: Boolean) = with(view) {
            if (TextUtils.isEmpty(gif.title)) {
                gif_title.visibility = View.GONE
            } else {
                gif_title.visibility = View.VISIBLE
                gif_title.text = gif.title
            }

            if (gif.isRead) {
                gif_title?.setTextColor(ResUtils.getColor(R.color.color_3))
            } else {
                gif_title?.setTextColor(ResUtils.getColor(R.color.color_1))
            }

            bury_btn.text = gif.buryCount.toString()
            dig_btn.text = gif.digCount.toString()

            bury_btn.isSelected = gif.isBuried
            dig_btn.isSelected = gif.isDigged

            gif_comment.text = gif.commentCount.toString()
            user_name_txt.text = gif.sourceName

            if (isFavoriteStyle) {
                adapter.state = CommonRecyclerAdapter.FragmentState.PAUSE

                news_emit_time_txt?.gravity = Gravity.CENTER_VERTICAL
                gif_cover.visibility = View.VISIBLE
                gif_video_player.visibility = View.INVISIBLE
                news_emit_time_txt?.visibility = View.VISIBLE
                dig_btn.visibility = View.INVISIBLE
                bury_btn.visibility = View.INVISIBLE
                gif_comment.visibility = View.INVISIBLE
                more_btn.visibility = View.INVISIBLE

                user_img.visibility = View.GONE
                user_name_txt.setTextColor(ResUtils.getColor(R.color.color_3))

                news_emit_time_txt.textSize = 9f
                user_name_txt.textSize = 9f

                val width = EnvDisplayMetrics.WIDTH_PIXELS.toFloat() - dip(20)
                val coverLayoutParams = gif_cover.layoutParams
                coverLayoutParams.width = width.toInt()
                coverLayoutParams.height = (width * 0.4F).toInt()

                val playerLayoutParams = gif_video_player.layoutParams
                playerLayoutParams.width = width.toInt()
                playerLayoutParams.height = (width * 0.4F).toInt()

                if (!gif.title.isEmpty()) {
                    val titleParams = gif_title?.layoutParams as ConstraintLayout.LayoutParams
                    titleParams.leftMargin = 0
                    titleParams.rightMargin = 0
                }

                news_item_layout.setPadding(dip(10), 0, dip(10), 0)

                val userParams = user_img?.layoutParams as RelativeLayout.LayoutParams
                userParams.leftMargin = 0

                val timeParams = news_emit_time_txt?.layoutParams as RelativeLayout.LayoutParams
                timeParams.rightMargin = 0

                val userNameParams = user_name_txt?.layoutParams as RelativeLayout.LayoutParams
                userNameParams.alignParentLeft()

                ImageManager.with(gif_cover).setAspectRatio(2.5F).load(gif.coverImage)
            } else {
                news_emit_time_txt?.visibility = View.GONE
                dig_btn.visibility = View.VISIBLE
                bury_btn.visibility = View.VISIBLE
                gif_comment.visibility = View.VISIBLE
                more_btn.visibility = View.VISIBLE

                user_name_txt.setTextColor(ResUtils.getColor(R.color.color_2))

                ImageManager.with(user_img).load(gif.sourcePic)

                bindItemChildViewClick(bury_btn)
                bindItemChildViewClick(dig_btn)
                bindItemChildViewClick(more_btn)
            }
        }

        fun bindVideo(gif: GIF, isFavoriteStyle: Boolean) = with(view) {
            if (adapter.state == CommonRecyclerAdapter.FragmentState.PAUSE) {
                stopWithUiResetDelay()
                return@with
            }
            if (adapter.state == CommonRecyclerAdapter.FragmentState.HIDDEN) {
                stopWithUiReset()
                return@with
            }

            gif_cover.visibility = View.VISIBLE
            gif_video_player?.visibility = View.VISIBLE

            ImageManager.with(gif_cover).load(gif.coverImage)

            val width = EnvDisplayMetrics.WIDTH_PIXELS.toFloat()
            gif.video?.run {
                if (this.width > 1 && this.height > 1) {
                    val displayHeight = width / this.width * this.height
                    val layoutParams = gif_video_player.layoutParams
                    layoutParams.height = displayHeight.toInt()

                    val coverLayoutParams = gif_cover.layoutParams
                    coverLayoutParams.height = displayHeight.toInt()
                }
            }

            if (simpleExoPlayer == null) {
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
            }

            val url = gif.video?.urls?.firstOrNull()
            val normalUrl = gif.video?.normalUrl
            val finalUrl = if (TextUtils.isEmpty(normalUrl)) {
                url
            } else {
                normalUrl
            }
            videoSource = ExtractorMediaSource(Uri.parse(finalUrl), dataSourceFactory, extractorsFactory, null, null)
            loopingSource = LoopingMediaSource(videoSource)

            gif_video_player.player = simpleExoPlayer
            gif_video_player.hideController()
            gif_video_player.controllerAutoShow = false
            gif_video_player.controllerHideOnTouch = true
            gif_video_player.useController = false
            gif_video_player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)

//            simpleExoPlayer?.addVideoListener(object: SimpleExoPlayer.VideoListener{
//                override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
//                }
//
//                override fun onRenderedFirstFrame() {
//                    gif_loading_pb.visibility = View.GONE
//                    log(gif)
//                    playPos = flexibleAdapterPosition
//                }
//            })
            gif_loading_pb.visibility = View.GONE
            gif_play_btn.visibility = View.VISIBLE
//            if (gif.playState == 0) {
            gif_play_btn.setImageResource(R.drawable.gif)
//            } else {
//                gif_play_btn.setImageResource(R.drawable.gif_replay)
//            }
            if (isFavoriteStyle) {
                bindItemChildViewClick(root_layout)
            } else {
                gif_play_btn.setOnClickListener {
                    play(gif)
                }

                gif_cover.setOnClickListener{
                    play(gif)
                }
                root_layout.setOnClickListener {
                    adapter.state = CommonRecyclerAdapter.FragmentState.PAUSE
                    adapter.notifyDataSetChanged()
                    GSYVideoManager.releaseAllVideos()
                    gotoDetail(context, gif)
                }
            }
        }

        override fun onViewDetachedFromWindow() {
            super.onViewDetachedFromWindow()
            if (adapter.state == CommonRecyclerAdapter.FragmentState.VISIBLE && playPos == flexibleAdapterPosition) {
                AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.SLIDE_STOP_GIF_PLAY)
            }
            stopWithUiReset()
        }

        private fun stopWithUiResetDelay() = with(view) {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
        postDelayed({
                gif_play_btn?.visibility = View.VISIBLE
                gif_loading_pb?.visibility = View.GONE
                gif_cover?.visibility = View.VISIBLE
                gif_video_player?.visibility = View.INVISIBLE
            }, 300)
        }

        private fun stopWithUiReset() = with(view) {
            simpleExoPlayer?.release()
            simpleExoPlayer = null

            gif_play_btn.visibility = View.VISIBLE
            gif_loading_pb.visibility = View.GONE
            gif_cover.visibility = View.VISIBLE
            gif_video_player.visibility = View.INVISIBLE
        }

        fun log(gif: GIF) {
            if (onClickPlay) {
                val mPositionSourceRefer: AppLog.Refer = AppLog.Refer.newBuilder()
                        .setName(AppLogKey.Refer.ARTICLE_LIST)
                        .setItemId(gif.aid)
                        .build()
                if (gif.playState < 1) {
                    AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.GIF_PLAY,
                            onAppsFlyer = false, onFirebase = true, onUmeng = true)
                }
                AppLogManager.logEvent(name = AppLog.EventName.EVENT_GIF,
                        label = AppLogKey.Label.START_PLAYING,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(gif.aid)
                                .setRefer(mPositionSourceRefer)
                                .setEnterTime(System.currentTimeMillis())
                                .build())
                onClickPlay = false
            }
        }

        fun play(gif: GIF) = with(view) {
            if (adapter.state != CommonRecyclerAdapter.FragmentState.VISIBLE) {
                return@with
            }
            gif_cover.visibility = View.GONE
            gif_play_btn.visibility = View.GONE
            gif_loading_pb.visibility = View.VISIBLE
            gif_video_player.visibility = View.VISIBLE

            AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.CLICK_GIF_PLAY)

            if (simpleExoPlayer == null) {
                simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)
            }
            simpleExoPlayer?.addVideoListener(object: SimpleExoPlayer.VideoListener{
                override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
                }

                override fun onRenderedFirstFrame() {
                    gif_loading_pb.visibility = View.GONE
                    log(gif)
                    playPos = flexibleAdapterPosition
                    gif.playState = 1
                }

            })
            gif_video_player.player = simpleExoPlayer

            simpleExoPlayer?.prepare(loopingSource)
            simpleExoPlayer?.playWhenReady = true

            onClickPlay = true
        }

        private fun gotoDetail(context: Context, gif: GIF) {
            AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.CLICK_GIF)
            val fragment  = CoreBaseFragment.instantiate(GifDetailFragment::class.java,
                    GifDetailPresenterAutoBundle.builder(gif, false, adapter.refer?: AppLog.Refer.getDefaultInstance())
                            .mChannel(adapter.channel)
                            .bundle())
            if (context is CoreBaseActivity) {
                context.callRootFragmentStart(fragment)
            }
        }

    }
}