package com.mynews.app.news.page.mvp.layer.main.dialog.comment

import android.os.Parcelable
import com.mynews.app.news.R
import com.mynews.app.news.account.AccountManager
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.event.EventManager
import com.mynews.app.news.event.change.CommentListChangeEvent
import com.mynews.app.news.util.UDIDUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.ioToMain
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.bindToLifecycle
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v2.DialogSettings
import com.kongzue.dialog.v2.TipDialog
import com.kongzue.dialog.v2.WaitDialog
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.rxkotlin.subscribeBy

class InputCommentDialogPresenter : MVPDialogPresenter<InputCommentDialogContract.View>(),
        InputCommentDialogContract.Presenter<InputCommentDialogContract.View> {

    @AutoBundleField
    var mTargetBean: Parcelable = Channel()
    @AutoBundleField
    var mTargetType = DataDictionary.TargetType.ARTICLE
    @AutoBundleField
    var mTargetId = ""
    @AutoBundleField(required = false)
    var mReplyComment: Comment? = null
    @AutoBundleField(required = false)
    var mShowForwardBoard: Boolean = false
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String

    var isSubmitting = false

    /**
     * 返回空值意味着不是回复别人
     */
    override fun getHint(): String? {
        return if (mReplyComment == null) {
            null
        } else {
            val comment = mReplyComment!!
            val screenName = if (comment.user == null) {
                if (comment.anonymous) {
                    ResUtils.getString(R.string.Common_AnonymousUser)
                } else {
                    comment.screenName
                }
            } else {
                if (comment.anonymous) {
                    ResUtils.getString(R.string.Comment_AnonymousMe)
                } else {
                    comment.user!!.screenName
                }
            }
            String.format("${ResUtils.getString(R.string.Comment_ReplySomeone)}:%s", screenName)
        }
    }

    override fun onClickSubmit(content: String, anonymous: Boolean, forwardWorldCupBoard: Boolean) {
        if (isSubmitting) {
            return
        }
        isSubmitting = true

        WaitDialog.show(CoreApp.getLastBaseActivityInstance()
                ?: return, ResUtils.getString(R.string.Tip_Loading))
        val commentPost = CommentPostRequestInfo(
                targetId = mTargetId,
                content = content,
                anonymous = anonymous,
                targetType = mTargetType.value,
                replyCommentId = mReplyComment?.id)
        DataManager.Remote.postComment(commentPost)
                .ioToMain()
                .bindToLifecycle(this)
                .subscribeBy(
                        onNext = {
                            if (mReplyComment != null) {
                                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_REPLY_SENT)
                            } else {
                                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.COMMENT_SENT)
                            }
                            val comment = Comment(
                                    anonymous = anonymous,
                                    content = content,
                                    reply = mReplyComment,
                                    ctime = System.currentTimeMillis(),
                                    id = it.commentId,
                                    uid = it.uid,
                                    screenName = it.screenName,
                                    avatarUrl = it.avatarUrl
                            )

                            if (AccountManager.isLogin()) {
                                comment.uid = AccountManager.account?.uid ?: ""
                                comment.user = AccountManager.account
                            } else {
                                comment.uid = UDIDUtils.getUniqueDeviceId()
                                comment.user = null
                            }

                            when (mTargetBean) {
                                is Article -> comment.article = mTargetBean as Article
                                is Video -> comment.video = mTargetBean as Video
                                is Image -> comment.image = mTargetBean as Image
                                is GIF -> comment.gif = mTargetBean as GIF
                                is Essay -> comment.essay = mTargetBean as Essay
                            }

                            EventManager.post(CommentListChangeEvent(
                                    action = DataAction.INSERT,
                                    targetBean = mTargetBean,
                                    comment = comment
                            ))
                            WaitDialog.dismiss()
                            DialogSettings.tipTextInfo = TextInfo().setFontSize(10)
                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Tip_CommentPostSucess),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                            mView?.back()
                        },
                        onError = {
                            WaitDialog.dismiss()
                            DialogSettings.tipTextInfo = TextInfo().setFontSize(10)
                            TipDialog.show(CoreApp.getLastBaseActivityInstance()
                                    ?: return@subscribeBy,
                                    ResUtils.getString(R.string.Tip_CommentPostFail),
                                    TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_ERROR)
                            mView?.back()
                        }
                )
    }

    override fun onClickAnonymousCheck(checked: Boolean) {
        if (checked) {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.ANONYMOUS_DISABLE)
        } else {
            AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.ANONYMOUS_ENABLE)
        }
        mView?.toggleAnonymousCheck()
    }

    override fun onClickWorldCupCheck(checked: Boolean) {
        mView?.toggleWorldCupCheck()
    }

}

