package com.mynews.app.news.page.mvp.layer.main.article.detail.web

import android.os.Bundle
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.Article
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataDictionary.ArticleCopyrightType.*
import com.mynews.app.news.data.adapter.bundle.AppLogReferConverter
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.refresh.NewsDetailWebRefreshEvent
import com.mynews.app.news.proto.AppLog
import com.mynews.common.extension.app.mvp.loading.browser.MVPBrowserPresent
import com.yatatsu.autobundle.AutoBundleField
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ArticleDetailWebPresenter : MVPBrowserPresent<ArticleDetailWebContract.View>(),
        ArticleDetailWebContract.Presenter<ArticleDetailWebContract.View> {

    @AutoBundleField
    var mArticle = Article()
    @AutoBundleField(required = false)
    var mLoadedUrl = false
    @AutoBundleField(converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()

    private var enterCount = 0

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        EventManager.register(this)
        val crType = DataDictionary.ArticleCopyrightType.intValueOf(mArticle.crType)
        when (crType) {
            LITE_AND_LOAD_WEB, WEB, WEB_ONLY -> {
                mView?.loadURL(mArticle.url)
                mLoadedUrl = true
            }
            else -> {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
        val pageHeight = mView?.getPageHeight() ?: -1
        val readHeight = mView?.getReadHeight() ?: -1
        if (pageHeight > -1 && readHeight > -1) {
            val progress = AppLog.Progress.newBuilder()
                    .setPageHeight(pageHeight)
                    .setReadHeight(readHeight)
                    .build()
            if (mArticle.crType != DataDictionary.ArticleCopyrightType.LITE_ONLY.value) {
                AppLogManager.logEvent(
                        name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                        label = AppLogKey.Label.WEB_PROGRESS,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(mArticle.aid)
                                .setProgress(progress)
                                .setRefer(mRefer)
                                .build())
            }
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        if ((!mLoadedUrl) && enterCount > 0) {
            mView?.loadURL(mArticle.url)
            mLoadedUrl = true
        }
        enterCount++
    }

    override fun onLoadingLayoutRetryClicked(id: Int) {
        mView?.reloadURL(false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNewsDetailWebRefreshEvent(event: NewsDetailWebRefreshEvent) {
        mView?.reloadURL(false)
    }

}

