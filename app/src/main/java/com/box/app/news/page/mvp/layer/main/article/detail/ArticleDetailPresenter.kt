package com.box.app.news.page.mvp.layer.main.article.detail

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataAction
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataDictionary.ArticleCopyrightType.*
import com.box.app.news.data.DataManager
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.CommentListChangeEvent
import com.box.app.news.event.change.NewsListChangeEvent
import com.box.app.news.event.refresh.NewsDetailWebRefreshEvent
import com.box.app.news.item.MoreItem
import com.box.app.news.page.activity.MainActivity
import com.box.app.news.page.mvp.layer.main.article.detail.newsjet.ArticleDetailTranscodePresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.article.detail.web.ArticleDetailWebPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.dialog.font.FontSizeDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.related.RelatedPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.DetailAppLogHelper
import com.box.app.news.util.ToastUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.MVPLoadingPresent
import com.google.gson.reflect.TypeToken
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*


@Suppress("unused")

class ArticleDetailPresenter : MVPLoadingPresent<ArticleDetailContract.View>(),
        ArticleDetailContract.Presenter<ArticleDetailContract.View> {

    @AutoBundleField
    var mArticle = Article()
    @AutoBundleField
    var mFromPush = false
    @AutoBundleField(required = false)
    var mChannel = Channel()
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    private val mDetailAppLogHelper = DetailAppLogHelper()

    @AutoBundleField(required = false)
    var mArticleInformation = ArticleInformation()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
        if (isNotRestore()) {
            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.ENTER)
            AnalyticsManager.logEvent(onUmeng = false, onAppsFlyer = false, onFirebase = true,
                    event = AnalyticsKey.Event.PV,
                    parameter = AnalyticsKey.Parameter.VIEW_TRANSCODE,
                    value = mArticle.copyrightSourceId.toString())
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.ENTER,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(System.currentTimeMillis())
                            .setItemId(mArticle.aid)
                            .setRefer(mRefer)
                            .setTags(convertTagsJsonToTagsString(mArticle.tags))
                            .build())
            AppLogManager.sendAppLog()
        }
        if (!mFromPush) {
            mView?.showContent()
            mView?.setArticle(mArticle)
            resolveCrTypeOnlyTitle()
        }
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        if (!mFromPush) {
            val appConfig = DataManager.Memory.getAppConfig()
            val updateInterval: Long = (System.currentTimeMillis() - mArticle.updateTime) / 1000L
            val isEffect = updateInterval < appConfig.detailMinRefreshInterval
            if (isEffect) {
                loadPages()
                loadArticleInformation()
                resolveCrType()
            } else {
                loadArticleIfInvalid()
            }
        } else {
            loadArticleIfFromPush()
        }
    }

    override fun onResume() {
        mDetailAppLogHelper.onResume()
    }

    override fun onPause() {
        mDetailAppLogHelper.onPause()

        if (mView?.getActivityContext() is MainActivity) {
            val context = mView?.getActivityContext() as? MainActivity
            if (context != null && context.isPaused) {
                AppLogManager.logEvent(
                        name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                        label = AppLogKey.Label.STAY_PAGE_RECURSIVE,
                        body = AppLog.EventBody.newBuilder()
                                .setEnterTime(mDetailAppLogHelper.firstVisibleTime)
                                .setDuration(mDetailAppLogHelper.totalRecursiveDuration)
                                .setItemId(mArticle.aid)
                                .setRefer(mRefer)
                                .build())

                AppLogManager.sendAppLog()
            }
        }
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
                        .setItemId(mArticle.aid)
                        .setRefer(mRefer)
                        .build())
        AppLogManager.sendAppLog()
    }

    private fun loadArticleInformation() {
        DataManager.Remote.getArticleInformation(mArticle.aid)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { articleInformation ->
                            val deleteContent = articleInformation.detail.deleteContent
                            if (deleteContent == DataDictionary.DeleteContentType.EXPIRED.value ||
                                    deleteContent == DataDictionary.DeleteContentType.NO_COPYRIGHT.value) {
                                ToastUtils.showToast(ResUtils.getString(R.string.News_InvalidContent), false)
                                EventManager.post(NewsListChangeEvent(DataAction.DELETE, mChannel, mArticle))
                                return@subscribeBy
                            }

                            mArticleInformation = articleInformation
                            mArticle.updateInformation(articleInformation.detail)
                            mView?.updateCommentFragmentShowForwardBoard(articleInformation.detail.forwardable)

                            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mArticle, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
                            if (articleInformation.related.isNotEmpty()) {
                                mView?.loadRelated(RelatedPresenterAutoBundle.builder(
                                        articleInformation.related, AnalyticsKey.Event.NEWS_DETAIL, AppLog.Refer.newBuilder()
                                        .setName(AppLogKey.Refer.ARTICLE_DETAIL)
                                        .setItemId(mArticle.aid)
                                        .build())
                                        .mParentChannel(mChannel)
                                        .bundle())
                            }
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                        })
    }

    private fun loadArticleIfInvalid() {
        mView?.showLoading()
        DataManager.Remote.getArticleDetail(mArticle.aid)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = { t: ArticleDetail ->
                            val news = t.news
                            if (news is Article) {
                                mView?.showContent()
                                mArticle = news
                                mArticle.updateTime = System.currentTimeMillis()
                                loadPages()
                                loadArticleInformation()
                                resolveCrType()
                                EventManager.post(NewsListChangeEvent(DataAction.UPDATE, mChannel, mArticle))
                            } else {
                                mView?.showFail()
                            }
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                            mView?.showFail()
                        })
    }

    private fun loadArticleIfFromPush() {
        if (mFromPush) {
            mView?.showLoading()
            DataManager.Remote.getArticleDetail(mArticle.aid)
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = { t: ArticleDetail ->
                                val news = t.news
                                if (news is Article) {
                                    mView?.showContent()
                                    mArticle = news
                                    loadPages()
                                    loadArticleInformation()
                                    resolveCrType()
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

    private fun resolveCrTypeOnlyTitle() {
        val crType = DataDictionary.ArticleCopyrightType.intValueOf(mArticle.crType)
        when (crType) {
            LITE -> {
                mView?.showSwitch()
                mView?.activateNewsJet()
            }
            LITE_AND_LOAD_WEB -> {
                mView?.showSwitch()
                mView?.activateNewsJet()
            }
            WEB -> {
                mView?.showSwitch()
                mView?.activateWeb()
            }
            WEB_ONLY -> {
                mView?.hideSwitch()
                mView?.activateWeb()
            }
            LITE_ONLY -> {
                mView?.hideSwitch()
                mView?.activateNewsJet()
            }
        }
    }

    private fun resolveCrType() {
        val crType = DataDictionary.ArticleCopyrightType.intValueOf(mArticle.crType)
        when (crType) {
            LITE -> {
                mView?.showSwitch()
                mView?.activateNewsJet()
                mView?.showNewsJet()
            }
            LITE_AND_LOAD_WEB -> {
                mView?.showSwitch()
                mView?.activateNewsJet()
                mView?.showNewsJet()
            }
            WEB -> {
                mView?.showSwitch()
                mView?.activateWeb()
                mView?.showWeb()
            }
            WEB_ONLY -> {
                mView?.hideSwitch()
                mView?.activateWeb()
                mView?.showWeb()
            }
            LITE_ONLY -> {
                mView?.hideSwitch()
                mView?.activateNewsJet()
                mView?.showNewsJet()
            }
        }
    }

    private fun loadPages() {
        val crType = DataDictionary.ArticleCopyrightType.intValueOf(mArticle.crType)
        val defaultShowWeb = when (crType) {
            LITE, LITE_AND_LOAD_WEB, LITE_ONLY -> false
            WEB, WEB_ONLY -> true
        }

        if (defaultShowWeb) {
            AnalyticsManager.logEvent(onUmeng = false, onAppsFlyer = false, onFirebase = true,
                    event = AnalyticsKey.Event.OPV,
                    parameter = AnalyticsKey.Parameter.VIEW_ORIGINAL_PAGE,
                    value = mArticle.copyrightSourceId.toString())
        }

        mView?.loadPages(webBundle = ArticleDetailWebPresenterAutoBundle.builder(mArticle, mRefer).bundle(),
                newsJetBundle = ArticleDetailTranscodePresenterAutoBundle.builder(mArticle, mRefer).mChannel(mChannel).bundle(),
                defaultShowWeb = defaultShowWeb)
    }

    override fun onClickNewsJet() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.SWITCH_TO_TRANSCODE_PAGE)
        mView?.activateNewsJet()
        mView?.showNewsJet(true)
    }

    override fun onClickWeb() {
        AnalyticsManager.logEvent(onUmeng = false, onAppsFlyer = false, onFirebase = true,
                event = AnalyticsKey.Event.OPV,
                parameter = AnalyticsKey.Parameter.VIEW_ORIGINAL_PAGE,
                value = mArticle.copyrightSourceId.toString())
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.SWITCH_TO_ORIGINAL_PAGE)
        mView?.activateWeb()
        mView?.showWeb(true)
    }

    override fun onClickFontSize() {
        mView?.goFromRoot(FontSizeDialogFragment::class.java, hideSelf = false)
    }

    override fun onClickRefresh() {
        EventManager.post(NewsDetailWebRefreshEvent())
    }

    override fun onClickWriteComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_POST)
        mView?.goFromRoot(InputCommentDialogFragment::class.java,
                InputCommentDialogPresenterAutoBundle
                        .builder(mArticle, DataDictionary.TargetType.ARTICLE, mArticle.aid, AnalyticsKey.Event.NEWS_DETAIL)
                        .mShowForwardBoard(mArticleInformation.detail.forwardable)
                        .bundle(), hideSelf = false)
    }

    override fun onClickShare() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_SHARE)
        mView?.goFromRoot(MoreDialogFragment::class.java, MoreDialogPresenterAutoBundle
                .builder(mArticle, mChannel, AnalyticsKey.Event.NEWS_DETAIL, "")
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
                .mReferId(mArticle.aid)
                .bundle(), hideSelf = false)
    }

    override fun onClickComment() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_COMMENT_SHOW)
        val crType = DataDictionary.ArticleCopyrightType.intValueOf(mArticle.crType)
        if (mView?.isNewsJetHidden() == true) {
            when (crType) {
                LITE, LITE_ONLY, LITE_AND_LOAD_WEB -> {
                    mView?.activateNewsJet()
                }
                else -> {
                }
            }
            mView?.showNewsJet(true)
            mView?.scrollTransCodeToComment()
        } else {
            when (crType) {
                LITE, LITE_ONLY, LITE_AND_LOAD_WEB -> {
                    mView?.toggleTransCodeToComment()
                }
                else -> {
                    mView?.showWeb(true)
                }
            }
        }
    }

    override fun onClickCollect() {
        AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_BAR_FAVOR)
        DataManager.Remote.toggleCollectNews(mArticle, mChannel)
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        super.onLoadingLayoutRetryClicked(id)
        loadArticleIfFromPush()
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
                        .setItemId(mArticle.aid)
                        .setRefer(mRefer)
                        .build())
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onNewsListChangeEvent(event: NewsListChangeEvent) {
        if (event.news !is Article) {
            return
        }
        if (event.news.aid != mArticle.aid) {
            return
        }
        when (event.action) {
            DataAction.UPDATE -> {
                when (event.extra) {
                    NewsListChangeEvent.EXTRA.NORMAL -> {
                        mArticle = event.news
                    }
                    NewsListChangeEvent.EXTRA.UPDATE_INFORMATION -> {
                        mArticle.updateInformation(event.news)
                    }
                }
                mView?.setArticle(mArticle)
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
        if (news.aid != this.mArticle.aid) {
            return
        }
        when (event.action) {
            DataAction.INSERT -> {
                mArticle.commentCount++
                mView?.setArticle(mArticle)
            }
            DataAction.DELETE -> {
                mArticle.commentCount--
                mView?.setArticle(mArticle)
            }
            DataAction.UPDATE -> {

            }
        }
    }

    /**
     * 将tag的对象json串转换成{tag.name}&{tags.name}形式
     */
    private fun convertTagsJsonToTagsString(tagsJson:String):String {
        val tagsList:List<Tag> = CoreGsonUtils.fromJson(tagsJson, object : TypeToken<ArrayList<Tag>>() {}.type) ?: listOf()
        var result = ""
        tagsList.forEach { result += it.name + "&" }
        return if (result.isEmpty()) result else result.substring(0, result.length - 1)
    }

    /**
     * 将tag的对象json串转换成{tag.name}&{tags.name}形式
     */
    private fun convertTagsJsonToTagsString(tags:List<Any>):String {
        val temp = LinkedList<Tag>()
        if (tags.isNotEmpty()) {
            var tag: Map<String, Any>
            tags.forEach {
                @Suppress("UNCHECKED_CAST")
                try {
                    temp.add(it as Tag)
                } catch (e: ClassCastException) {
                    tag = it as Map<String, Any>
                    temp.add(Tag(color = tag["color"].toString(), name = tag["name"].toString()))
                } catch (e:Exception) {

                }
            }
        }
        var result = ""
        temp.forEach { result += it.name + "&" }
        return if (result.isEmpty()) result else result.substring(0, result.length - 1)
    }
}

