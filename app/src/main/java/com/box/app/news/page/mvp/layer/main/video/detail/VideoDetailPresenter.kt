package com.box.app.news.page.mvp.layer.main.video.detail

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.account.AccountManager
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.ArticleDetail
import com.box.app.news.bean.ArticleInformation
import com.box.app.news.bean.Channel
import com.box.app.news.bean.Video
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.base.BaseNewsBean.Companion.FROM_VIDEO_DEATIL
import com.box.app.news.data.DataAction
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.CommentListChangeEvent
import com.box.app.news.event.change.NewsListChangeEvent
import com.box.app.news.item.MoreItem
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.dialog.login.LoginDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.digbury.DigBuryPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.comment.CommentPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.related.RelatedPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.DetailAppLogHelper
import com.box.app.news.util.ToastUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.MVPLoadingPresent
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class VideoDetailPresenter : MVPLoadingPresent<VideoDetailContract.View>(),
        VideoDetailContract.Presenter<VideoDetailContract.View> {

    @AutoBundleField
    var mVideo = Video()
    @AutoBundleField
    var mFromPush = false
    @AutoBundleField(required = false)
    var mChannel: Channel = Channel()
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    private val mDetailAppLogHelper = DetailAppLogHelper()

    @AutoBundleField(required = false)
    var mArticleInformation = ArticleInformation()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (isNotRestore()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.ENTER)
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.ENTER,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(System.currentTimeMillis())
                            .setItemId(mVideo.aid)
                            .setRefer(mRefer)
                            .build())
        }
        EventManager.register(this)
        mView?.setVideo(video = mVideo)
        mView?.setPlayerVideo(video = mVideo,
                positionSourceRefer = mRefer,
                positionRefer = AppLog.Refer
                        .newBuilder()
                        .setItemId(mVideo.aid)
                        .setName(AppLogKey.Refer.ARTICLE_DETAIL)
                        .build())
        mView?.loadDigBury(DigBuryPresenterAutoBundle
                .builder(mVideo, mChannel, AnalyticsKey.Event.VIDEO_DETAIL)
                .bundle())
    }

    override fun onResume() {
        mDetailAppLogHelper.onResume()
    }

    override fun onPause() {
        mDetailAppLogHelper.onPause()
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        if (!mFromPush) {
            mView?.playVideo()
            mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mVideo, mChannel, AnalyticsKey.Event.IMAGE_DETAIL)
                    .bundle())
            mView?.loadComment(CommentPresenterAutoBundle.builder(mVideo, mRefer, AnalyticsKey.Event.IMAGE_DETAIL).bundle())
        } else {
            loadVideoIfFromPush()
        }
        loadInformation()
    }

    fun loadVideoIfFromPush() {
        if (mFromPush) {
            mView?.showVideoLoadingUI()
            DataManager.Remote.getArticleDetail(mVideo.aid)
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = { t: ArticleDetail ->
                                val news = t.news
                                if (news is Video) {
                                    mVideo = news
                                    mView?.setVideo(video = mVideo)
                                    mView?.setPlayerVideo(video = mVideo,
                                            positionSourceRefer = mRefer,
                                            positionRefer = AppLog.Refer
                                                    .newBuilder()
                                                    .setItemId(mVideo.aid)
                                                    .setName(AppLogKey.Refer.ARTICLE_DETAIL)
                                                    .build())
                                    mView?.playVideo()
                                } else {
                                    mView?.showVideoErrorUI()
                                }
                            },
                            onError = { throwable ->
                                Logger.e(throwable)
                                mView?.showFail()
                            })
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        mDetailAppLogHelper.onViewVisible(mVisibleTime)
    }

    override fun onViewInvisible() {
        super.onViewInvisible()
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                label = AppLogKey.Label.STAY_PAGE,
                body = AppLog.EventBody.newBuilder()
                        .setEnterTime(mVisibleTime)
                        .setDuration(System.currentTimeMillis() - mVisibleTime)
                        .setItemId(mVideo.aid)
                        .setRefer(mRefer)
                        .build())
    }

    private fun loadInformation() {
        DataManager.Remote.getArticleInformation(mVideo.aid)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { articleInformation ->
                            val deleteContent = articleInformation.detail.deleteContent
                            if (deleteContent == DataDictionary.DeleteContentType.EXPIRED.value ||
                                    deleteContent == DataDictionary.DeleteContentType.NO_COPYRIGHT.value) {
                                ToastUtils.showToast(ResUtils.getString(R.string.News_InvalidContent), false)
                                EventManager.post(NewsListChangeEvent(DataAction.DELETE, mChannel, mVideo))
                                return@subscribeBy
                            }

                            mArticleInformation = articleInformation

                            mVideo.updateInformation(articleInformation.detail)
                            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mVideo, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
                            mView?.setVideo(video = mVideo)
                            mView?.loadComment(CommentPresenterAutoBundle.builder(mVideo, mRefer, AnalyticsKey.Event.VIDEO_DETAIL).bundle())
                            if (articleInformation.related.isNotEmpty()) {
                                for (item in articleInformation.related) {
                                    item?.from = FROM_VIDEO_DEATIL
                                }
                                mView?.loadRelated(RelatedPresenterAutoBundle.builder(
                                        articleInformation.related, AnalyticsKey.Event.VIDEO_DETAIL,
                                        AppLog.Refer.newBuilder()
                                                .setItemId(mVideo.aid)
                                                .setName(AppLogKey.Label.RELATE)
                                                .build())
                                        .mParentChannel(mChannel)
                                        .bundle())
                            }
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                        })
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        EventManager.unregister(this)
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                label = AppLogKey.Label.STAY_PAGE_RECURSIVE,
                body = AppLog.EventBody.newBuilder()
                        .setEnterTime(mDetailAppLogHelper.firstVisibleTime)
                        .setDuration(mDetailAppLogHelper.totalRecursiveDuration)
                        .setItemId(mVideo.aid)
                        .setRefer(mRefer)
                        .build())
        AppLogManager.sendAppLog()
    }

    override fun onClickShare() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_SHARE)
        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                .builder(mVideo, mChannel, AnalyticsKey.Event.VIDEO_DETAIL, "")
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
                .bundle(), hideSelf = false)
    }

    override fun onClickDescription(isVisibility: Boolean) {
        if (isVisibility) {
            mView?.hideDescription()
            AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.HIDE_DESCRIPTION)
        } else {
            mView?.showDescription()
            AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.SHOW_DESCRIPTION)
        }
    }

    override fun onClickOrigin() {
        GSYVideoManager.releaseAllVideos()
        AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.CLICK_ORIGINAL_PAGE)
        mView?.goFromRoot(WebBrowserFragment::class.java,
                WebBrowserPresenterAutoBundle.builder(mVideo.originalSiteUrl, "")
                        .bundle())
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.news !is Video) {
            return
        }
        if (event.news.aid != mVideo.aid) {
            return
        }
        when (event.action) {
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mVideo = event.news
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mVideo.updateInformation(event.news)
                    }
                }
                mView?.setVideo(video = mVideo)
            }
            DataAction.DELETE -> {
                mView?.backToPrev()
            }
            else -> {

            }
        }
    }

    override fun onClickComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_SHOW)
        mView?.toggleToComment()
    }

    override fun onClickCollect() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_FAVOR)
        DataManager.Remote.toggleCollectNews(mVideo, mChannel)
    }

    override fun onClickWriteComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.VIDEO_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_POST)
        mView?.goFromRoot(InputCommentDialogFragment::class.java,
                InputCommentDialogPresenterAutoBundle.builder(mVideo, DataDictionary.TargetType.VIDEO, mVideo.aid, AnalyticsKey.Event.VIDEO_DETAIL)
                        .mShowForwardBoard(mArticleInformation.detail.forwardable)
                        .bundle(), hideSelf = false)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentListChangeEvent(event: CommentListChangeEvent) {
        val news = event.targetBean as? BaseNewsBean ?: return
        if (news.aid != this.mVideo.aid) {
            return
        }
        when (event.action) {
            DataAction.INSERT -> {
                mVideo.commentCount++
                mView?.scrollToComment()
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mVideo, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.DELETE -> {
                mVideo.commentCount--
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mVideo, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.UPDATE -> {

            }
        }
    }

}
