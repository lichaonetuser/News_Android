package com.box.app.news.page.mvp.layer.main.gif

import android.os.Bundle
import android.util.TypedValue
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
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
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.digbury.DigBuryPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
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
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GifDetailPresenter : MVPLoadingPresent<GifDetailContract.View>(),
        GifDetailContract.Presenter<GifDetailContract.View> {

    @AutoBundleField
    var mGif = GIF()
    @AutoBundleField
    var mFromPush = false
    @AutoBundleField(required = false)
    var mChannel: Channel = Channel()
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    private val mDetailAppLogHelper = DetailAppLogHelper()
    private var mIsClickNewsImg = false

    @AutoBundleField(required = false)
    var mArticleInformation = ArticleInformation()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (isNotRestore()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.ENTER)
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.ENTER,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(System.currentTimeMillis())
                            .setItemId(mGif.aid)
                            .setRefer(mRefer)
                            .build())
        }

        if (!mFromPush) {
            mView?.showContent()
        } else {
            mView?.showLoading()
        }
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        if (!mFromPush) {
            mView?.setNews(mGif)
            mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mGif, mChannel, AnalyticsKey.Event.GIF_DETAIL)
                    .bundle())
            mView?.loadComment(CommentPresenterAutoBundle.builder(mGif, mRefer, AnalyticsKey.Event.GIF_DETAIL).bundle())
        } else {
            loadGifFromPush()
        }
        EventManager.register(this)
        loadInformation()
    }

    override fun onResume() {
        mDetailAppLogHelper.onResume()
    }

    override fun onPause() {
        mDetailAppLogHelper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
        AppLogManager.logEvent(
                name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                label = AppLogKey.Label.STAY_PAGE_RECURSIVE,
                body = AppLog.EventBody.newBuilder()
                        .setEnterTime(mDetailAppLogHelper.firstVisibleTime)
                        .setDuration(mDetailAppLogHelper.totalRecursiveDuration)
                        .setItemId(mGif.aid)
                        .setRefer(mRefer)
                        .build())
    }

    override fun onViewVisible() {
        super.onViewVisible()
        mDetailAppLogHelper.onViewVisible(mVisibleTime)
    }

    override fun onViewInvisible() {
        super.onViewInvisible()
        if (!mIsClickNewsImg) {
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.STAY_PAGE,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(mVisibleTime)
                            .setDuration(System.currentTimeMillis() - mVisibleTime)
                            .setItemId(mGif.aid)
                            .setRefer(mRefer)
                            .build())
        }
    }

    private fun loadGifFromPush() {
        if (mFromPush) {
            mView?.showLoading()
            DataManager.Remote.getArticleDetail(mGif.aid)
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = { t: ArticleDetail ->
                                val news = t.news
                                if (news is GIF) {
                                    mGif = news
                                    mView?.setNews(mGif)
                                    mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mGif, mChannel, AnalyticsKey.Event.GIF_DETAIL).bundle())
                                    mView?.loadComment(CommentPresenterAutoBundle.builder(mGif, mRefer, AnalyticsKey.Event.GIF_DETAIL).bundle())
                                    mView?.showContent()
                                } else {
                                    mView?.showFail()
                                }
                            },
                            onError = { throwable ->
                                Logger.e(throwable)
                                mView?.showFail()
                            })
        }
    }

    private fun loadInformation() {
        DataManager.Remote.getArticleInformation(mGif.aid)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { articleInformation ->
                            val deleteContent = articleInformation.detail.deleteContent
                            if (deleteContent == DataDictionary.DeleteContentType.EXPIRED.value ||
                                    deleteContent == DataDictionary.DeleteContentType.NO_COPYRIGHT.value) {
                                ToastUtils.showToast(ResUtils.getString(R.string.News_InvalidContent), false)
                                EventManager.post(NewsListChangeEvent(DataAction.DELETE, mChannel, mGif))
                                return@subscribeBy
                            }

                            mArticleInformation = articleInformation

                            mGif.updateInformation(articleInformation.detail)
                            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mGif, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
                            if (articleInformation.related.isNotEmpty()) {
                                mView?.loadRelated(RelatedPresenterAutoBundle.builder(
                                        articleInformation.related, AnalyticsKey.Event.GIF_DETAIL,
                                        AppLog.Refer.newBuilder()
                                                .setItemId(mGif.aid)
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

    override fun onClickAuthor() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.CLICK_TO_AUTHOR_PAGE)
        mView?.goFromRoot(WebBrowserFragment::class.java, WebBrowserPresenterAutoBundle
                .builder(mGif.originalSiteUrl, "")
                .bundle())
    }

    override fun onClickShare() {
        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                .builder(mGif, mChannel, AnalyticsKey.Event.GIF_DETAIL, "")
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
                .mReferName(AppLogKey.Refer.ARTICLE_DETAIL)
                .mReferId(mGif.aid)
                .bundle(), hideSelf = false)
    }

    override fun onClickNewsImg() {
        mIsClickNewsImg = true
        AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.CLICK_GIF)
        mView?.goFullImage(ImageBrowserPresenterAutoBundle.builder(ArrayList(mGif.image?.urls), 0)
                .mNews(mGif)
                .mAnalyticsEventKey(AnalyticsKey.Event.GIF_DETAIL)
                .mAnalyticsVisibleTime(mVisibleTime)
                .mEnableAppLogBack(false)
                .mRefer(mRefer)
                .bundle())
    }

    override fun onClickComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_SHOW)
        mView?.toggleToComment()
    }

    override fun onClickCollect() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_FAVOR)
        DataManager.Remote.toggleCollectNews(mGif, mChannel)
    }

    override fun onClickWriteComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.GIF_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_POST)
        mView?.goFromRoot(InputCommentDialogFragment::class.java,
                InputCommentDialogPresenterAutoBundle.builder(mGif,DataDictionary.TargetType.GIF, mGif.aid,AnalyticsKey.Event.GIF_DETAIL)
                        .mShowForwardBoard(mArticleInformation.detail.forwardable)
                        .bundle(), hideSelf = false)
    }

    override fun onFragmentResultFromFullImage(data: Bundle?) {
        mIsClickNewsImg = false
        if (data != null) {
            val time = data.getLong("mVisibleTime")
            if (time > 0) {
                mVisibleTime = time
            }
        }
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        loadGifFromPush()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.news !is GIF) {
            return
        }
        if (event.news.aid != mGif.aid) {
            return
        }
        when (event.action) {
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mGif = event.news
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mGif.updateInformation(event.news)
                    }
                }
                mView?.setNews(mGif)
            }
            DataAction.DELETE -> {
                mView?.backToPrev()
            }
            else -> {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCommentListChangeEvent(event: CommentListChangeEvent) {
        val news = event.targetBean as? BaseNewsBean ?: return
        if (news.aid != this.mGif.aid) {
            return
        }
        when (event.action) {
            DataAction.INSERT -> {
                mGif.commentCount++
                mView?.scrollToComment()
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mGif, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.DELETE -> {
                mGif.commentCount--
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mGif, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.UPDATE -> {

            }
        }
    }

}

