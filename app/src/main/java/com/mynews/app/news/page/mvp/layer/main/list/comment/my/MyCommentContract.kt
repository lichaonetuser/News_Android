package com.mynews.app.news.page.mvp.layer.main.list.comment.my

import com.mynews.common.extension.app.mvp.loading.list.MVPListContract

interface MyCommentContract {

    interface View : MVPListContract.View

    interface Presenter<in V : View> : MVPListContract.Presenter<V>

}