package com.box.app.news.page.mvp.layer.main.gif

import android.os.Bundle
import com.box.app.news.bean.GIF
import com.box.app.news.bean.Image
import com.box.common.extension.app.mvp.base.MVPBaseContract
import com.box.common.extension.app.mvp.loading.MVPLoadingContract
import com.box.common.extension.app.mvp.loading.browser.MVPBrowserContract

interface GifDetailContract {

    interface View : MVPLoadingContract.View {
        fun setNews(gif: GIF)
        fun goFullImage(bundle: Bundle)
        fun loadRelated(bundle: Bundle)
        fun loadDigBury(bundle: Bundle)
        fun loadComment(bundle: Bundle)
        fun toggleToComment()
        fun scrollToComment()
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onClickShare()
        fun onClickNewsImg()
        fun onClickAuthor()
        fun onPause()
        fun onResume()
        fun onFragmentResultFromFullImage(data: Bundle?)
        fun onClickComment()
        fun onClickCollect()
        fun onClickWriteComment()
    }

}