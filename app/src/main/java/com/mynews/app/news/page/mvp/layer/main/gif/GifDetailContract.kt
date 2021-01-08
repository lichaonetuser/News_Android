package com.mynews.app.news.page.mvp.layer.main.gif

import android.os.Bundle
import com.mynews.app.news.bean.GIF
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

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