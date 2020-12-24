package com.box.app.news.page.mvp.layer.main.worldcup.board

import com.box.common.extension.app.mvp.loading.list.MVPListContract

interface WorldCupBoardContract {

    interface View : MVPListContract.View {
        fun scrollToPositionTop(position: Int)
        fun showGoLinkDialog(url: String?)
        fun startBrowserIntent(url: String)
    }

    interface Presenter<in V : View> : MVPListContract.Presenter<V> {
        fun onClickPostComment()
        fun onClickGoLinkDialog(url: String, isYes: Boolean)
    }

}