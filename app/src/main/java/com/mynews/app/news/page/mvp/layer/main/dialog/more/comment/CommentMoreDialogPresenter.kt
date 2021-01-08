package com.mynews.app.news.page.mvp.layer.main.dialog.more.comment

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.Channel
import com.mynews.app.news.bean.Comment
import com.mynews.app.news.bean.CommentDelete
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CommentListChangeEvent
import com.mynews.app.news.item.CommentMoreItem
import com.mynews.app.news.item.CommentMoreItem.More.Type
import com.mynews.app.news.item.ReportItem
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.page.mvp.layer.main.dialog.comment.InputCommentDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.dialog.report.ReportDialogPresenterAutoBundle
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserFragment
import com.mynews.app.news.page.mvp.layer.main.web.WebBrowserPresenterAutoBundle
import com.mynews.app.news.util.UDIDUtils
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class CommentMoreDialogPresenter : MVPDialogPresenter<CommentMoreDialogContract.View>(),
        CommentMoreDialogContract.Presenter<CommentMoreDialogContract.View> {

    @AutoBundleField
    var mComment: Comment = Comment()
    @AutoBundleField
    var mTargetBean: Parcelable = Channel()
    @AutoBundleField
    var mTargetType = DataDictionary.TargetType.ARTICLE
    @AutoBundleField
    var mTargetId = ""
    @AutoBundleField(required = false)
    var mShowForwardBoard: Boolean = false
    @AutoBundleField(required = false)
    var mCommentMoreAction = arrayListOf<CommentMoreItem.More>()
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        initCommentMoreAction()
        mView?.getAdapter()?.updateDataSet(Observable.just(mCommentMoreAction)
                .convertBeansToItems(ItemFactory.COMMENT_MORE)
                .blockingFirst())
    }

    /**
     * 如果外界没有传入CommentMoreAction或者传入Empty数组
     * 判断是否登录
     * 如果已登录并且评论的uid和当前用户的uid相等或者
     * 未登录并且评论的uid和当前用户的UniqueDeviceId相等
     * 那么展示删除选项
     * 否则展示举报选项
     */
    private fun initCommentMoreAction() {
        if (mCommentMoreAction.isEmpty()) {
            mCommentMoreAction = if (
                    (AccountManager.isLogin() && mComment.uid == AccountManager.account?.uid) ||
                    (!AccountManager.isLogin() && mComment.uid == UDIDUtils.getUniqueDeviceId())
            ) {
                arrayListOf(
                        CommentMoreItem.More(CommentMoreItem.More.Type.REPLY, ResUtils.getString(R.string.Comment_Reply)),
                        CommentMoreItem.More(CommentMoreItem.More.Type.DELETE, ResUtils.getString(R.string.Common_Delete)))
            } else {
//                mComment.platform = 1
//                mComment.homepage = "https://www.baidu.com/"
                if (mComment.platform != 0 && !TextUtils.isEmpty(mComment.homepage)) {
                    val str = when (mComment.platform) {
                        1 -> "twitter"
                        2 -> "facebook"
                        3 -> "instagram"
                        4 -> "youtube"
                        5 -> "yahoo"
                        6 -> "twoCH"
                        7 -> "abemaBlog"
                        else -> "twitter"
                    }
                    arrayListOf(
                            CommentMoreItem.More(CommentMoreItem.More.Type.NETWORK_COMMENT,
                                    ResUtils.getString(R.string.Comment_GoToSocialMediaPage, str)).apply {
                                openUrl = mComment.homepage
                            },
                            CommentMoreItem.More(CommentMoreItem.More.Type.REPLY, ResUtils.getString(R.string.Comment_Reply)),
                            CommentMoreItem.More(CommentMoreItem.More.Type.DELETE, ResUtils.getString(R.string.Common_Delete))
                    )
                } else {
                    arrayListOf(
                            CommentMoreItem.More(CommentMoreItem.More.Type.REPLY, ResUtils.getString(R.string.Comment_Reply)),
                            CommentMoreItem.More(CommentMoreItem.More.Type.REPORT, ResUtils.getString(R.string.Common_Report)))
                }
            }
        }
    }

    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        if (item !is CommentMoreItem) {
            return true
        }
        when (item.getModel().type) {
            Type.REPLY -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_REPLY)
                mView?.goReply(InputCommentDialogPresenterAutoBundle
                        .builder(mTargetBean, mTargetType, mTargetId, mAnalyticsEventKey)
                        .mReplyComment(mComment)
                        .mShowForwardBoard(mShowForwardBoard)
                        .bundle())
            }
            Type.DELETE -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_DELETE)
                mView?.showDeleteDialog()
            }
            Type.REPORT -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_REPORT)
                mView?.goReport(ReportDialogPresenterAutoBundle.builder(mComment.id, DataDictionary.ReportType.COMMENT)
                        .mReportReason(arrayListOf(
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.HATE_SPEECH, ResUtils.getString(R.string.Report_HateSpeech)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.POLITICS, ResUtils.getString(R.string.Report_Politics)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.PORN, ResUtils.getString(R.string.Report_Porn)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.PRIVACY, ResUtils.getString(R.string.Report_Privacy)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.OTHER, ResUtils.getString(R.string.Report_Other))
                        ))
                        .bundle())
            }
            Type.NETWORK_COMMENT -> {
                val str = item.getModel().openUrl
                if (!TextUtils.isEmpty(str)) {
//                    OpenUrlManager.checkOpenUrl(str!!)
                    mView?.goFromRoot(WebBrowserFragment::class.java,
                            WebBrowserPresenterAutoBundle.builder(str!!, "").bundle())
                }
            }
        }
        return true
    }

    override fun onClickDeleteConfirm(isYes: Boolean) {
        if (isYes) {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_DELETE_CONFIRM)
            DataManager.Remote.deleteComment(CommentDelete(
                    targetType = mTargetType.value,
                    targetId = mTargetId,
                    commentId = mComment.id
            )).ioToMain().subscribeBy(
                    onNext = {
                        EventManager.post(CommentListChangeEvent(
                                action = DataAction.DELETE,
                                comment = mComment,
                                targetBean = mTargetBean
                        ))
                    },
                    onError = {

                    }
            )
        } else {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.CLICK_COMMENT_DELETE_CANCEL)
        }
        mView?.back()
    }

}

