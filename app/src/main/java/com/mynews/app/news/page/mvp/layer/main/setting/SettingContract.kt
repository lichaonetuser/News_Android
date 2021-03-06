package com.mynews.app.news.page.mvp.layer.main.setting

import com.mynews.app.news.widget.NewsVideoView
import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface SettingContract {

    interface View : MVPBaseContract.View {

        fun setVersionName(name: String)
        fun setFontSize(size: String)
        fun setVideoClarity(clarity: NewsVideoView.Clarity)
        fun showLogout()
        fun hideLogout()
        fun showLogoutConfirmDialog()
        fun setHasUnreadFeedback(hasUnread: Boolean)
        fun gotoSendEmail()

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {

        fun onClickBar(id: Int)
        fun onClickLogout()
        fun onClickLogoutConfirm(isYes: Boolean)
    }

}