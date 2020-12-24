package com.box.app.news.page.mvp.layer.main.dialog.report.content

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.item.ReportItem
import com.box.common.core.CoreApp
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy

class ReportContentDialogPresenter : MVPDialogPresenter<ReportContentDialogContract.View>(),
        ReportContentDialogContract.Presenter<ReportContentDialogContract.View> {

    @AutoBundleField
    var mItemId: String = ""
    @AutoBundleField
    var mReportType: DataDictionary.ReportType = DataDictionary.ReportType.COMMENT

    override fun onClickSubmit(content: String) {
        val reason = ReportItem.ReportReason(key = DataDictionary.ReportReasonKey.OTHER,
                text = content)
        sendReport(reason)
    }

    fun sendReport(reason: ReportItem.ReportReason) {
        WaitDialog.show(CoreApp.getLastBaseActivityInstance()
                ?: return, ResUtils.getString(R.string.Tip_Loading))
        DataManager.Remote.postPublicReport(
                post = ReportPostRequestInfo(
                        content = reason.text,
                        reportKey = reason.key,
                        itemId = mItemId,
                        reportType = mReportType.value
                )
        ).ioToMain().subscribeBy(
                onNext = {
                    WaitDialog.dismiss()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ReportCommentSuccess),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                    mView?.back()
                },
                onError = {
                    WaitDialog.dismiss()
                    TipDialog.show(CoreApp.getLastBaseActivityInstance()
                            ?: return@subscribeBy,
                            ResUtils.getString(R.string.Tip_ReportCommentFail),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                }
        )
    }

}

