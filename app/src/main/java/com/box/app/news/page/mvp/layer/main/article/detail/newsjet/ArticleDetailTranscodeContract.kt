package com.box.app.news.page.mvp.layer.main.article.detail.newsjet

import android.os.Bundle
import com.box.app.news.bean.Article
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserContract

interface ArticleDetailTranscodeContract {

    interface View : MVPBrowserContract.View {
        fun goFullImage(bundle: Bundle)
        fun getPageHeight(): Long
        fun getReadHeight(): Long
        fun loadRelated(bundle: Bundle)
        fun loadDigBury(bundle: Bundle)
        fun loadComment(bundle: Bundle)
        fun scrollToComment()
        fun toggleToComment()
    }

    interface Presenter<in V : View> : MVPBrowserContract.Presenter<V> {
    }

}