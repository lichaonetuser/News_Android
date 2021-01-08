package com.mynews.app.news.page.mvp.layer.main.dialog.more

import android.os.Bundle
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.item.MoreItem
import com.mynews.app.news.item.MoreItem.More.Type
import com.mynews.app.news.item.ReportItem
import com.mynews.app.news.item.factory.ItemFactory
import com.mynews.app.news.page.mvp.layer.main.dialog.report.ReportDialogPresenterAutoBundle
import com.mynews.app.news.util.ShareUtils
import com.mynews.common.core.CoreApp
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.rx.schedulers.io
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.app.mvp.dialog.MVPDialogPresenter
import com.mynews.common.extension.share.ContentLink
import com.mynews.common.extension.share.SharePlatform
import com.mynews.common.extension.widget.recycler.item.BaseItem
import com.mynews.common.extension.widget.recycler.util.convertBeansToItems
import com.kongzue.dialog.v2.TipDialog
import com.yatatsu.autobundle.AutoBundleField
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class MoreDialogPresenter : MVPDialogPresenter<MoreDialogContract.View>(),
        MoreDialogContract.Presenter<MoreDialogContract.View> {

    /**
     * 默认会构建非空对象
     */
    @AutoBundleField
    var mNews: BaseNewsBean = Article()
    @AutoBundleField
    var mChannel: Channel = Channel()
    /**
     * 用于友盟等统计的EventKey，@see AnalyticsManager
     */
    @AutoBundleField
    lateinit var mAnalyticsEventKey: String
    /**
     * 用于友盟等统计区分不同页面的补充缀名
     */
    @AutoBundleField
    var mAnalyticsParameterKeyPrefix: String = ""
    /**
     * 默认分享选项
     */
    @AutoBundleField(required = false)
    var mMoreShare = arrayListOf(
            MoreItem.More(Type.SHARE_FACEBOOK, R.drawable.share_facebook, ResUtils.getString(R.string.Common_Facebook)),
            MoreItem.More(Type.SHARE_TWITTER, R.drawable.share_twitter, ResUtils.getString(R.string.Common_Twitter)),
            MoreItem.More(Type.SHARE_LINE, R.drawable.share_line, ResUtils.getString(R.string.Common_LINE)),
            MoreItem.More(Type.SHARE_COPY, R.drawable.share_copy, ResUtils.getString(R.string.Common_CopyLink)),
            MoreItem.More(Type.SHARE_SYSTEM, R.drawable.share_system, ResUtils.getString(R.string.Common_SystemShare)),
            MoreItem.More(Type.SHARE_MESSAGE, R.drawable.share_copy, ResUtils.getString(R.string.Common_Message)),
            MoreItem.More(Type.SHARE_MAIL, R.drawable.share_mail, ResUtils.getString(R.string.Common_Mail))
    )
    /**
     * 默认行为选项
     */
    @AutoBundleField(required = false)
    var mMoreAction = arrayListOf(
            MoreItem.More(Type.ACTION_COLLECT, R.drawable.share_collect, ResUtils.getString(R.string.Setting_FollowingNews)),
            MoreItem.More(Type.ACTION_DELETE, R.drawable.share_delete, ResUtils.getString(R.string.Common_NotInterested)),
            MoreItem.More(Type.ACTION_REPORT, R.drawable.share_report, ResUtils.getString(R.string.Common_Report))
    )
    /**
     * 用于分享的链接，标题等内容
     * 传入的话会取代从mNews中获取的逻辑
     */
    @AutoBundleField(required = false)
    var mShare: Share? = null
    /**
     * 用于分享的链接，标题等内容
     * 传入的话会取代从mNews中获取的逻辑
     * 优先度最高
     */
    @AutoBundleField(required = false)
    var mShareContentLink: ContentLink? = null

    @AutoBundleField(required = false)
    var mReferName: String? = null

    @AutoBundleField(required = false)
    var mReferId: String? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        initEnableNotInterested()
        initActionItem()
        initFavoriteItem()
    }

    /**
     * 根据AppConfig标记是否展示删除标记
     * 不展示的时候即便传入也会移除
     */
    fun initEnableNotInterested() {
        if (!DataManager.Memory.getAppConfig().enableNotInterested) {
            mMoreAction.removeAll { it.type == Type.ACTION_DELETE }
        }
    }

    /**
     * 初始化选项
     */
    fun initActionItem() {
        mView?.getActionAdapter()?.updateDataSet(Observable.just(mMoreAction)
                .convertBeansToItems(ItemFactory.MORE)
                .blockingFirst())
        mView?.getShareAdapter()?.updateDataSet(Observable.just(mMoreShare)
                .convertBeansToItems(ItemFactory.MORE)
                .blockingFirst())
    }

    /**
     * 初始化收藏状态
     * 如果没有传入，忽略
     */
    fun initFavoriteItem() {
        if (mNews.isFavorite) {
            mView?.getActionAdapter()?.updateItem(MoreItem(MoreItem.More(Type.ACTION_COLLECT, R.drawable.share_collected, ResUtils.getString(R.string.Common_Unfavorite))))
        } else {
            mView?.getActionAdapter()?.updateItem(MoreItem(MoreItem.More(Type.ACTION_COLLECT, R.drawable.share_collect, ResUtils.getString(R.string.Common_Favorite))))
        }
    }


    override fun onItemClick(position: Int, item: BaseItem<*, *>): Boolean {
        if (item !is MoreItem) {
            return true
        }

        val bean = item.getModel()
        when (bean.type) {
            Type.SHARE_FACEBOOK -> {
                share(SharePlatform.FACEBOOK, AnalyticsKey.Parameter.SHARE_FACEBOOK)
            }
            Type.SHARE_TWITTER -> {
                share(SharePlatform.TWITTER, AnalyticsKey.Parameter.SHARE_TWITTER)
            }
            Type.SHARE_LINE -> {
                share(SharePlatform.LINE, AnalyticsKey.Parameter.SHARE_LINE)
            }
            Type.SHARE_COPY -> {
                share(SharePlatform.CLIPBOARD, AnalyticsKey.Parameter.SHARE_COPYLINK)
            }
            Type.SHARE_MESSAGE -> {
                share(SharePlatform.SMS, AnalyticsKey.Parameter.SHARE_MESSAGE)
            }
            Type.SHARE_MAIL -> {
                share(SharePlatform.MAIL, AnalyticsKey.Parameter.SHARE_MAIL)
            }
            Type.SHARE_SYSTEM -> {
                share(SharePlatform.SYSTEM, AnalyticsKey.Parameter.SHARE_SYSTEM)
            }
            Type.ACTION_DELETE -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, AnalyticsKey.Parameter.VIDEO_ELLIPSIS_NOT_INTERESTED)
                mView?.showDeleteDialog(mNews)
            }
            Type.ACTION_REPORT -> {
                AnalyticsManager.logEvent(mAnalyticsEventKey, "$mAnalyticsParameterKeyPrefix${AnalyticsKey.Parameter.SHARE_REPORT}")
                val reportType = when (mNews) {
                    is Article -> DataDictionary.ReportType.ARTICLE
                    is Video -> DataDictionary.ReportType.VIDEO
                    is Image -> DataDictionary.ReportType.IMAGE
                    is GIF -> DataDictionary.ReportType.GIF
                    is Essay -> DataDictionary.ReportType.ESSAY
                    else -> return true
                }
                mView?.goReport(ReportDialogPresenterAutoBundle.builder(mNews.aid, reportType)
                        .mReportReason(arrayListOf(
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.HATE_SPEECH, ResUtils.getString(R.string.Report_HateSpeech)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.POLITICS, ResUtils.getString(R.string.Report_Politics)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.PORN, ResUtils.getString(R.string.Report_Porn)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.PRIVACY, ResUtils.getString(R.string.Report_Privacy)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.COPYRIGHT, ResUtils.getString(R.string.Common_OptionCopyrightInfringement)),
                                ReportItem.ReportReason(DataDictionary.ReportReasonKey.OTHER, ResUtils.getString(R.string.Report_Other))
                        ))
                        .bundle())
            }
            Type.ACTION_COLLECT -> {
                if (mNews.isFavorite) {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, "$mAnalyticsParameterKeyPrefix${AnalyticsKey.Parameter.CANCEL_FAVOR}")
                    TipDialog.show(CoreApp.getLastBaseActivityInstance() ?: return false,
                            ResUtils.getString(R.string.Tip_NewsUnfollowSuccess),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                } else {
                    AnalyticsManager.logEvent(mAnalyticsEventKey, "$mAnalyticsParameterKeyPrefix${AnalyticsKey.Parameter.FAVOR}")
                    TipDialog.show(CoreApp.getLastBaseActivityInstance() ?: return false,
                            ResUtils.getString(R.string.Tip_NewsFollowedSuccess),
                            TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH)
                }
                DataManager.Remote.toggleCollectNews(mNews, mChannel)
                mView?.back()
            }
        }
        return true
    }

    private fun share(platform: SharePlatform, analyticsParameter: String) {
        AnalyticsManager.logEvent(mAnalyticsEventKey, "$mAnalyticsParameterKeyPrefix$analyticsParameter")
        mView?.shareLink(platform = platform,
                content = when {
                    mShareContentLink != null -> mShareContentLink!!
                    mShare != null -> ShareUtils.getAppConfigShareContentLink(mShare
                            ?: Share(), platform)
                    else -> ShareUtils.getCommonContentLink(mNews, platform)
                },
                listener = ShareUtils.getCommonShareListener(
                        analyticsEventKey = mAnalyticsEventKey,
                        analyticsParameterKeyPrefix = mAnalyticsParameterKeyPrefix,
                        itemId = mNews.aid,
                        refer = mReferName?: "",
                        finish = {
                            mView?.back()
                        }))
    }

    override fun onClickDeleteConfirm(isYes: Boolean) {
        if (isYes) {
            val parameter = if (mAnalyticsEventKey == AnalyticsKey.Event.NEWS || mAnalyticsEventKey == AnalyticsKey.Event.VIDEO) {
                AnalyticsKey.Parameter.VIDEO_ELLIPSIS_NOT_INTERESTED_CONFIRM
            } else {
                AnalyticsKey.Parameter.CLICK_NEWS_TRASH_CONFIRM
            }
            AnalyticsManager.logEvent(mAnalyticsEventKey, parameter)
            DataManager.deleteArticleAndPostNotInterest(mNews, mChannel)
                    .io()
                    .subscribeBy(onError = {})
            mView?.back()
        } else {
            val parameter = if (mAnalyticsEventKey == AnalyticsKey.Event.NEWS || mAnalyticsEventKey == AnalyticsKey.Event.VIDEO) {
                AnalyticsKey.Parameter.VIDEO_ELLIPSIS_NOT_INTERESTED_CANCEL
            } else {
                AnalyticsKey.Parameter.CLICK_NEWS_TRASH_CANCEL
            }
            AnalyticsManager.logEvent(mAnalyticsEventKey, parameter)
        }
    }

}

