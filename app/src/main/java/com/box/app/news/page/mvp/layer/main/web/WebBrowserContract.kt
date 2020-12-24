package com.box.app.news.page.mvp.layer.main.web

import android.os.Bundle
import com.box.app.news.bean.Share
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserContract

interface WebBrowserContract {

    interface View : MVPBrowserContract.View {
        fun setDispatchBack(dispatch: Boolean)
        fun setTitle(title: String)
        fun setShare(share: Share?)
        fun hideTitleBar()
        fun hideStatusBar()
        fun hideIndicator()
        fun showIndicator()
        fun saveWebState(outState: Bundle)
        fun restoreStateWebState(inState: Bundle)
    }

    interface Presenter<in V : View> : MVPBrowserContract.Presenter<V> {
        fun onClickShare(share: Share)
    }

}