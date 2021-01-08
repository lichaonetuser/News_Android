package com.mynews.app.news.page.mvp.layer.main.essay

import android.os.Bundle
import com.mynews.app.news.bean.Essay
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

interface EssayDetailContract {

    interface View : MVPLoadingContract.View {
        fun setNews(essay: Essay)
        fun goFullImage(bundle: Bundle)
        fun loadRelated(bundle: Bundle)
        fun loadDigBury(bundle: Bundle)
        fun loadComment(bundle: Bundle)
        fun toggleToComment()
        fun scrollToComment()
        fun changeText()
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onClickShare()
        fun onClickAuthor()
        fun onPause()
        fun onResume()
        fun onFragmentResultFromFullImage(data: Bundle?)
        fun onClickComment()
        fun onClickCollect()
        fun onClickWriteComment()
    }

}