package com.mynews.app.news.page.mvp.layer.main.video.detail

import android.os.Bundle
import com.mynews.app.news.bean.Video
import com.mynews.app.news.proto.AppLog
import com.mynews.common.extension.app.mvp.loading.MVPLoadingContract

interface VideoDetailContract {

    interface View : MVPLoadingContract.View {

        fun setVideo(video: Video)
        fun setPlayerVideo(video: Video, positionSourceRefer: AppLog.Refer, positionRefer: AppLog.Refer)
        fun showVideoLoadingUI()
        fun showVideoErrorUI()
        fun playVideo()
        fun toggleDescription()
        fun showDescription()
        fun hideDescription()
        fun loadRelated(bundle: Bundle)
        fun loadDigBury(bundle: Bundle)

        fun loadComment(bundle: Bundle)
        fun scrollToComment()
        fun toggleToComment()
    }

    interface Presenter<in V : View> : MVPLoadingContract.Presenter<V> {
        fun onClickShare()
        fun onClickOrigin()
        fun onClickDescription(isVisibility: Boolean)
        fun onPause()
        fun onResume()

        fun onClickComment()
        fun onClickCollect()
        fun onClickWriteComment()
    }

}