package com.box.app.news.page.mvp.layer.main.list.comment

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface CommentContract {

    interface View : MVPListContract.View {
        fun showEmpty(message: String, imgRes: Int)
    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {
        fun onUpdateShowForwardBoard(showForwardBoard: Boolean)
    }

}