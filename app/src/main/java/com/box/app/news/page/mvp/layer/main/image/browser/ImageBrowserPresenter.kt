package com.box.app.news.page.mvp.layer.main.image.browser

import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import com.box.app.news.R
import com.box.app.news.analytics.AnalyticsKey
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.Image
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.adapter.bundle.AppLogReferConverter
import com.box.app.news.proto.AppLog
import com.box.app.news.util.ToastUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.environment.EnvDisplayMetrics
import com.box.common.core.image.fresco.ImageManager
import com.box.common.core.image.fresco.listener.IDownloadResult
import com.box.common.core.image.fresco.photoview.entity.PhotoInfo
import com.box.common.core.log.Logger
import com.box.common.core.rx.permission.PermissionException
import com.box.common.core.rx.permission.RxPermission
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.base.MVPBasePresenter
import com.yanzhenjie.permission.Permission
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import me.yokeyword.fragmentation.ISupportFragment
import java.io.File
import java.lang.Exception

class ImageBrowserPresenter : MVPBasePresenter<ImageBrowserContract.View>(),
        ImageBrowserContract.Presenter<ImageBrowserContract.View> {

    @AutoBundleField
    var mImageUrls = arrayListOf<String>()
    @AutoBundleField
    var mImageIndex = 0
    @AutoBundleField(required = false)
    var mNews: BaseNewsBean? = null
    @AutoBundleField(required = false)
    var mAnalyticsEventKey: String? = null
    @AutoBundleField(required = false)
    var mAnalyticsVisibleTime: Long = -1L
    @AutoBundleField(required = false, converter = AppLogReferConverter::class)
    var mRefer: AppLog.Refer = AppLog.Refer.getDefaultInstance()
    @AutoBundleField(required = false)
    var mEnableAppLogBack = true

    private var mEnableAppLogInvisible = true

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        if (mNews is Image) {
            if ((mNews as Image).images.isEmpty()) {
                val info = (mNews as Image).info
                val url = info.urls.firstOrNull()
                val ratio = info.width.toFloat() / info.height.toFloat()
                val photo = PhotoInfo()
                photo.photoId = mNews?.aid
                photo.originalUrl = url
                photo.thumbnailUrl = url
                photo.width = EnvDisplayMetrics.WIDTH_PIXELS
                photo.height = (photo.width / ratio).toInt()
                mView?.setPhotoInfo(arrayListOf(photo), mImageIndex)
            } else {
                val list = ArrayList((mNews as Image).images.map {
                    val url = it.urls.firstOrNull()
                    val ratio = it.width.toFloat() / it.height.toFloat()
                    val photo = PhotoInfo()
                    photo.photoId = mNews?.aid
                    photo.originalUrl = url
                    photo.thumbnailUrl = url
                    photo.width = EnvDisplayMetrics.WIDTH_PIXELS
                    photo.height = (photo.width / ratio).toInt()
                    photo})
                mView?.setPhotoInfo(list, mImageIndex)
            }
        } else {
            mView?.setPhotoInfo(ArrayList(mImageUrls.map { t ->
                val info = PhotoInfo()
                info.originalUrl = t
                info.thumbnailUrl = t
                info
            }), mImageIndex)
        }
        if (mNews != null && mNews?.banFlag == 1) {
            mView?.hideDownload()
        } else {
            mView?.showDownload()
        }
        mView?.startTransitionCompat()
    }

    override fun onBackBegin() {
        super.onBackBegin()
        mEnableAppLogInvisible = mEnableAppLogBack
    }

    override fun onClickDownload(position: Int) {
        if (mAnalyticsEventKey != null) {
            AnalyticsManager.logEvent(mAnalyticsEventKey!!, AnalyticsKey.Parameter.CLICK_SAVE_IMAGE)
        }
        RxPermission.request(*Permission.STORAGE)
                .singleOrError()
                .ioToMain()
                .subscribeBy(
                        onSuccess = {
                            val directoryPath = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).absolutePath +
                                    File.separator + "NewsBox"
                            val file = File(directoryPath)
                            if (!file.exists()) {
                                file.mkdirs()
                            }
                            val filePath = directoryPath + File.separator + mImageUrls[position].hashCode() + ".jpg"
                            ImageManager.with(CoreApp.getInstance())
                                    .setUrl(mImageUrls[position])
                                    .setResult(object : IDownloadResult(filePath) {
                                        override fun onResult(filePath: String?) {
                                            if (filePath.isNullOrBlank()) {
                                                ToastUtils.showToast(ResUtils.getString(R.string.picture_save_error))
                                                return
                                            }

                                            Single.just(filePath)
                                                    .ioToMain()
                                                    .onErrorReturn { "" }
                                                    .subscribe(Consumer {
                                                        ToastUtils.showToast(
                                                                ResUtils.getString(R.string.picture_save_success) + filePath
                                                                , true)
                                                    })
                                        }

                                        override fun onError(e: Exception?) {
                                            super.onError(e)
                                            Single.just(1).ioToMain()
                                                    .onErrorReturn { 1 }
                                                    .subscribe(Consumer {
                                                        ToastUtils.showToast(ResUtils.getString(R.string.picture_save_error))
                                                    })
                                        }
                                    })
                                    .download()
                        },
                        onError = { throwable ->
                            Logger.e(throwable)
                            when (throwable) {
                                is PermissionException -> {
                                    mView?.showSettingDialogIfAlwaysDenied()
                                }
                                else -> mView?.showToast(R.string.picture_save_error)
                            }
                        }
                )
    }

    override fun onPageSelected(position: Int) {
    }

    override fun onClickPhoto() {
        mView?.back()
    }

    override fun onViewVisible() {
        super.onViewVisible()
        if (mNews is Image && mAnalyticsVisibleTime > -1) {
            mVisibleTime = mAnalyticsVisibleTime
            mAnalyticsVisibleTime = -1
        }
    }

    override fun onViewInvisible() {
        super.onViewInvisible()
        if (mNews !is Image) {
            return
        }
        if (mEnableAppLogInvisible) {
            AppLogManager.logEvent(
                    name = AppLog.EventName.EVENT_ARTICLE_DETAIL,
                    label = AppLogKey.Label.STAY_PAGE,
                    body = AppLog.EventBody.newBuilder()
                            .setEnterTime(mVisibleTime)
                            .setDuration(System.currentTimeMillis() - mVisibleTime)
                            .setItemId(mNews?.aid ?: "")
                            .setRefer(mRefer)
                            .build())
        } else {
            val bundle = Bundle()
            bundle.putLong("mVisibleTime", mVisibleTime)
            mView?.setFragmentResult(ISupportFragment.RESULT_OK, bundle)
        }
    }

}

