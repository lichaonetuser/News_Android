@file:Suppress("unused")

package com.mynews.app.news.page.mvp.layer.main.image.detail

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.ArticleDetail
import com.mynews.app.news.bean.ArticleInformation
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.Image
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.adapter.bundle.AppLogReferConverter
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CommentListChangeEvent
import com.mynews.app.news.event.change.NewsListChangeEvent
import com.mynews.app.news.item.MoreItem
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.mynews.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.digbury.DigBuryPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.list.comment.CommentPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.list.related.RelatedPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.util.DetailAppLogHelper
import com.mynews.app.news.util.ToastUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.loading.MVPLoadingPresent
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ImageDetailPresenter : MVPLoadingPresent<ImageDetailContract.View>(),
        ImageDetailContract.Presenter<ImageDetailContract.View> {

    @AutoBundleField
    var mImage = Image()
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
            AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.ENTER)
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.ENTER,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(System.currentTimeMillis())
                            .setItemId(mImage.aid)
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
            mView?.setNews(mImage)
            mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mImage, mChannel, AnalyticsKey.Event.IMAGE_DETAIL)
                    .bundle())
            mView?.loadComment(CommentPresenterAutoBundle.builder(mImage, mRefer, AnalyticsKey.Event.IMAGE_DETAIL).bundle())
        } else {
            loadImageIfFromPush()
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
                        .setItemId(mImage.aid)
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
                            .setItemId(mImage.aid)
                            .setRefer(mRefer)
                            .build())
        }
    }

    private fun loadImageIfFromPush() {
        if (mFromPush) {
            mView?.showLoading()
            DataManager.Remote.getArticleDetail(mImage.aid)
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = { t: ArticleDetail ->
                                val news = t.news
                                if (news is Image) {
                                    mImage = news
                                    mView?.setNews(mImage)
                                    mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mImage, mChannel, AnalyticsKey.Event.IMAGE_DETAIL)
                                            .bundle())
                                    mView?.loadComment(CommentPresenterAutoBundle.builder(mImage, mRefer, AnalyticsKey.Event.IMAGE_DETAIL).bundle())
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
        DataManager.Remote.getArticleInformation(mImage.aid)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { articleInformation ->
                            val deleteContent = articleInformation.detail.deleteContent
                            if (deleteContent == DataDictionary.DeleteContentType.EXPIRED.value ||
                                    deleteContent == DataDictionary.DeleteContentType.NO_COPYRIGHT.value) {
                                ToastUtils.showToast(ResUtils.getString(R.string.News_InvalidContent), false)
                                EventManager.post(NewsListChangeEvent(DataAction.DELETE, mChannel, mImage))
                                return@subscribeBy
                            }

                            mArticleInformation = articleInformation
                            Logger.d(mArticleInformation.toString())
                            mImage.updateInformation(articleInformation.detail)
                            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mImage, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
                            if (articleInformation.related.isNotEmpty()) {
                                mView?.loadRelated(RelatedPresenterAutoBundle.builder(
                                        articleInformation.related, AnalyticsKey.Event.IMAGE_DETAIL,
                                        AppLog.Refer.newBuilder()
                                                .setItemId(mImage.aid)
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
        AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.CLICK_TO_AUTHOR_PAGE)
        mView?.goFromRoot(WebBrowserFragment::class.java, WebBrowserPresenterAutoBundle
                .builder(mImage.originalSiteUrl, "")
                .bundle())
    }

    override fun onClickShare() {
        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                .builder(mImage, mChannel, AnalyticsKey.Event.IMAGE_DETAIL, "")
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
                .mReferId(mImage.aid)
                .bundle(), hideSelf = false)
    }

    override fun onClickNewsImg(index:Int) {
        mIsClickNewsImg = true
        AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.CLICK_IMAGE)
        // todo 多张图片点击
        val images = if (mImage.images.isEmpty())  mImage.info.urls else {
            mImage.images.map { it.urls.firstOrNull()!! }
        }
        mView?.goFullImage(ImageBrowserPresenterAutoBundle.builder(ArrayList(images), index)
                .mNews(mImage)
                .mAnalyticsEventKey(AnalyticsKey.Event.IMAGE_DETAIL)
                .mAnalyticsVisibleTime(mVisibleTime)
                .mEnableAppLogBack(false)
                .mRefer(mRefer)
                .bundle())
    }

    override fun onClickComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_SHOW)
        mView?.toggleToComment()
    }

    override fun onClickCollect() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_FAVOR)
        DataManager.Remote.toggleCollectNews(mImage, mChannel)
    }

    override fun onClickWriteComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.IMAGE_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_POST)
        mView?.goFromRoot(InputCommentDialogFragment::class.java,
                InputCommentDialogPresenterAutoBundle.builder(mImage,DataDictionary.TargetType.IMAGE,mImage.aid,AnalyticsKey.Event.IMAGE_DETAIL)
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
        loadImageIfFromPush()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.news !is Image) {
            return
        }
        if (event.news.aid != mImage.aid) {
            return
        }
        when (event.action) {
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mImage = event.news
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mImage.updateInformation(event.news)
                    }
                }
                mView?.setNews(mImage)
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
        if (news.aid != this.mImage.aid) {
            return
        }
        when (event.action) {
            DataAction.INSERT -> {
                mImage.commentCount++
                mView?.scrollToComment()
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mImage, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.DELETE -> {
                mImage.commentCount--
                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mImage, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
            }
            DataAction.UPDATE -> {

            }
        }
    }

}

