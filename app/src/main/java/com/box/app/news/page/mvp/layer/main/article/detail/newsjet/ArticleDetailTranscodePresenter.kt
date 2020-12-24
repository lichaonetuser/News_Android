package com.box.app.news.page.mvp.layer.main.article.detail.newsjet

import android.os.Bundle
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.Article
import com.box.app.news.bean.ArticleContentWrapper
import com.box.app.news.bean.Channel
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.bean.js.JSFont
import com.box.app.news.bean.js.JSImage
import com.box.app.news.data.DataAction
import com.box.app.news.data.DataDictionary.ArticleCopyrightType
import com.box.app.news.data.DataDictionary.ArticleCopyrightType.*
import com.box.app.news.data.DataDictionary.JSArticleDetailAction.DETAIL_ACTION
import com.box.app.news.data.DataDictionary.JSArticleDetailAction.OPEN_URL
import com.box.app.news.data.DataDictionary.JSArticleDetailActionType.*
import com.box.app.news.data.DataManager
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.CommentListChangeEvent
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.event.change.NewsListChangeEvent
import com.box.app.news.openurl.OpenUrlManager
import com.box.app.news.page.mvp.layer.main.digbury.DigBuryPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.image.browser.ImageBrowserPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.comment.CommentPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.box.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.AppConfigResourceUtils
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.environment.EnvPackage
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.js.JSBaseAndroidObject
import com.box.common.core.js.JSBridge
import com.box.common.core.log.Logger
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.extension.app.mvp.bindToLifecycle
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserPresent
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

