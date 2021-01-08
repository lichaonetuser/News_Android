package com.mynews.common.extension.app.mvp.loading

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface MVPLoadingContract {

    interface View : MVPBaseContract.View {

        fun showEmpty(message: String = "")

        fun showLoading(message: String = "")

        fun showFail(message: String = "")

        fun showContent(message: String = "")

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {

        fun onLoadingLayoutRetryClicked(id: Int)

    }

}