package com.box.app.news.page.mvp.layer.main.feedback

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface FeedbackContract {

    interface View : MVPListContract.View {

        fun scrollTo(position: Int, smooth: Boolean = false)

    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {

        fun onClickFeedback()

    }

}