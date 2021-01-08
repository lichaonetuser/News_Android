package com.mynews.app.news.page.mvp.layer.main.feedback.submit

import com.mynews.common.extension.app.mvp.base.MVPBaseContract
import com.luck.picture.lib.entity.LocalMedia

interface FeedbackSubmitContract {

    interface View : MVPBaseContract.View {

        fun goPictureSelector()

        fun setDescription(description: String)

        fun setUploadPicture(mPicturePath: String?)

        fun showProgress()

        fun hideProgress()
    }

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V> {

        fun onClickSubmit(description: String, contact: String?)

        fun onClickUploadPicture()

        fun onSelectPicture(localMedia: LocalMedia)

    }

}