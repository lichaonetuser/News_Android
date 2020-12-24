package com.box.app.news.page.mvp.layer.main.gif

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.view.animation.*
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.GIF
import com.box.app.news.bean.GIF.Companion.GIF_TYPE_IMAGE
import com.box.app.news.bean.GIF.Companion.GIF_TYPE_MP4
import com.box.app.news.data.DataDictionary
import com.box.app.news.page.mvp.layer.main.digbury.DigBuryragment
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserFragment
import com.box.app.news.page.mvp.layer.main.list.comment.CommentFragment
import com.box.app.news.page.mvp.layer.main.list.related.RelatedFragment
import com.box.app.news.proto.AppLog
import com.box.app.news.widget.nested.NestedChildRecyclerView
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.loading.MVPLoadingFragment
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.LoopingMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_gif_detail.*
import me.yokeyword.fragmentation.ISupportFragment
import org.jetbrains.anko.findOptional

class GifDetailFragment : MVPLoadingFragment<GifDetailContract.View,
        GifDetailContract.Presenter<GifDetailContract.View>>(),
        GifDetailContract.View {

    override val mAttachSwipeBack = true
    override var mDispatchBack = true
    override val mPresenter = GifDetailPresenter()
    override val mLayoutRes = R.layout.fragment_gif_detail
    private var mRelatedFragment: RelatedFragment? = null
    private var mDigBuryFragment: DigBuryragment? = null
    private var mCommentFragment: CommentFragment? = null
    private val mGoFullImageRequestCode = 0x111

    private var mRememberScrollY = -1
    private var mRememberCommentScrollPosition = -1

    private var mGif: GIF? = null
    private var isPlaying = false
    private var hasSetupGif: Boolean = false
    private var hasSetupVideo: Boolean = false
    private var onClickPlay: Boolean = false
    private var simpleExoPlayer: SimpleExoPlayer? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        gif_user_img.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        gif_user_name_txt.setOnClickListener {
            mPresenter.onClickAuthor()
        }
        write_comment_btn.setOnClickListener {
            mPresenter.onClickWriteComment()
        }
        comment_btn.setOnClickListener {
            mPresenter.onClickComment()
        }
        collect_btn.setOnClickListener {
            mGif?.run {
                if (!isFavorite) {
                    collect_btn.collect()
                } else {
                    collect_btn.unCollect()
                }
                mPresenter.onClickCollect()
            }
        }
        share_btn.setOnClickListener {
            mPresenter.onClickShare()
        }

//        scroll_view.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
//            when(mGif?.gifType) {
//                GIF_TYPE_MP4 -> {
//                    gif_video_player?: return@OnScrollChangeListener
//                    if (scrollY > gif_video_player.bottom && isPlaying) {
//                        simpleExoPlayer?.stop()
//                        gif_img.visibility = View.VISIBLE
//                        gif_play_btn.visibility = View.VISIBLE
//                        if (mGif?.playState == 1) {
//                            gif_play_btn.setImageResource(R.drawable.gif_replay)
//                        } else {
//                            gif_play_btn.setImageResource(R.drawable.gif)
//                        }
//                        isPlaying = false
//                    }
//                }
//            }
//        })
        gif_play_btn.visibility = View.GONE
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    override fun onFragmentResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentResult(requestCode, resultCode, data)
        if (requestCode == mGoFullImageRequestCode && resultCode == ISupportFragment.RESULT_OK) {
            mPresenter.onFragmentResultFromFullImage(data)
        }
    }

    override fun setNews(gif: GIF) {
        mGif = gif
        if (gif.title.isBlank()) {
            gif_title_txt.visibility = View.GONE
        } else {
            gif_title_txt.visibility = View.VISIBLE
        }
        gif_title_txt.text = gif.title
        gif_user_name_txt.text = gif.sourceName
        ImageManager.with(gif_user_img).load(gif.sourcePic)

        collect_btn.isActivated = gif.isFavorite
        comment_count_txt.visibility = if (gif.commentCount == 0) {
            View.GONE
        } else {
            View.VISIBLE
        }
        comment_count_txt.text = gif.commentCount.toString()
        write_comment_btn.isEnabled = (gif.commentType == DataDictionary.CommentType.ENABLE.value)
        write_comment_btn.text = if (write_comment_btn.isEnabled) {
            ResUtils.getString(R.string.Tip_WriteAComment)
        } else {
            ResUtils.getString(R.string.Tip_NewsCommentUnsupported)
        }
        gif_video_player?: return
        when (gif.gifType) {
            GIF_TYPE_IMAGE -> {
                gif_video_player?.visibility = View.INVISIBLE
                gif_img.visibility = View.VISIBLE
                bindGif(gif)
            }
            GIF_TYPE_MP4 -> {
                gif_video_player?.visibility = View.VISIBLE
                gif_img.visibility = View.GONE
                bindVideo(gif)
            }
        }
    }

    private fun bindGif(gif: GIF) {
        if (hasSetupGif) {
            return
        }
        val urls = gif.image?.urls
        if (urls != null && urls.isNotEmpty()) {
            val draweeController = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setUri(Uri.parse(urls.firstOrNull()))//设置uri
                    .build()
            gif_img.controller = draweeController

            gif_img.setOnClickListener {
                val animate = draweeController.animatable
                animate?.run {
                    if (isRunning) { //判断是否正在运行
                        gif_play_btn.visibility = View.VISIBLE
                        stop()
                    } else {
                        gif_play_btn.visibility = View.GONE
                        start()
                    }
                }
            }
            gif_play_btn.visibility = View.GONE
        } else {
            ImageManager.with(gif_img).load(gif.coverImage)
        }
        hasSetupGif = true
    }

    private val bandwidthMeter = DefaultBandwidthMeter()
    private val videoTackSelectionFactory = Factory(bandwidthMeter)
    private val trackSelector = DefaultTrackSelector(videoTackSelectionFactory)
    private val loadControl = DefaultLoadControl()

    private fun bindVideo(gif: GIF) {
        if (hasSetupVideo || context == null || isDetached) {
            return
        }
        val mAnalyticsKey: String = AnalyticsKey.Event.GIF_PLAY
        val mPositionSourceRefer: AppLog.Refer = AppLog.Refer.newBuilder()
                .setName(AppLogKey.Refer.ARTICLE_DETAIL)
                .setItemId(gif.aid)
                .build()
        val mPositionRefer: AppLog.Refer = AppLog.Refer.newBuilder()
                .setItemId(gif.aid)
                .build()

        val url = gif.video?.urls?.firstOrNull()
        val normalUrl = gif.video?.normalUrl
        val finalUrl = if (TextUtils.isEmpty(normalUrl)) {
            url
        } else {
            normalUrl
        }
        val width = (EnvDisplayMetrics.WIDTH_PIXELS - scroll_layout.paddingLeft - scroll_layout.paddingRight).toFloat()
        gif.video?.run {
            if (this.width > 1 && this.height > 1) {
                val displayHeight = width / this.width * this.height
                val layoutParams = gif_video_player.layoutParams
                layoutParams.height = displayHeight.toInt()

                val coverLayoutParams = gif_img.layoutParams
                coverLayoutParams.height = displayHeight.toInt()
            }
        }

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl)

        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "gif_detail"), bandwidthMeter)
        val extractorsFactory = DefaultExtractorsFactory()

        val videoSource = ExtractorMediaSource(Uri.parse(finalUrl), dataSourceFactory, extractorsFactory, null, null)
        val loopingSource = LoopingMediaSource(videoSource)

        gif_video_player.player = simpleExoPlayer
        gif_video_player.hideController()
        gif_video_player.controllerAutoShow = false
        gif_video_player.controllerHideOnTouch = true
        gif_video_player.useController = false
        simpleExoPlayer?.prepare(loopingSource)
        simpleExoPlayer?.playWhenReady = true
        onClickPlay = true

        simpleExoPlayer?.addVideoListener(object: SimpleExoPlayer.VideoListener{
            override fun onVideoSizeChanged(width: Int, height: Int, unappliedRotationDegrees: Int, pixelWidthHeightRatio: Float) {
            }

            override fun onRenderedFirstFrame() {
                gif_img.visibility = View.GONE
                gif_loading_pb.visibility = View.GONE
                log(gif, mPositionSourceRefer, mAnalyticsKey, mPositionRefer)
                gif.playState = 1
            }

        })
        ImageManager.with(gif_img).load(gif.coverImage)
        gif_loading_pb.visibility = View.VISIBLE
        gif_play_btn.visibility = View.GONE
        isPlaying = true
        if (gif.playState == 0) {
            gif_play_btn.setImageResource(R.drawable.gif)
        } else {
            gif_play_btn.setImageResource(R.drawable.gif_replay)
        }
        gif_play_btn.setOnClickListener {
            play(gif, mPositionSourceRefer, mAnalyticsKey, mPositionRefer, loopingSource)
        }
        gif_img.setOnClickListener{
            play(gif, mPositionSourceRefer, mAnalyticsKey, mPositionRefer, loopingSource)
        }

        hasSetupVideo = true
    }

    fun play(gif: GIF, mPositionSourceRefer: AppLog.Refer, mAnalyticsKey: String, mPositionRefer: AppLog.Refer, loopingSource: LoopingMediaSource) {
        gif_img.visibility = View.GONE
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
                gif_img.visibility = View.GONE
                gif_loading_pb.visibility = View.GONE
                log(gif, mPositionSourceRefer, mAnalyticsKey, mPositionRefer)
                gif.playState = 1
            }

        })
        gif_video_player.player = simpleExoPlayer
        simpleExoPlayer?.prepare(loopingSource)
        simpleExoPlayer?.playWhenReady = true

        onClickPlay = true
        isPlaying = true
    }

    fun log(gif: GIF, mPositionSourceRefer: AppLog.Refer, mAnalyticsKey: String, mPositionRefer: AppLog.Refer) {
        if (onClickPlay) {
//            AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.CLICK_GIF)
            if (gif.playState < 1) {
                AnalyticsManager.logEvent(mAnalyticsKey, AnalyticsKey.Parameter.GIF_PLAY,
                        onAppsFlyer = false, onFirebase = true, onUmeng = true)
            }
            AppLogManager.logEvent(name = AppLog.EventName.EVENT_GIF,
                    label = AppLogKey.Label.START_PLAYING,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(System.currentTimeMillis())
                            .setItemId(gif.aid)
                            .setRefer(mPositionSourceRefer)
                            .build())

            onClickPlay = false
        }
    }

    private var handler: Handler? = Handler()

    override fun onSupportInvisible() {
        super.onSupportInvisible()

        stop()
        handler?.postDelayed({
            gif_img.visibility = View.VISIBLE
            gif_play_btn.visibility = View.VISIBLE
            gif_video_player.visibility = View.INVISIBLE
        }, 300)
    }

    fun stop() {
        simpleExoPlayer?.release()
        simpleExoPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        stop()
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    override fun goFullImage(bundle: Bundle) {
        extraTransaction()
                .setCustomAnimations(
                        R.anim.core_fade_in,
                        R.anim.core_fade_out,
                        R.anim.no_anim,
                        R.anim.core_fade_out)
                .startForResultDontHideSelf(instantiate(ImageBrowserFragment::class.java, bundle), mGoFullImageRequestCode)
    }

    override fun loadRelated(bundle: Bundle) {
        val relatedFragment = findChildFragment(RelatedFragment::class.java)
        if (relatedFragment == null) {
            mRelatedFragment = CoreBaseFragment.instantiate(RelatedFragment::class.java, bundle)
            loadRootFragment(R.id.container_related_layout, mRelatedFragment)
        } else {
            mRelatedFragment = relatedFragment
        }
    }

    override fun loadDigBury(bundle: Bundle) {
        val digBuryFragment = findChildFragment(DigBuryragment::class.java)
        if (digBuryFragment == null) {
            mDigBuryFragment = CoreBaseFragment.instantiate(DigBuryragment::class.java, bundle)
            loadRootFragment(R.id.container_dig_bury_layout, mDigBuryFragment)
        } else {
            mDigBuryFragment = digBuryFragment
        }
    }

    override fun loadComment(bundle: Bundle) {
        val commentFragment = findChildFragment(CommentFragment::class.java)
        if (commentFragment == null) {
            mCommentFragment = CoreBaseFragment.instantiate(CommentFragment::class.java, bundle)
            loadRootFragment(R.id.container_comment_layout, mCommentFragment)
        } else {
            mCommentFragment = commentFragment
        }
    }

    override fun scrollToComment() {
        if (mRememberScrollY == -1) {
            mRememberScrollY = 0
        }
        val rv = container_comment_layout.findOptional<NestedChildRecyclerView>(R.id.common_content_rv)
                ?: return
        scroll_view.fling(0)
        rv.stopScroll()
        scroll_view.smoothScrollTo(0, container_comment_layout.top)
        rv.smoothScrollToPosition(0)
    }

    override fun toggleToComment() {
        val rv = container_comment_layout.findOptional<NestedChildRecyclerView>(R.id.common_content_rv)
                ?: return
        if (mRememberScrollY == -1) {
            mRememberScrollY = scroll_view.scrollY
            if (rv.isScrolledToTop()) {
                scroll_view.fling(0)
                scroll_view.smoothScrollTo(0, container_comment_layout.top)
            } else {
                val layoutManager: LinearLayoutManager = rv.layoutManager as LinearLayoutManager
                mRememberCommentScrollPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                rv.stopScroll()
                rv.smoothScrollToPosition(0)
                scroll_view.fling(0)
                scroll_view.smoothScrollTo(0, container_comment_layout.top)
            }
        } else {
            if (mRememberCommentScrollPosition != -1) {
                rv.stopScroll()
                rv.smoothScrollToPosition(mRememberCommentScrollPosition)
                mRememberCommentScrollPosition = -1
            }
            scroll_view.fling(0)
            scroll_view.smoothScrollTo(0, mRememberScrollY)
            mRememberScrollY = -1
        }
    }
}


