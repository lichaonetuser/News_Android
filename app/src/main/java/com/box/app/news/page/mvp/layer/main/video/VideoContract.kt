package com.box.app.news.page.mvp.layer.main.video

import com.box.app.news.page.mvp.layer.main.article.ArticleContract

interface VideoContract {

    interface View : ArticleContract.View

    interface Presenter<in V : View> : ArticleContract.Presenter<V>

}