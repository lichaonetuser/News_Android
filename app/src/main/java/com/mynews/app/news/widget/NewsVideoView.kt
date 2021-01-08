package com.mynews.app.news.widget

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import androidx.annotation.NonNull

import androidx.constraintlayout.widget.ConstraintLayout
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.mynews.app.news.BuildConfig
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.Tag
import com.mynews.app.news.bean.Video
import com.mynews.app.news.bean.VideoUrls
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.ClarityChangeEvent
import com.mynews.app.news.item.MoreItem
import com.mynews.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.GSYVideoTransferUtils
import com.mynews.app.news.util.ShareUtils
import com.mynews.app.news.util.TimeUtils
import com.mynews.app.news.util.ToastUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.app.activity.CoreBaseActivity
import com.mynews.common.core.app.fragment.CoreBaseFragment
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.computationToMain
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.share.ContentLink
import com.mynews.common.extension.share.IShareListener
import com.mynews.common.extension.share.ShareManager
import com.mynews.common.extension.share.SharePlatform
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.shuyu.gsyvideoplayer.video.base.GSYVideoViewBridge
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.video_clarity_container.view.*
import kotlinx.android.synthetic.main.video_news_player.view.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.dip
import org.jetbrains.anko.findOptional
import org.jetbrains.anko.imageResource
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import java.io.File
import java.util.concurrent.TimeUnit

class NewsVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : GSYVideoView(context, attrs) {

    val LOGGER_TAG = "PLAYER"

    /************************* 枚举 *************************/

    enum class StartType {
        NORMAL, REPLAY, RETRY, TRANSFER
    }

    /************************* 私有变量 *************************/

    private var mSetUpLazy: Boolean = false
    private var mVideo: Video = Video()
    private var mStyle: LayoutStyle = LayoutStyle.DETAIL
    private var mPreviousState: Int = GSYVideoView.CURRENT_STATE_NORMAL
    private var mTrackingTouchSeek: Boolean = false
    private var mAnalyticsEventKey: String = AnalyticsKey.Event.VIDEO_DETAIL
    private var mBuffterPoint: Int = 0

    /************************* 公开变量 *************************/

    var playListener: (() -> Unit)? = null
    var onClickThumbImgListener: (() -> Unit)? = null

    /************************* 重写抽象接口 *************************/

    override fun releaseVideos() {
        GSYVideoManager.releaseAllVideos()
    }

    override fun getGSYVideoManager(): GSYVideoViewBridge {
        GSYVideoManager.instance().initContext(context.applicationContext)
        return GSYVideoManager.instance()
    }

    override fun backFromFull(context: Context?): Boolean {
        return true
    }

    override fun startPlayLogic() {
        this.startPlayLogic(StartType.NORMAL)
    }

    fun startPlayLogic(type: StartType) {
        if (type == StartType.TRANSFER && mVideo.aid == GSYVideoTransferUtils.video?.aid) {
            mFirstStartTimestamp = GSYVideoTransferUtils.firstStartTimestamp
            mAllPlayDuration = GSYVideoTransferUtils.allPlayDuration
        }

        changeUiToPreparingShow()
        if (mVideo.sourceType == DataDictionary.SourceType.YOUTUBE.value) {
            if (BuildConfig.DEBUG) {
                ToastUtils.showToast("This is A YOUTUBE VIDEO")
            }
            requestYoutubeUrls(type)
            return
        }
        prepareVideo(type)
    }

