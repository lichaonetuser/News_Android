package com.mynews.app.news.page.mvp.layer.main.image.detail

import android.os.Bundle
import com.mynews.app.news.bean.Image
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

interface ImageDetailContract {

    interface View : MVPLoadingContract.View {
        fun setNews(image: Image)
        fun goFullImage(bundle: Bundle)
        fun loadRelated(bundle: Bundle)
        fun loadDigBury(bundle: Bundle)
        fun loadComment(bundle: Bundle)
        fun toggleToComment()
        fun scrollToComment()
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onClickShare()
        fun onClickNewsImg(index :Int)
        fun onClickAuthor()
        fun onPause()
        fun onResume()
        fun onFragmentResultFromFullImage(data: Bundle?)
        fun onClickComment()
        fun onClickCollect()
        fun onClickWriteComment()
    }

}