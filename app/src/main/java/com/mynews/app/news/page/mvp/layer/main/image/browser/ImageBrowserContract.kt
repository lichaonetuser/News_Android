package com.mynews.app.news.page.mvp.layer.main.image.browser

import com.mynews.common.core.image.fresco.photoview.entity.PhotoInfo
import com.mynews.common.extension.app.mvp.base.MVPBaseContract
import java.util.ArrayList

interface ImageBrowserContract {

    interface View : MVPBaseContract.View {

        fun setPhotoInfo(info: ArrayList<PhotoInfo>, index: Int)
        fun startTransitionCompat()
        fun showDownload()
        fun hideDownload()
        fun showSettingDialogIfAlwaysDenied()

    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {
        fun onClickPhoto()
        fun onClickDownload(position: Int)
        fun onPageSelected(position: Int)
    }

}