    override fun setStateAndUi(state: Int) {
        Logger.tag(LOGGER_TAG).d("setStateAndUi : $state")
        //TRY FIX 锁屏后部分情况会一直回调这个状态，但实际上视频是播放着的
        if (mPreviousState == GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START &&
                state == GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START) {
            if (GSYVideoManager.instance().player.mediaPlayer.isPlaying) {
                setStateAndUi(GSYVideoView.CURRENT_STATE_PLAYING)
            }
            return
        }
        mPreviousState = mCurrentState
        mCurrentState = state
        when (mCurrentState) {
            GSYVideoView.CURRENT_STATE_NORMAL -> {
                if (isCurrentMediaListener) {
                    GSYVideoManager.instance().releaseMediaPlayer()
                    releasePauseCover()
                    mBuffterPoint = 0
                }
//                if (mAudioManager != null) {
//                    mAudioManager.abandonAudioFocus(onFocusChangeListener)
//                }
                releaseNetWorkState()
                playListener?.invoke()
            }
            GSYVideoView.CURRENT_STATE_PREPAREING -> {
                playListener?.invoke()
            }
            GSYVideoView.CURRENT_STATE_PLAYING -> {
                playListener?.invoke()
            }
            GSYVideoView.CURRENT_STATE_PAUSE -> {
            }
            GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START -> {
            }
            GSYVideoView.CURRENT_STATE_ERROR -> {
                if (isCurrentMediaListener) {
                    GSYVideoManager.instance().releaseMediaPlayer()
                }
            }
            GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                playListener?.invoke()
            }
        }
        resolveAppLogEvent(state)
        resolveUIState(state)
        resolveTimerState(state)
    }

    override fun onBufferingUpdate(percent: Int) {
        Logger.d("onBufferingUpdate : $percent")
        if (mCurrentState != GSYVideoView.CURRENT_STATE_NORMAL && mCurrentState != GSYVideoView.CURRENT_STATE_PREPAREING) {
            if (percent > 0) {
                mBuffterPoint = percent
                updateBufferProgress(bufferProgress = percent)
            }
            if (mLooping && mHadPlay && percent == 0 && progress_seek.progress >= progress_seek.max - 1) {
                minProgress()
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.video_news_player
    }

    override fun onBackFullscreen() {
        val activity = context as? CoreBaseActivity ?: return
        exitFullScreen(activity)
    }

    override fun setSmallVideoTextureView() {
        // WILL DO 小窗逻辑
    }

    /************************* 重写非抽象接口 *************************/

    override fun startPrepare() {
        this.startPrepare(StartType.NORMAL)
    }

    fun startPrepare(type: StartType) {
        if (GSYVideoManager.instance().listener() != null) {
            if (type == StartType.NORMAL || type == StartType.TRANSFER) {
                GSYVideoManager.instance().listener().onCompletion()
            } else {
                super.onCompletion()
            }
        }
        GSYVideoManager.instance().setListener(this)
        GSYVideoManager.instance().playTag = mPlayTag
        GSYVideoManager.instance().playPosition = mPlayPosition
        mAudioManager.requestAudioFocus(onFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
        (activityContext as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mBackUpPlayingBufferState = -1
        GSYVideoManager.instance().prepare(mUrl, mMapHeadData, mLooping, mSpeed, false, null)
        setStateAndUi(CURRENT_STATE_PREPAREING)
    }

    override fun prepareVideo() {
        this.prepareVideo(StartType.NORMAL)
    }

    /**
     * 增对列表优化，在播放前的时候才进行setup
     */
    fun prepareVideo(type: StartType) {
        try {
            if (mSetUpLazy) {
                super.setUp(mOriginUrl,
                        mCache,
                        mCachePath,
                        mMapHeadData,
                        mTitle)
            }
            startPrepare(type)
            addTextureView()
        } catch (e: Exception) {
            setStateAndUi(CURRENT_STATE_ERROR)
        }
    }

    override fun onVideoResume() {
        super.onVideoResume()
        mAudioManager.requestAudioFocus(onFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
    }

    override fun onCompletion() {
        super.onCompletion()
        Logger.tag(LOGGER_TAG).d("setStateAndUi : onCompletion")
        if (GSYVideoTransferUtils.transferFlag) {
            GSYVideoTransferUtils.transferFlag = false
            return
        }
        Logger.tag(LOGGER_TAG).d("recordProgressEvent when onCompletion")
        recordProgressEvent()
        Logger.tag(LOGGER_TAG).d("recordTotalProgressEvent when onCompletion")
        recordTotalProgressEvent()
    }

    override fun onAutoCompletion() {
        setStateAndUi(CURRENT_STATE_AUTO_COMPLETE)

        mSaveChangeViewTIme = 0

//        if (mTextureViewContainer.childCount > 0) {
//            mTextureViewContainer.removeAllViews()
//        }

        if (!mIfCurrentIsFullscreen)
            GSYVideoManager.instance().setLastListener(null)
//        mAudioManager.abandonAudioFocus(onFocusChangeListener)
        (activityContext as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        releaseNetWorkState()

        if (mVideoAllCallBack != null && isCurrentMediaListener) {
            Debuger.printfLog("onAutoComplete")
            mVideoAllCallBack.onAutoComplete(mOriginUrl, mTitle, this)
        }
    }

    override fun setUp(aUrl: String?, cacheWithPlay: Boolean, cachePath: File?, title: String?): Boolean {
        var url = aUrl ?: ""
        mCache = cacheWithPlay
        mCachePath = cachePath
        mOriginUrl = url
        if (isCurrentMediaListener && System.currentTimeMillis() - mSaveChangeViewTIme < CHANGE_DELAY_TIME)
            return false
        mCurrentState = CURRENT_STATE_NORMAL
//        if (cacheWithPlay && url.startsWith("http") && !url.contains("127.0.0.1") && !url.contains(".m3u8")) {
//            val proxy = GSYVideoManager.getProxy(activityContext!!.applicationContext, cachePath)
//            //此处转换了url，然后再赋值给mUrl。
//            url = proxy.getProxyUrl(url)
//            mCacheFile = !url.startsWith("http")
//            //注册上缓冲监听
//            if (!mCacheFile && GSYVideoManager.instance() != null) {
//                proxy.registerCacheListener(GSYVideoManager.instance(), mOriginUrl)
//            }
//        } else if (!cacheWithPlay && (!url.startsWith("http") && !url.startsWith("rtmp")
//                        && !url.startsWith("rtsp") && !url.contains(".m3u8"))) {
//            mCacheFile = true
//        }
        this.mUrl = url
        this.mTitle = title
        return true
    }

    /************************* 初始化 *************************/

    override fun init(context: Context?) {
        super.init(context)
        initUI()
    }

    private fun initUI() {
        video_thumb_layout.setOnClickListener { onClickThumbImg() }
        video_play_btn.setOnClickListener { onClickPlayBtn() }
        detail_top_layout_share_btn.setOnClickListener { onClickShare() }
        detail_top_layout_back_btn.setOnClickListener { onClickBackFullscreen() }
        video_share_facebook.setOnClickListener { onClickShareFacebook() }
        video_share_twitter.setOnClickListener { onClickShareTwitter() }
        video_share_line.setOnClickListener { onClickShareLine() }
        video_replay_btn.setOnClickListener { onClickReplay() }
        video_retry_btn.setOnClickListener { onClickRetry() }
        video_clarity_btn.setOnClickListener { onClickClarity() }
        video_fullscreen_btn.setOnClickListener { onClickFullscreen() }
        surface_container.setOnClickListener { onClickSurface() }
        video_control_mask_view.setOnClickListener { onClickVideoControlMask() }
        progress_seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                try {
                    if (fromUser) {
                        cancelDismissControlViewTimer()
                        cancelProgressTimer()
                        val time = progress * duration / seekBar.max
                        current_time_txt.text = TimeUtils.getVideoDurationText(time.toLong())
                    }
                } catch (e: Exception) {
                    Logger.e(e)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                mTrackingTouchSeek = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Logger.tag(LOGGER_TAG).d("recordProgressEvent when onStopTrackingTouch")
                recordProgressEvent()
                mTrackingTouchSeek = false
                if (mVideoAllCallBack != null && isCurrentMediaListener) {
                    if (isIfCurrentIsFullscreen) {
                        Debuger.printfLog("onClickSeekbarFullscreen")
                        mVideoAllCallBack.onClickSeekbarFullscreen(mOriginUrl, mTitle, this)
                    } else {
                        Debuger.printfLog("onClickSeekbar")
                        mVideoAllCallBack.onClickSeekbar(mOriginUrl, mTitle, this)
                    }
                }
                if (GSYVideoManager.instance().player.mediaPlayer != null && mHadPlay) {
                    try {
                        val time = seekBar.getProgress() * duration / seekBar.max
                        GSYVideoManager.instance().player.mediaPlayer.seekTo(time.toLong())
                    } catch (e: Exception) {
                        Debuger.printfWarning(e.toString())
                    }

                }
            }

        })
    }

    /************************* 点击事件 *************************/

    /**
     * 预览图点击
     */
    private fun onClickThumbImg() {
        onClickThumbImgListener?.invoke()
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_PLAY)
        if (mStyle != LayoutStyle.NO_CONTROL) {
            startPlayLogic()
        }
    }

    /**
     * 播放按键点击
     */
    private fun onClickPlayBtn() {
        if (TextUtils.isEmpty(mUrl)) {
            return
        }
        when (mCurrentState) {
            GSYVideoView.CURRENT_STATE_NORMAL, GSYVideoView.CURRENT_STATE_AUTO_COMPLETE, GSYVideoView.CURRENT_STATE_ERROR -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_PLAY)
                startButtonLogic()
            }
            GSYVideoView.CURRENT_STATE_PLAYING, GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_PAUSE)
                cancelDismissControlViewTimer()
                GSYVideoManager.instance().player.mediaPlayer.pause()
                setStateAndUi(GSYVideoView.CURRENT_STATE_PAUSE)
                if (mVideoAllCallBack != null && isCurrentMediaListener) {
                    if (mIfCurrentIsFullscreen) {
                        mVideoAllCallBack.onClickStopFullscreen(mOriginUrl, mTitle, this)
                    } else {
                        mVideoAllCallBack.onClickStop(mOriginUrl, mTitle, this)
                    }
                }
            }
            GSYVideoView.CURRENT_STATE_PAUSE -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_PLAY)
                startDismissControlViewTimer()
                if (mVideoAllCallBack != null && isCurrentMediaListener) {
                    if (mIfCurrentIsFullscreen) {
                        mVideoAllCallBack.onClickResumeFullscreen(mOriginUrl, mTitle, this)
                    } else {
                        mVideoAllCallBack.onClickResume(mOriginUrl, mTitle, this)
                    }
                }
                GSYVideoManager.instance().player.mediaPlayer.start()
                setStateAndUi(GSYVideoView.CURRENT_STATE_PLAYING)
            }
        }
    }

    private fun onClickShareFacebook() {
        shareLinkOnEnd(SharePlatform.FACEBOOK)
    }

    private fun onClickShareTwitter() {
        shareLinkOnEnd(SharePlatform.TWITTER)
    }

    private fun onClickShareLine() {
        shareLinkOnEnd(SharePlatform.LINE)
    }

    private fun onClickShare() {
        val activity = context
        if (activity is CoreBaseActivity) {
            activity.callRootFragmentStart(CoreBaseFragment.instantiate(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                    .builder(mVideo, Channel(), mAnalyticsEventKey, "")
                    .mMoreShare(arrayListOf(
                            MoreItem.More(MoreItem.More.Type.SHARE_FACEBOOK, R.drawable.share_facebook, ResUtils.getString(R.string.Common_Facebook)),
                            MoreItem.More(MoreItem.More.Type.SHARE_TWITTER, R.drawable.share_twitter, ResUtils.getString(R.string.Common_Twitter)),
                            MoreItem.More(MoreItem.More.Type.SHARE_LINE, R.drawable.share_line, ResUtils.getString(R.string.Common_LINE))))
                    .mMoreAction(arrayListOf(
                            MoreItem.More(MoreItem.More.Type.SHARE_COPY, R.drawable.share_copy, ResUtils.getString(R.string.Common_CopyLink)),
                            MoreItem.More(MoreItem.More.Type.SHARE_SYSTEM, R.drawable.share_system, ResUtils.getString(R.string.Common_SystemShare)),
                            MoreItem.More(MoreItem.More.Type.SHARE_MESSAGE, R.drawable.share_messages, ResUtils.getString(R.string.Common_Message)),
                            MoreItem.More(MoreItem.More.Type.SHARE_MAIL, R.drawable.share_mail, ResUtils.getString(R.string.Common_Mail)),
                            MoreItem.More(MoreItem.More.Type.ACTION_REPORT, R.drawable.share_report, ResUtils.getString(R.string.Common_Report))
                    ))
                    .mReferName(AppLogKey.Refer.VIDEO_PLAYER)
                    .mReferId(mVideo.aid)
                    .bundle()))
        }
    }

    private fun onClickRetry() {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.VIDEO_ERROR_REPLAY)
        startPlayLogic(StartType.RETRY)
    }

    private fun onClickReplay() {
        AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.VIDEO_END_REPLAY)
        if (mIfCurrentIsFullscreen) {
            val activity = context as? Activity ?: return
            activity.window.decorView.systemUiVisibility = mSystemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        startPlayLogic(StartType.REPLAY)
    }

    private fun onClickSurface() {
        showAllControlViewByStyle()
        startDismissControlViewTimer()

        // FIXME 临时代码
        if (mStyle == LayoutStyle.FULL_SCREEN) {
            detail_top_layout_share_btn.visibility = View.INVISIBLE
        } else if (mStyle == LayoutStyle.DETAIL) {
            detail_top_layout_share_btn.visibility = View.VISIBLE
        }
    }

    private fun onClickClarity() {
        showClarityWindow()
    }

    private fun onClickFullscreen() {
        toggleWindowFullScreen()
    }

    private fun onClickBackFullscreen() {
        exitFullScreen(context as? Activity ?: return)
    }

    private fun onClickVideoControlMask() {
        cancelDismissControlViewTimer()
        hideAllControlViewByStyle()
    }

    /************************* 计时器状态机相关 *************************/

    private fun resolveTimerState(state: Int) {
        when (state) {
            GSYVideoView.CURRENT_STATE_NORMAL -> {
                if (isCurrentMediaListener) {
                    cancelProgressTimer()
                }
                cancelDismissControlViewTimer()
            }
            GSYVideoView.CURRENT_STATE_PREPAREING -> {
                startDismissControlViewTimer()
            }
            GSYVideoView.CURRENT_STATE_PLAYING -> {
                startProgressTimer()
                startDismissControlViewTimer()
            }
            GSYVideoView.CURRENT_STATE_PAUSE -> {
                cancelDismissControlViewTimer()
            }
            GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                cancelProgressTimer()
                cancelDismissControlViewTimer()
            }
        }
    }

    private var mDismissControlDisposable: Disposable? = null

    private val mDismissControlTimer by lazy {
        Single.just(0)
                .delay(3000, TimeUnit.MILLISECONDS)
                .onErrorReturn { -1 }
                .computationToMain()
                .doOnSuccess {
                    hideAllControlViewByStyle()
                    cancelDismissControlViewTimer()
                }
    }

    private fun startDismissControlViewTimer() {
        if (mDismissControlDisposable == null) {
            mDismissControlDisposable = mDismissControlTimer.subscribe()
        }
    }

    private fun cancelDismissControlViewTimer() {
        mDismissControlDisposable?.dispose()
        mDismissControlDisposable = null
    }

    private var mProgressDisposable: Disposable? = null

    private val mProgressTimer by lazy {
        Observable.interval(0, 1000, TimeUnit.MILLISECONDS)
                .onErrorReturn { -1 }
                .map {
                    var buffterPoint = getBuffterPoint()
                    // EXOPLAYER不回调缓冲进度，这里手动获取下替代
                    if (PlayerFactory.getPlayManager() == Exo2PlayerManager::class.java) {
                        val mediaPlayer = GSYVideoManager.instance().player.mediaPlayer as? IjkExo2MediaPlayer
                        buffterPoint = mediaPlayer?.bufferedPercentage ?: 0
                        Logger.d("buffterPoint : $buffterPoint")
                    }
                    arrayOf(getProgress(), buffterPoint, getCurrentPositionWhenPlaying(), getDuration())
                }
                .computationToMain()
                .doOnNext { it ->
                    updateProgress(it[0], it[1], it[2], it[3])
                }.doOnComplete {
                    cancelProgressTimer()
                }
    }

    private fun startProgressTimer() {
        if (mProgressDisposable == null) {
            mProgressDisposable = mProgressTimer.subscribe()
        }
    }

    private fun cancelProgressTimer() {
        mProgressDisposable?.dispose()
        mProgressDisposable = null
    }

    /************************* UI状态机相关 *************************/

    private fun resolveUIState(state: Int) {
        when (state) {
            GSYVideoView.CURRENT_STATE_NORMAL -> changeUiToNormal()
            GSYVideoView.CURRENT_STATE_PREPAREING -> changeUiToPreparingShow()
            GSYVideoView.CURRENT_STATE_PLAYING -> changeUiToPlayingShow()
            GSYVideoView.CURRENT_STATE_PAUSE -> changeUiToPauseShow()
            GSYVideoView.CURRENT_STATE_ERROR -> changeUiToErrorShow()
            GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START -> changeUiToPlayingBufferingShow()
            GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> changeUiToCompleteShow()
        }
    }

    private fun changeUiToNormal() {
        hideAllControlViewByStyle()

        video_thumb_layout.visibility = View.VISIBLE
        video_thumb_play_img.visibility = View.VISIBLE
        video_loading_layout.visibility = View.INVISIBLE
        video_duration_txt.visibility = View.VISIBLE
        retry_layout.visibility = View.INVISIBLE
        video_share_layout.visibility = View.INVISIBLE
        bottom_progress.visibility = View.INVISIBLE
        video_play_btn.imageResource = R.drawable.video_play_btn

        getTopMaskViewByStyle().visibility = View.VISIBLE
        minProgress()
        when (mStyle) {
            LayoutStyle.LIST -> {
                list_top_layout.visibility = View.INVISIBLE
                list_top_layout_title_txt.visibility = View.INVISIBLE
                tag_layout.visibility = View.VISIBLE
            }
            LayoutStyle.DETAIL -> {
                detail_top_layout.visibility = View.VISIBLE
                detail_top_layout_title_txt.visibility = View.INVISIBLE
                detail_top_layout_share_btn.visibility = View.VISIBLE
            }
            LayoutStyle.FULL_SCREEN -> {
                showAllControlViewByStyle(getTopLayoutByStyle(LayoutStyle.FULL_SCREEN))
                detail_top_layout_mask_view.visibility = View.INVISIBLE
                detail_top_layout_share_btn.visibility = View.INVISIBLE
            }
            LayoutStyle.NO_CONTROL -> {
                if (isLooping) {
                    video_thumb_layout.visibility = View.GONE
                } else {
                    video_thumb_layout.visibility = View.VISIBLE
                }
                list_top_layout.visibility = View.GONE
                video_thumb_play_img.visibility = View.GONE
                video_duration_txt.visibility = View.GONE
            }
        }
    }

    fun changeUiToPreparingShow() {
        hideAllControlViewByStyle()

        video_thumb_layout.visibility = View.VISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.VISIBLE
        video_duration_txt.visibility = View.INVISIBLE
        retry_layout.visibility = View.INVISIBLE
        video_share_layout.visibility = View.INVISIBLE
        bottom_progress.visibility = View.INVISIBLE
        video_play_btn.imageResource = R.drawable.video_pause_btn

        getTopMaskViewByStyle().visibility = View.INVISIBLE
        getTitleTextViewByStyle().visibility = View.INVISIBLE
        minProgress()

        if (isLooping) {
            video_thumb_layout.visibility = View.INVISIBLE
        } else {
            video_thumb_layout.visibility = View.VISIBLE
        }
    }

    private fun changeUiToPlayingShow() {
        video_thumb_layout.visibility = View.INVISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.INVISIBLE
        video_duration_txt.visibility = View.INVISIBLE
        retry_layout.visibility = View.INVISIBLE
        video_share_layout.visibility = View.INVISIBLE
        video_play_btn.imageResource = R.drawable.video_pause_btn

        if (!progress_seek.isShown) {
            bottom_progress.visibility = View.VISIBLE
        } else {
            bottom_progress.visibility = View.INVISIBLE
        }
        if (mStyle == LayoutStyle.NO_CONTROL) {
            bottom_progress.visibility = View.GONE
        }
    }

    private fun changeUiToPauseShow() {
        video_thumb_layout.visibility = View.INVISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.INVISIBLE
        video_duration_txt.visibility = View.INVISIBLE
        retry_layout.visibility = View.INVISIBLE
        video_share_layout.visibility = View.INVISIBLE
//        bottom_progress.visibility = bottom_progress.visibility
        video_play_btn.imageResource = R.drawable.video_play_btn

        if (mStyle == LayoutStyle.NO_CONTROL) {
            bottom_progress.visibility = View.GONE
        }
    }

    fun changeUiToErrorShow() {
        hideAllControlViewByStyle()

        video_thumb_layout.visibility = View.INVISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.INVISIBLE
        video_duration_txt.visibility = View.INVISIBLE
//        retry_layout.visibility = View.VISIBLE
        video_share_layout.visibility = View.INVISIBLE
        bottom_progress.visibility = View.INVISIBLE
        video_play_btn.imageResource = R.drawable.video_play_btn

        if (mStyle == LayoutStyle.NO_CONTROL) {
            retry_layout.visibility = View.GONE
        } else {
            retry_layout.visibility = View.VISIBLE
        }
    }

    private fun changeUiToPlayingBufferingShow() {
        video_thumb_layout.visibility = View.INVISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.VISIBLE
        video_duration_txt.visibility = View.INVISIBLE
        retry_layout.visibility = View.INVISIBLE
        video_share_layout.visibility = View.INVISIBLE
//        bottom_progress.visibility = bottom_progress.visibility
        video_play_btn.imageResource = R.drawable.video_pause_btn

        if (mStyle == LayoutStyle.NO_CONTROL) {
            bottom_progress.visibility = View.GONE
        }
    }

    private fun changeUiToCompleteShow() {
        hideAllControlViewByStyle()

//        video_thumb_layout.visibility = View.INVISIBLE
        video_thumb_play_img.visibility = View.INVISIBLE
        video_loading_layout.visibility = View.INVISIBLE
        video_duration_txt.visibility = View.INVISIBLE
        retry_layout.visibility = View.INVISIBLE
//        video_share_layout.visibility = View.VISIBLE
//        bottom_progress.visibility = bottom_progress.visibility
        video_play_btn.imageResource = R.drawable.video_play_btn

        maxProgress()

        if (mStyle == LayoutStyle.NO_CONTROL) {
            bottom_progress.visibility = View.GONE
            video_share_layout.visibility = View.GONE
            if (isLooping) {
                video_thumb_layout.visibility = View.INVISIBLE
            } else {
                video_thumb_layout.visibility = View.VISIBLE
            }
        } else {
            video_share_layout.visibility = View.VISIBLE
            video_thumb_layout.visibility = View.INVISIBLE
        }
    }

    /************************* 设置数据源相关 *************************/

