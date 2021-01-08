package com.mynews.app.news.page.mvp.layer.main.dialog.comment

import com.mynews.common.extension.app.mvp.dialog.MVPDialogContract

interface InputCommentDialogContract {

    interface View : MVPDialogContract.View {
        fun toggleAnonymousCheck()
        fun toggleWorldCupCheck()
        fun setWorldCupCheckVisible(showForwardBoard: Boolean)
        fun setInputHint(hint: String)
    }

    interface Presenter<in V : View> : MVPDialogContract.Presenter<V> {
        fun onClickSubmit(content: String, anonymous: Boolean, forwardWorldCupBoard: Boolean)
        fun onClickAnonymousCheck(checked: Boolean)
        fun onClickWorldCupCheck(checked: Boolean)
        // 用于view主动获取应该显示的hint
        fun getHint(): String?
    }

}