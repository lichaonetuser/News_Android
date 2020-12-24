package com.box.app.news.page.mvp.layer.main.dialog.report

import android.os.Bundle
import com.box.app.news.R
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.app.news.item.ReportItem
import com.box.app.news.item.factory.ItemFactory
import com.box.app.news.page.mvp.layer.main.dialog.report.content.ReportContentDialogPresenterAutoBundle
import com.box.common.core.CoreApp
import com.box.common.core.rx.schedulers.ioToMain
import com.box.common.core.util.ResUtils
import com.box.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.util.convertBeansToItems
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class ReportDialogPresenter : MVPDialogPresenter<ReportDialogContract.View>(),
        ReportDialogContract.Presenter<ReportDialogContract.View> {

    @AutoBundleField
    var mItemId: String = ""
    @AutoBundleField
    var mReportType: DataDictionary.ReportType = DataDictionary.ReportType.COMMENT
    @AutoBundleField(required = false)
    var mReportReason = arrayListOf(
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.HATE_SPEECH, ResUtils.getString(R.string.Report_HateSpeech)),
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.POLITICS, ResUtils.getString(R.string.Report_Politics)),
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.PORN, ResUtils.getString(R.string.Report_Porn)),
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.PRIVACY, ResUtils.getString(R.string.Report_Privacy)),
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.COPYRIGHT, ResUtils.getString(R.string.Common_OptionCopyrightInfringement)),
            ReportItem.ReportReason(DataDictionary.ReportReasonKey.OTHER, ResUtils.getString(R.string.Report_Other))
    )

    private var mSending = false

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.getAdapter()?.updateDataSet(Observable.just(mReportReason)
                .convertBeansToItems(ItemFactory.REPORT_REASON)
                .blockingFirst())
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        val bean = item.getModel(ReportItem.ReportReason::class.java) ?: return true
        when (bean.key) {
            DataDictionary.ReportReasonKey.HATE_SPEECH -> {
                sendReport(bean)
            }
            DataDictionary.ReportReasonKey.POLITICS -> {
                sendReport(bean)
            }
            DataDictionary.ReportReasonKey.PORN -> {
                sendReport(bean)
            }
            DataDictionary.ReportReasonKey.PRIVACY -> {
                sendReport(bean)
            }
            DataDictionary.ReportReasonKey.COPYRIGHT -> {
                mView?.reportByEmail(ResUtils.getString(R.string.Tip_SelectMailApp))
            }
            DataDictionary.ReportReasonKey.OTHER -> {
                mView?.goReportContent(ReportContentDialogPresenterAutoBundle
                        .builder(mItemId, mReportType)
                        .bundle())
            }
        }
        return true
    }

    fun sendReport(reason: ReportItem.ReportReason) {
        if (mSending) {
            return
        }
        mSending = true
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
                    mSending = false
                }
        )
    }

}

