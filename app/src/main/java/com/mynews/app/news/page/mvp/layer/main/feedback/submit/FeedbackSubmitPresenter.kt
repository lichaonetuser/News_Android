package com.mynews.app.news.page.mvp.layer.main.feedback.submit

import android.os.Bundle
import com.mynews.app.news.BuildConfig
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.FeedbackMessage
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.refresh.FeedbackRefreshEvent
import com.mynews.app.news.util.ToastUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.permission.RxPermission
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import com.mynews.common.extension.picture.PictureSelectorHelper
import com.google.firebase.iid.FirebaseInstanceId
import com.luck.picture.lib.entity.LocalMedia
import com.yanzhenjie.permission.Permission
import io.reactivex.rxkotlin.subscribeBy
import java.io.File

class FeedbackSubmitPresenter : MVPBasePresenter<FeedbackSubmitContract.View>(),
        FeedbackSubmitContract.Presenter<FeedbackSubmitContract.View> {

    private var mDescription: String? = null
    private var mContact: String? = null
    private var mPictureMedia: LocalMedia? = null
    private var mUri: String? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        AnalyticsManager.logEvent(AnalyticsKey.Event.FEEDBACK, AnalyticsKey.Parameter.ENTER_POST_FEEDBACK)
    }

    override fun onSelectPicture(localMedia: LocalMedia) {
        mPictureMedia = localMedia
        mView?.setUploadPicture(PictureSelectorHelper.getPathFromLocalMedia(localMedia))
    }

    override fun onClickSubmit(description: String, contact: String?) {
        if (BuildConfig.DEBUG && description == "push") {
            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                val token = instanceIdResult.token
                Logger.tag("PUSH").d(token)
                DataManager.Remote.submitPushToken(token)
                        .ioToMain()
                        .subscribeBy(
                                onNext = {
                                    mView?.setDescription(token)
                                    ToastUtils.showNewToast("上报完成")
                                },
                                onError = {
                                    ToastUtils.showNewToast("上报失败")
                                })
            }
            return
        }

        AnalyticsManager.logEvent(AnalyticsKey.Event.FEEDBACK, AnalyticsKey.Parameter.FEEDBACK_POSTED)
        mDescription = description
        mContact = contact
        if (mPictureMedia == null) {
            uploadFeedback()
        } else {
            uploadFeedbackAndPicture()
        }
    }

    private fun uploadFeedback() {
        mView?.showProgress()
        val width = mPictureMedia?.width
        val height = mPictureMedia?.height
        val contact = if (mContact.isNullOrBlank()) null else mContact
        val message = FeedbackMessage(
                content = mDescription, contact = contact,
                imageUri = mUri, imageWidth = width, imageHeight = height)
        DataManager.Remote.sendFeedbackMessage(message)
                .ioToMain()
                .subscribeBy(onNext = {
                    EventManager.post(FeedbackRefreshEvent())
                    mView?.back()
                }, onError = { throwable ->
                    Logger.e(throwable)
                    mView?.hideProgress()
                    mView?.showToast(ResUtils.getString(R.string.Tip_ServerError))
                })
    }

    private fun uploadFeedbackAndPicture() {
        mView?.showProgress()
        DataManager.Remote.uploadImageFile(File(PictureSelectorHelper.getPathFromLocalMedia(mPictureMedia)))
                .ioToMain()
                .subscribeBy(onNext = { data ->
                    mUri = data.imageUri
                    uploadFeedback()
                }, onError = {
                    mView?.hideProgress()
                    mView?.showToast(R.string.Tip_ServerError)
                })
    }

    override fun onClickUploadPicture() {
        RxPermission
                .request(*Permission.STORAGE)
                .singleOrError()
                .subscribeBy(
                        onSuccess = {
                            mView?.goPictureSelector()
                        },
                        onError = {

                        }
                )
    }

}