class ArticleDetailTranscodePresenter : MVPBrowserPresent<ArticleDetailTranscodeContract.View>(),
        ArticleDetailTranscodeContract.Presenter<ArticleDetailTranscodeContract.View> {

    @AutoBundleField
    var mArticle = Article()
    @AutoBundleField(required = false)
    var mChannel: Channel = Channel()
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()

    companion object {
        val htmlRes by lazy { AppConfigResourceUtils.getHtmlResource() }
        val css: String? by lazy { htmlRes[0] }
        val js: String? by lazy { htmlRes[1] }
        val lang: String? by lazy { htmlRes[2] }
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.addJavaObject("WebViewJSBridge", JSAndroidObject())
        mView?.loadDigBury(DigBuryPresenterAutoBundle.builder(mArticle, mChannel, AnalyticsKey.Event.NEWS_DETAIL)
                .bundle())
        mView?.loadComment(CommentPresenterAutoBundle.builder(mArticle, mRefer, AnalyticsKey.Event.NEWS_DETAIL).bundle())
        EventManager.register(this)
        val crType = ArticleCopyrightType.intValueOf(mArticle.crType)
        when (crType) {
            LITE, LITE_AND_LOAD_WEB, WEB, LITE_ONLY -> {
                loadContent()
            }
            else -> {
                mView?.showContent()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)

        val pageHeight = mView?.getPageHeight() ?: -1
        val readHeight = mView?.getReadHeight() ?: -1

        if (pageHeight > 0) {
            val progress = AppLog.Progress.newBuilder()
                    .setPageHeight(pageHeight)
                    .setReadHeight(readHeight)
                    .build()
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.PROGRESS,
                    body = AppLog.EventBody.newBuilder()
                            .setItemId(mArticle.aid)
                            .setProgress(progress)
                            .setRefer(mRefer)
                            .build())
        }
    }

    override fun onWebReceivedError(errorCode: Int, description: CharSequence?, failingUrl: String?): Boolean {
        val cacheContent = DataManager.Cache.getArticleContent(mArticle.aid)
        return if (cacheContent != null && cacheContent.isNotEmpty()) {
            true
        } else {
            super.onWebReceivedError(errorCode, description, failingUrl)
        }
    }

    fun loadContent() {
        val cacheContent = DataManager.Cache.getArticleContent(mArticle.aid)
        if (cacheContent != null && cacheContent.isNotEmpty()) {
            parseContentToWeb(cacheContent)
        } else {
            DataManager.Remote.getArticleContent(mArticle.aid)
                    .map { t: ArticleContentWrapper -> t.articleContent }
                    .ioToMain()
                    .bindToLifecycle(this)
                    .subscribeBy(
                            onNext = { articleContent ->
                                val newContent = articleContent.content
                                DataManager.Cache.putArticleContent(mArticle.aid, newContent)
                                if (newContent.isNotEmpty()) {
                                    parseContentToWeb(newContent)
                                } else {
                                    mView?.showFail()
                                }
                            },
                            onError = { throwable ->
                                Logger.e(throwable)
                                mView?.showFail()
                            }
                    )
        }
    }

    fun parseContentToWeb(content: String) {
        ImageManager.prefetchToBitmapCache(mArticle.sourcePic)
        mView?.loadDataWithBaseURL(null, parseContent(content), "text/html", "UTF-8", null)
    }

    fun parseContent(content: String): String {
        val textSize = DataManager.Local.getFontSize().name
        val scrPic = mArticle.sourcePic

        val html = StringBuilder("<html><head>")
        html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />")
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=no\">")
        html.append("<meta name=\"source_pic\" content=\"$scrPic\">")
        html.append("<meta name=\"crtype\" content=\"${mArticle.crType}\">")
        html.append("<meta name=\"v\" content=\"${EnvPackage.VERSION_NAME}\">")
        html.append("<meta name=\"channel_lang\" content=\"ja\">")
        html.append("<meta name=\"fe_flag\" content=\"${mArticle.feFlag}\">")
        html.append("<style>$css</style>")
        html.append("<script>${JSBridge.script}</script>")
        html.append("</head>")
        html.append("<body class=\"font_$textSize\">")
        html.append("<script>$lang</script>")
        html.append("<script>$js</script>")
        html.append(content)
        html.append("</body></html>")
        return html.toString()
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        loadContent()
    }

    override fun onWebPageFinished(url: String?) {
        super.onWebPageFinished(url)
        val size = DataManager.Local.getFontSize()
        mView?.callJSBridgeAction("change_font", JSFont(font = size.name))
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onFontSizeChangeEvent(event: FontSizeChangeEvent) {
        mView?.callJSBridgeAction("change_font", JSFont(font = event.fontSize.name))
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
        if (event.action == DataAction.INSERT) {
            mView?.scrollToComment()
        }
    }

    inner class JSAndroidObject : JSBaseAndroidObject() {

        override fun onCallbackAction(callbackId: String, action: String, actionType: String, parameters: JSONObject?) {
            when (action) {
                DETAIL_ACTION.value ->
                    when (actionType) {
                        LOAD_IMAGE.value -> {
                            val index = parameters?.optInt("index") ?: 0
                            val imageUrl = mArticle.detailImageUrls.getOrNull(index) ?: return
                            val jsImg = JSImage(index.toString(), imageUrl)
                            mView?.callJSBridgeCallback(callbackId, jsImg)
                        }
                        OPEN_ORIGINAL_URL.value -> {
                            AnalyticsManager.logEvent(onUmeng = false, onAppsFlyer = false, onFirebase = true,
                                    event = AnalyticsKey.Event.OPV,
                                    parameter = AnalyticsKey.Parameter.VIEW_ORIGINAL_PAGE,
                                    value = mArticle.copyrightSourceId.toString())
                            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_TO_WEB_PAGE)
                            mView?.goFromRoot(WebBrowserFragment::class.java,
                                    WebBrowserPresenterAutoBundle.builder(mArticle.url, "")
                                            .bundle())
                        }
                        SHOW_IMAGE.value -> {
                            AnalyticsManager.logEvent(AnalyticsKey.Event.NEWS_DETAIL, AnalyticsKey.Parameter.CLICK_IMAGE)
                            val src = parameters?.optString("imgsrc") ?: return
                            val index = parameters.optInt("index")
                            mView?.goFullImage(ImageBrowserPresenterAutoBundle.builder(ArrayList(mArticle.detailImageUrls), index)
                                    .mNews(mArticle)
                                    .mAnalyticsEventKey(AnalyticsKey.Event.NEWS_DETAIL)
                                    .bundle())
                        }
                    }
                OPEN_URL.value -> {
                    val url = parameters?.optString("url")
                    OpenUrlManager.checkOpenUrl(url ?: return)
                }
            }
        }

        override fun onError(e: Exception) {

        }
    }

}

