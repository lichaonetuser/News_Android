package com.mynews.app.news.data.source.local.db

import com.mynews.app.news.bean.OrmaDatabase
import com.mynews.common.core.CoreApp
import io.reactivex.Single

object DBAPI {

    val ARTICLE_DB by lazy {
        OrmaDatabase.builder(CoreApp.getInstance())
                .name("news_jet_articles_v7_2_1.db")
                .build()
    }

    val APP_LOG_DB by lazy {
        OrmaDatabase.builder(CoreApp.getInstance())
                .name("app_log.db")
                .build()
    }

    fun connectAllDB(): Single<Void> {
        return Single.create {
            ARTICLE_DB
            APP_LOG_DB
        }
    }

}