//    fun setUp(@NonNull video: Video, cacheWithPlay: Boolean = false, cachePath: File? = null, style: LayoutStyle = LayoutStyle.DETAIL) {
//        mVideo = video
//        resetUIForUpdateVideo(video, style)
//        super.setUp(getVideoOriginUrl(), cacheWithPlay, cachePath, video.title)
//    }
    fun setUpLazy(@NonNull video: Video,
                  @NonNull analyticsEventKey: String,
                  cacheWithPlay: Boolean = false,
                  cachePath: File? = null,
                  mapHeadData: Map<String, String> = hashMapOf(),
                  style: LayoutStyle = LayoutStyle.LIST,
                  positionSourceRefer: AppLog.Refer,
                  positionRefer: AppLog.Refer) {
        mVideo = video
        mCache = cacheWithPlay
        mCachePath = cachePath
        mMapHeadData = mapHeadData
        mOriginUrl = getVideoOriginUrl()
        mSetUpLazy = true
        mPositionSourceRefer = positionSourceRefer
        mPositionRefer = positionRefer
        mAnalyticsEventKey = analyticsEventKey
        resetUIForUpdateVideo(video, style)
        checkClarityEnable()
    }

    private fun resetUIForUpdateVideo(@NonNull video: Video, style: LayoutStyle) {
        setLayoutStyle(style)
        setStateAndUi(CURRENT_STATE_NORMAL)
        video_duration_txt.text = TimeUtils.getVideoDurationText(video.durationInterval * 1000)
        video_thumb_img.visibility = View.VISIBLE
        ImageManager.with(video_thumb_img).load(mVideo.coverImage)
        var list = arrayListOf<String>()
        for (i in mVideo.tags.indices) {
            try {
                list.add(getTagNameSafe(mVideo.tags[i]))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        tag_layout.setContent(list, R.drawable.video_tag, 11f, "#fff2be", EnvDisplayMetrics.WIDTH_PIXELS, 55, 45, 6, 19, 13)
    }

    private fun getTagNameSafe(obj: Any): String {
        return try {
            if (obj is Tag) {
                obj.name
            } else {
                try {
                    (obj as Tag).name
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    val tag = obj as Map<String, Any>
                    tag["name"].toString()
                }
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun getVideoOriginUrl(): String? {
        val defaultUrl = when (DataManager.Local.getVideoClarity()) {
            Clarity.NORMAL -> mVideo.normalUrl
            Clarity.SD -> mVideo.sdUrl
            Clarity.HD -> mVideo.hdUrl
        }
        if (defaultUrl.isNotBlank()) {
            return defaultUrl
        }

        return when {
            mVideo.normalUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.NORMAL
                mVideo.normalUrl
            }
            mVideo.sdUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.SD
                mVideo.sdUrl
            }
            mVideo.hdUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.HD
                mVideo.hdUrl
            }
            else -> ""
        }
    }

    /************************* 风格样式相关 *************************/

    fun getTopMaskViewByStyle(style: LayoutStyle = mStyle): View {
        return when (style) {
            LayoutStyle.LIST -> list_top_layout_mask_view
            LayoutStyle.DETAIL, LayoutStyle.FULL_SCREEN -> detail_top_layout_mask_view
            LayoutStyle.NO_CONTROL -> View(context)
        }
    }

    fun getTitleTextViewByStyle(style: LayoutStyle = mStyle): TextView {
        return when (style) {
            LayoutStyle.LIST -> list_top_layout_title_txt
            LayoutStyle.DETAIL, LayoutStyle.FULL_SCREEN -> detail_top_layout_title_txt
            LayoutStyle.NO_CONTROL -> TextView(context)
        }
    }

    fun getTopLayoutByStyle(style: LayoutStyle = mStyle): ConstraintLayout {
        return when (style) {
            LayoutStyle.LIST -> list_top_layout
            LayoutStyle.DETAIL, LayoutStyle.FULL_SCREEN -> detail_top_layout
            LayoutStyle.NO_CONTROL -> ConstraintLayout(context)
        }
    }

    fun showAllControlViewByStyle(topLayout: ViewGroup = getTopLayoutByStyle()) {
        val childCount = topLayout.childCount
        for (i in 0 until childCount) {
            val childView = topLayout.getChildAt(i)
            if (mStyle != LayoutStyle.DETAIL && childView != detail_top_layout_mask_view) {
                childView.visibility = View.VISIBLE
            }
        }
        if (mStyle != LayoutStyle.LIST) {
            topLayout.visibility = View.VISIBLE
        }
//        video_bottom_layout.visibility = View.VISIBLE
        bottom_progress.visibility = View.INVISIBLE

        if (mStyle == LayoutStyle.NO_CONTROL) {
            video_bottom_layout.visibility = View.GONE
        } else {
            video_bottom_layout.visibility = View.VISIBLE
            video_play_btn.visibility = View.VISIBLE
            video_control_mask_view.visibility = View.VISIBLE
        }
    }

    fun hideAllControlViewByStyle(topLayout: ViewGroup = getTopLayoutByStyle()) {
        val childCount = topLayout.childCount
        for (i in 0 until childCount) {
            topLayout.getChildAt(i).visibility = View.INVISIBLE
        }
        topLayout.visibility = View.INVISIBLE
        video_bottom_layout.visibility = View.INVISIBLE
        video_play_btn.visibility = View.INVISIBLE
        video_control_mask_view.visibility = View.INVISIBLE
//        bottom_progress.visibility = View.VISIBLE

        if (mStyle == LayoutStyle.NO_CONTROL) {
            bottom_progress.visibility = View.GONE
        } else {
            bottom_progress.visibility = View.VISIBLE
        }
    }

    fun setLayoutStyle(style: LayoutStyle) {
        mStyle = style
        when (style) {
            LayoutStyle.LIST -> {
                list_top_layout.visibility = View.INVISIBLE
                list_top_layout_title_txt.visibility = View.INVISIBLE
                list_top_layout_title_txt.text = mVideo.title
                tag_layout.visibility = View.VISIBLE
                detail_top_layout.visibility = View.GONE
            }
            LayoutStyle.DETAIL -> {
                list_top_layout.visibility = View.GONE

                detail_top_layout.visibility = View.VISIBLE
                detail_top_layout_back_btn.visibility = View.INVISIBLE
                detail_top_layout_title_txt.visibility = View.INVISIBLE
                tag_layout.visibility = View.INVISIBLE
                detail_top_layout_share_btn.visibility = View.VISIBLE
            }
            LayoutStyle.FULL_SCREEN -> {
                list_top_layout.visibility = View.INVISIBLE
                detail_top_layout_title_txt.text = mVideo.title
                tag_layout.visibility = View.INVISIBLE
                showAllControlViewByStyle(getTopLayoutByStyle(LayoutStyle.FULL_SCREEN))
                detail_top_layout_mask_view.visibility = View.INVISIBLE
                detail_top_layout_share_btn.visibility = View.INVISIBLE
            }
            LayoutStyle.NO_CONTROL -> {
                video_thumb_play_img.visibility = View.GONE
                video_play_btn.visibility = View.GONE
                retry_layout.visibility = View.GONE
                detail_top_layout.visibility = View.GONE
                list_top_layout.visibility = View.GONE
                video_share_layout.visibility = View.GONE
                bottom_progress.visibility = View.GONE
                video_duration_txt.visibility = View.GONE
            }
        }
    }

    enum class LayoutStyle {
        LIST, DETAIL, FULL_SCREEN, NO_CONTROL
    }

    /************************* 进度条更新相关 *************************/


    private fun getProgress(): Int {
        val position = currentPositionWhenPlaying
        val duration = duration
        return position * progress_seek.max / if (duration <= 0) 1 else duration
    }

    protected fun updateBufferProgress(bufferProgress: Int = getBuffterPoint()) {
        // 考虑到可能会出现缓存无法到达100的情况，或者到达100后不回调
        var secProgress = bufferProgress * 10
        if (bufferProgress > bottom_progress.max * 0.9) {
            secProgress = bottom_progress.max
        }
        if (bufferProgress > 0) {
            progress_seek.secondaryProgress = secProgress
            bottom_progress.secondaryProgress = secProgress
        }
    }

    protected fun updateProgress(progress: Int = getProgress(),
                                 bufferProgress: Int = getBuffterPoint(),
                                 currentPosition: Int = getCurrentPositionWhenPlaying(),
                                 duration: Int = getDuration()) {
        if (mCurrentState != CURRENT_STATE_PLAYING) {
            return
        }

        Logger.d("updateProgress\nprogress : $progress bufferProgress : $bufferProgress " +
                "currentPosition : $currentPosition duration : $duration")

        // 考虑到可能会出现缓存无法到达100的情况，或者到达100后不回调
        var secProgress = bufferProgress * 10
        if (bufferProgress > bottom_progress.max * 0.9) {
            secProgress = bottom_progress.max
        }

        if (bufferProgress > 0) {
            progress_seek.secondaryProgress = secProgress
            bottom_progress.secondaryProgress = secProgress
        }

        if (duration > 0) {
            total_time_txt.text = TimeUtils.getVideoDurationText(duration.toLong())
        }

        if (currentPosition > 0) {
            current_time_txt.text = TimeUtils.getVideoDurationText(currentPosition.toLong())
        }

        if (mTrackingTouchSeek) {
            return
        }

        if (progress > 0) {
            progress_seek.progress = progress
            bottom_progress.progress = progress
        }
    }

    private fun minProgress() {
        progress_seek.progress = 0
        progress_seek.secondaryProgress = 0
        bottom_progress.progress = 0
        bottom_progress.secondaryProgress = 0
        total_time_txt.text = TimeUtils.getVideoDurationText(0)
        current_time_txt.text = TimeUtils.getVideoDurationText(0)
    }

    private fun maxProgress() {
        progress_seek.progress = progress_seek.max
        bottom_progress.progress = bottom_progress.max
        current_time_txt.text = total_time_txt.text
    }

    /************************* YOUTUBE解析相关 *************************/

    private var mYouTubeVideoUrls: VideoUrls? = null

    private fun requestYoutubeUrls(type: StartType) {
        if (GSYVideoManager.instance().listener() != null) {
            if (type == StartType.NORMAL || type == StartType.TRANSFER) {
                GSYVideoManager.instance().listener().onCompletion()
            } else {
                super.onCompletion()
            }
        }
        DataManager.Remote.getYouTubeVideoUrl(mVideo.yVideoId)
                .ioToMain()
                .subscribeBy(
                        onNext = { it: VideoUrls ->
                            mYouTubeVideoUrls = it
                            mOriginUrl = getYouTubeVideoOriginUrl(type)
                            if (mOriginUrl.isNotEmpty()) {
                                prepareVideo()
                            } else {
                                changeUiToErrorShow()
                            }
                            checkClarityEnable()
                        },
                        onError = {
                            changeUiToErrorShow()
                        })
    }

    private fun getYouTubeVideoOriginUrl(type: StartType): String? {
        val urls = mYouTubeVideoUrls ?: return null
        val clarity = if (type == StartType.RETRY || type == StartType.REPLAY) {
            mCurrentClarity
        } else {
            DataManager.Local.getVideoClarity()
        }
        val defaultUrl = when (clarity) {
            Clarity.NORMAL -> urls.normalUrl
            Clarity.SD -> urls.sdUrl
            Clarity.HD -> urls.hdUrl
        }
        if (defaultUrl.isNotBlank()) {
            return defaultUrl
        }
        return when {
            urls.normalUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.NORMAL
                urls.normalUrl
            }
            urls.sdUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.SD
                urls.sdUrl
            }
            urls.hdUrl.isNotEmpty() -> {
                mCurrentClarity = Clarity.HD
                urls.hdUrl
            }
            else -> ""
        }
    }

    /************************* 清晰度选择相关 *************************/

    private var mCurrentClarity = DataManager.Local.getVideoClarity()

    enum class Clarity(val text: String) {
        SD(ResUtils.getString(R.string.Setting_SmoothDefinition)),
        NORMAL(ResUtils.getString(R.string.Setting_StandardDefinition)),
        HD(ResUtils.getString(R.string.Setting_HighDefinition));

        companion object {
            fun stringValueOf(value: String): Clarity {
                return when (value) {
                    SD.text -> Clarity.SD
                    NORMAL.text -> Clarity.NORMAL
                    HD.text -> Clarity.HD
                    else -> Clarity.NORMAL
                }
            }
        }
    }

    private fun showClarityWindow() {
        cancelDismissControlViewTimer()
        val urls = when (mVideo.sourceType) {
            DataDictionary.SourceType.YOUTUBE.value -> {
                val urls = mYouTubeVideoUrls ?: return
                arrayOf(urls.sdUrl, urls.normalUrl, urls.hdUrl)
            }
            DataDictionary.SourceType.MP4.value -> {
                arrayOf(mVideo.sdUrl, mVideo.normalUrl, mVideo.hdUrl)
            }
            else -> {
                return
            }
        }

        if (urls.isEmpty()) {
            return
        }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val clarityLayout = inflater.inflate(R.layout.video_clarity_container, null) as LinearLayout
        val clarityPopWindow = PopupWindow(clarityLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true)

        val clarityList = Clarity.values()
        for (i in urls.indices) {
            if (urls[i].isNotBlank()) {
                val clarityTextView = inflater.inflate(R.layout.video_clarity_item, null) as TextView
                clarityTextView.text = clarityList[i].text
                clarityTextView.isActivated = clarityList[i] == mCurrentClarity
                clarityLayout.item_container.addView(clarityTextView, dip(70), dip(35))
                clarityTextView.setOnClickListener {
                    when (clarityList[i]) {
                        NewsVideoView.Clarity.SD -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_360P)
                        NewsVideoView.Clarity.NORMAL -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_480P)
                        NewsVideoView.Clarity.HD -> AnalyticsManager.logEvent(AnalyticsKey.Event.SETTING, AnalyticsKey.Parameter.VIDEO_RESOLUTION_TO_720P)
                    }
                    mCurrentClarity = clarityList[i]
                    mSeekOnStart = currentPositionWhenPlaying.toLong()
                    super.setUp(urls[i], false, null, mVideo.title)
                    prepareVideo()
                    clarityPopWindow.dismiss()
                    video_clarity_btn.text = clarityTextView.text
                }
            }
        }

        clarityPopWindow.setBackgroundDrawable(ColorDrawable(ResUtils.getColor(R.color.transparent)))
        clarityPopWindow.contentView = clarityLayout

        clarityLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = clarityLayout.measuredWidth
        val popupHeight = clarityLayout.measuredHeight

        val location = IntArray(2)
        video_clarity_btn.getLocationOnScreen(location)

        val xOffset = -popupWidth / 2 + video_clarity_btn.width / 2
        val yOffset = -video_clarity_btn.height - popupHeight

        clarityPopWindow.setOnDismissListener {
            startDismissControlViewTimer()
        }
        clarityPopWindow.showAsDropDown(video_clarity_btn, xOffset, yOffset)
    }

    private fun checkClarityEnable() {
        when (mVideo.sourceType) {
            DataDictionary.SourceType.YOUTUBE.value -> {
                val urls = arrayListOf(mYouTubeVideoUrls?.sdUrl, mYouTubeVideoUrls?.normalUrl, mYouTubeVideoUrls?.hdUrl)
                val effectCount = urls.count { it?.isNotBlank() == true }
                video_clarity_btn.isEnabled = effectCount > 1
            }
            DataDictionary.SourceType.MP4.value -> {
                val urls = arrayListOf(mVideo.sdUrl, mVideo.normalUrl, mVideo.hdUrl)
                val effectCount = urls.count { it.isNotBlank() }
                video_clarity_btn.isEnabled = effectCount > 1
            }
        }
    }

    /************************* 分享相关 *************************/

    fun shareLinkOnEnd(platform: SharePlatform,
                       content: ContentLink = ShareUtils.getCommonContentLink(mVideo, platform),
                       listener: IShareListener = ShareUtils.getCommonShareListener(
                               analyticsEventKey = mAnalyticsEventKey,
                               analyticsParameterKeyPrefix = "video_end_",
                               itemId = mVideo.aid,
                               refer = AppLogKey.Refer.VIDEO_PLAYER)) {
        ShareManager.shareLink(context as? Activity ?: return, platform, content, listener)
    }

    /************************* 全屏相关 *************************/

    private var mFullscreenFromViewGroup: ViewGroup? = null
    private var mIsSwitchToFullscreen: Boolean = false
    private var mFullScreenNormalStyle: LayoutStyle = mStyle
    private var mSystemUiVisibility: Int = 0
    private var mTogglewFullScreenType: Int = CURRENT_STATE_NORMAL

    fun toggleWindowFullScreen() {
        try {
            val activity = context as? CoreBaseActivity ?: return
            mIfCurrentIsFullscreen = !mIfCurrentIsFullscreen
            if (mIfCurrentIsFullscreen) {
                enterFullScreen(activity)
            } else {
                exitFullScreen(activity)
            }
        } catch (e: Exception) {
            mIsSwitchToFullscreen = false
        }
    }


    private fun enterFullScreen(activity: Activity) {
        try {
            mTogglewFullScreenType = mCurrentState

            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_FULLSCREEN)

            mIsSwitchToFullscreen = true
            mFullScreenNormalStyle = mStyle
            setLayoutStyle(LayoutStyle.FULL_SCREEN)
            video_clarity_btn.visibility = View.VISIBLE
            video_clarity_btn.text = mCurrentClarity.text
            video_fullscreen_btn.setImageResource(R.drawable.video_zoom_out)

            mSystemUiVisibility = activity.window.decorView.systemUiVisibility
            activity.window.decorView.systemUiVisibility = mSystemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            GSYVideoManager.instance().player.mediaPlayer.pause()

            mIfCurrentIsFullscreen = true
            postDelayed({
                try {
                    val parent = video_view_root_layout.parent
                    if (parent != null && parent is ViewGroup) {
                        mFullscreenFromViewGroup = parent
                        parent.removeView(video_view_root_layout)
                    }
                    val activityRootContent = activity.findOptional<ViewGroup>(Window.ID_ANDROID_CONTENT)
                    val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    activityRootContent?.addView(video_view_root_layout, lp)

                    when (mTogglewFullScreenType) {
                        CURRENT_STATE_PLAYING -> GSYVideoManager.instance().player.mediaPlayer.start()
                    }
                } catch (e: Exception) {
                    setStateAndUi(CURRENT_STATE_ERROR)
                }
            }, 50)
        } catch (e: Exception) {
            setStateAndUi(CURRENT_STATE_ERROR)
        }
    }

    private fun exitFullScreen(activity: Activity) {
        try {
            mTogglewFullScreenType = mCurrentState
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_VIDEO_INLINE)

            mIsSwitchToFullscreen = false
            setLayoutStyle(mFullScreenNormalStyle)
            video_clarity_btn.visibility = View.GONE
            video_fullscreen_btn.setImageResource(R.drawable.video_zoom_in)

            activity.window.decorView.systemUiVisibility = mSystemUiVisibility
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            GSYVideoManager.instance().player.mediaPlayer.pause()
            mIfCurrentIsFullscreen = false

            postDelayed({
                try {
                    val parent = video_view_root_layout.parent
                    if (parent != null && parent is ViewGroup) {
                        parent.removeView(video_view_root_layout)
                    }
                    val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    mFullscreenFromViewGroup?.addView(video_view_root_layout, lp)

                    when (mTogglewFullScreenType) {
                        CURRENT_STATE_PLAYING -> GSYVideoManager.instance().player.mediaPlayer.start()
                    }
                } catch (e: Exception) {
                    setStateAndUi(CURRENT_STATE_ERROR)
                }
            }, 50)
        } catch (e: Exception) {
            setStateAndUi(CURRENT_STATE_ERROR)
        }
    }

    /**
     * 列表退出用
     */
    fun releaseOnItemDetachedFromWindow() {
        Logger.d("releaseOnItemDetachedFromWindow")
        if (mIsSwitchToFullscreen) {
            return
        }
        super.release()
    }

    companion object FullScreen {
        /**
         * 退出全屏，主要用于返回键
         *
         * @return 返回是，拦截了返回键
         */
        fun backFromWindowFull(context: Context): Boolean {
            val activity = context as? CoreBaseActivity ?: return false
            val activityRootContent = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val childCount = activityRootContent.childCount
            for (i in 0 until childCount) {
                if (activityRootContent.getChildAt(i).id == R.id.video_view_root_layout &&
                        GSYVideoManager.instance().listener() != null) {
                    GSYVideoManager.instance().listener().onBackFullscreen()
                    return true
                }
            }
            return false
        }
    }

    /************************* 服务端统计相关 *************************/

    private var mPositionSourceRefer: AppLog.Refer? = null
    private var mPositionRefer: AppLog.Refer? = null
    private var mStartTimestamp = 0L
    private var mPlayTime = 0L

    private var mFirstStartTimestamp = 0L
    private var mAllPlayDuration = 0L

    private fun resolveAppLogEvent(state: Int) {
        when (state) {
            GSYVideoView.CURRENT_STATE_NORMAL -> {
                Logger.tag(LOGGER_TAG).d("CURRENT_STATE_NORMAL : PreviousState = $mPreviousState")
            }
            GSYVideoView.CURRENT_STATE_PREPAREING -> {

            }
            GSYVideoView.CURRENT_STATE_PLAYING -> {
                mStartTimestamp = System.currentTimeMillis()
                if (mFirstStartTimestamp <= 0L) {
                    mFirstStartTimestamp = mStartTimestamp
                }
                mPlayTime = currentPositionWhenPlaying.toLong()
            }
            GSYVideoView.CURRENT_STATE_PAUSE -> {
                Logger.tag(LOGGER_TAG).d("recordProgressEvent when CURRENT_STATE_PAUSE")
                recordProgressEvent()
            }
            GSYVideoView.CURRENT_STATE_ERROR -> {
                Logger.tag(LOGGER_TAG).d("recordProgressEvent when CURRENT_STATE_ERROR")
                recordProgressEvent()
            }
            GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START -> {
                Logger.tag(LOGGER_TAG).d("recordProgressEvent when CURRENT_STATE_PLAYING_BUFFERING_START")
                recordProgressEvent()
            }
            GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                Logger.tag(LOGGER_TAG).d("recordProgressEvent when CURRENT_STATE_AUTO_COMPLETE")
                recordProgressEvent()
            }
        }
    }

    private fun recordProgressEvent() {
        if (mStartTimestamp != 0L) {
            val playDuration = System.currentTimeMillis() - mStartTimestamp
            if (playDuration >= 200) {
                mAllPlayDuration += playDuration
                Logger.tag("${LOGGER_TAG}_ProgressEvent ").d("AllPlayDuration : $mAllPlayDuration | PlayDuration : $playDuration")
                AppLogManager.logEvent(name = AppLog.EventName.EVENT_VIDEO,
                        label = AppLogKey.Label.PROGRESS,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(mVideo.aid)
                                .setPlay(AppLog.Play.newBuilder()
                                        .setStartTimestamp(mStartTimestamp)
                                        .setPositionSourceRefer(mPositionSourceRefer)
                                        .setPositionRefer(mPositionRefer)
                                        .setDuration(duration.toLong())
                                        .setPlayDuration(playDuration)
                                        .setPlayTime(mPlayTime)
                                )
                                .build())
            }
        }
        mStartTimestamp = 0
    }

    private fun recordTotalProgressEvent() {
        if (mFirstStartTimestamp != 0L) {
            AppLogManager.logEvent(name = AppLog.EventName.EVENT_VIDEO,
                    label = AppLogKey.Label.TOTAL_PROGRESS,
                    body = AppLog.EventBody.newBuilder()
                            .setItemId(mVideo.aid)
                            .setPlay(AppLog.Play.newBuilder()
                                    .setStartTimestamp(mFirstStartTimestamp)
                                    .setPositionSourceRefer(mPositionSourceRefer)
                                    .setPositionRefer(mPositionRefer)
                                    .setDuration(duration.toLong())
                                    .setPlayDuration(mAllPlayDuration)
                                    .setPlayTime(0)
                            )
                            .build())
        }
        mAllPlayDuration = 0
        mStartTimestamp = 0
        mFirstStartTimestamp = 0
        GSYVideoTransferUtils.firstStartTimestamp = 0
        GSYVideoTransferUtils.allPlayDuration = 0
        GSYVideoTransferUtils.transferFlag = false
        GSYVideoTransferUtils.video = null
    }

    /************************* 事件相关 *************************/

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        EventManager.register(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        EventManager.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onClarityChangeEvent(event: ClarityChangeEvent) {
        mCurrentClarity = event.newClarity
    }

    /************************* 播放焦点相关 *************************/

    protected var onFocusChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
            }
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                try {
                    if (GSYVideoManager.instance().player.mediaPlayer == null) {
                        return@OnAudioFocusChangeListener
                    }
                    if (GSYVideoManager.instance().player.isPlaying) {
                        GSYVideoManager.instance().player.pause()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
            }
        }
    }

    /************************* 外部UI调整相关 *************************/

    fun setCurrentTimeTxtLeft(margin: Int) {
        val params = current_time_txt.layoutParams as? MarginLayoutParams ?: return
        params.leftMargin = margin
        current_time_txt.layoutParams = params
    }

    /************************* 用来给服务端统计用的临时代码 *************************/

    override fun onInfo(what: Int, extra: Int) {
        super.onInfo(what, extra)
        if (what == GSYVideoTransferUtils.INFO_PREPARE_TRANSFER) {
            prepareTransfer()
        }
    }

    fun prepareTransfer() {
        recordProgressEvent()
        Logger.tag(LOGGER_TAG).d("prepareTransfer")
        GSYVideoTransferUtils.transferFlag = true
        GSYVideoTransferUtils.video = mVideo
        GSYVideoTransferUtils.firstStartTimestamp = mFirstStartTimestamp
        GSYVideoTransferUtils.allPlayDuration = mAllPlayDuration

        mFirstStartTimestamp = 0
        mAllPlayDuration = 0
    }

    fun sendClickAppLogEvent() {
        val currentTimeStamp = System.currentTimeMillis()
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_VIDEO,
                label = AppLogKey.Label.CLICK,
                body = AppLog.EventBody.newBuilder()
                        .setEnterTime(currentTimeStamp)
                        .setItemId(mVideo.aid)
                        .setPlay(AppLog.Play.newBuilder()
                                .setStartTimestamp(currentTimeStamp)
                                .setPositionSourceRefer(mPositionSourceRefer)
                                .setPositionRefer(mPositionRefer))
                        .build())
        AppLogManager.sendAppLog()
    }

}