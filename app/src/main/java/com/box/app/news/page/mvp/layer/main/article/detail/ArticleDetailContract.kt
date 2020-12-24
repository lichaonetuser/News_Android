package com.box.app.news.page.mvp.layer.main.article.detail

import android.os.Bundle
import com.box.app.news.bean.Article
import com.box.common.extension.app.mvp.loading.MVPLoadingContract

interface ArticleDetailContract {

    interface View : MVPLoadingContract.View {
        fun loadPages(webBundle: Bundle, newsJetBundle: Bundle, defaultShowWeb: Boolean)
        fun loadRelated(bundle: Bundle)
        fun showNewsJet(anim: Boolean = false)
        fun showWeb(anim: Boolean = false)
        fun showSwitch()
        fun hideSwitch()
        fun activateNewsJet()
        fun activateWeb()
        fun isNewsJetHidden(): Boolean
        fun setArticle(article: Article)
        fun toggleTransCodeToComment()
        fun scrollTransCodeToComment()
        fun updateCommentFragmentShowForwardBoard(showForwardBoard: Boolean)
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onClickNewsJet()
        fun onClickWeb()
        fun onClickFontSize()
        fun onClickRefresh()
        fun onPause()
        fun onResume()
        fun onClickShare()
        fun onClickComment()
        fun onClickCollect()
        fun onClickWriteComment()
    }

}