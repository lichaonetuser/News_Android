package com.box.app.news.openurl

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog;
import com.box.app.news.R
import com.box.app.news.applog.AppLogKey
import com.box.app.news.applog.AppLogManager
import com.box.app.news.bean.*
import com.box.app.news.data.DataDictionary
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.FontSizeChangeEvent
import com.box.app.news.item.MoreItem
import com.box.app.news.openurl.OpenUrlHost.ADJUST_TEXT_SIZE
import com.box.app.news.openurl.OpenUrlHost.ALERT
import com.box.app.news.openurl.OpenUrlHost.AUTHORIZATION
import com.box.app.news.openurl.OpenUrlHost.DETAIL
import com.box.app.news.openurl.OpenUrlHost.ESSAY
import com.box.app.news.openurl.OpenUrlHost.FEEDBACK
import com.box.app.news.openurl.OpenUrlHost.GIF
import com.box.app.news.openurl.OpenUrlHost.IMAGE
import com.box.app.news.openurl.OpenUrlHost.LOGIN
import com.box.app.news.openurl.OpenUrlHost.MARKET
import com.box.app.news.openurl.OpenUrlHost.MIX_LIST
import com.box.app.news.openurl.OpenUrlHost.REQUEST_LOCATION
import com.box.app.news.openurl.OpenUrlHost.SAFARI
import com.box.app.news.openurl.OpenUrlHost.SEARCH
import com.box.app.news.openurl.OpenUrlHost.SHARE
import com.box.app.news.openurl.OpenUrlHost.TAB
import com.box.app.news.openurl.OpenUrlHost.TEXT_SIZE
import com.box.app.news.openurl.OpenUrlHost.TOAST
import com.box.app.news.openurl.OpenUrlHost.TRACK_EVENT
import com.box.app.news.openurl.OpenUrlHost.TRACK_SERVER_EVENT
import com.box.app.news.openurl.OpenUrlHost.VIDEO
import com.box.app.news.openurl.OpenUrlHost.WEB
import com.box.app.news.openurl.OpenUrlParameter.ACTION_TITLE
import com.box.app.news.openurl.OpenUrlParameter.AID
import com.box.app.news.openurl.OpenUrlParameter.CHANNEL
import com.box.app.news.openurl.OpenUrlParameter.CHID
import com.box.app.news.openurl.OpenUrlParameter.EVENT
import com.box.app.news.openurl.OpenUrlParameter.FORWARD
import com.box.app.news.openurl.OpenUrlParameter.ID
import com.box.app.news.openurl.OpenUrlParameter.IMAGE_URL
import com.box.app.news.openurl.OpenUrlParameter.INDEX
import com.box.app.news.openurl.OpenUrlParameter.LABEL
import com.box.app.news.openurl.OpenUrlParameter.MESSAGE
import com.box.app.news.openurl.OpenUrlParameter.NAME
import com.box.app.news.openurl.OpenUrlParameter.PLATFORM
import com.box.app.news.openurl.OpenUrlParameter.REFER
import com.box.app.news.openurl.OpenUrlParameter.REFER_ID
import com.box.app.news.openurl.OpenUrlParameter.SHARE_URL
import com.box.app.news.openurl.OpenUrlParameter.STYLE
import com.box.app.news.openurl.OpenUrlParameter.TITLE
import com.box.app.news.openurl.OpenUrlParameter.TYPE
import com.box.app.news.openurl.OpenUrlParameter.URL
import com.box.app.news.openurl.OpenUrlSchema.LOCAL
import com.box.app.news.openurl.OpenUrlSchema.NEWSBOX
import com.box.app.news.openurl.OpenUrlSchema.NEWSJET
import com.box.app.news.page.activity.MainActivity
import com.box.app.news.page.mvp.layer.main.MainFragment
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailFragment
import com.box.app.news.page.mvp.layer.main.article.detail.ArticleDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.browser.BrowserFragment
import com.box.app.news.page.mvp.layer.main.browser.BrowserPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.container.title.ContainerTitleFragment
import com.box.app.news.page.mvp.layer.main.container.title.ContainerTitlePresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.dialog.login.LoginDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogFragment
import com.box.app.news.page.mvp.layer.main.dialog.more.MoreDialogPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.essay.EssayDetailFragment
import com.box.app.news.page.mvp.layer.main.essay.EssayDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.feedback.FeedbackFragment
import com.box.app.news.page.mvp.layer.main.font.FontFragment
import com.box.app.news.page.mvp.layer.main.gif.GifDetailFragment
import com.box.app.news.page.mvp.layer.main.gif.GifDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.image.detail.ImageDetailFragment
import com.box.app.news.page.mvp.layer.main.image.detail.ImageDetailPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.list.news.NewsListFragment
import com.box.app.news.page.mvp.layer.main.list.news.NewsListPresenterAutoBundle
import com.box.app.news.page.mvp.layer.main.video.detail.VideoDetailFragment
import com.box.app.news.page.mvp.layer.main.video.detail.VideoDetailPresenterAutoBundle
import com.box.app.news.proto.AppLog
import com.box.app.news.util.ShareUtils
import com.box.common.core.CoreApp
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.app.activity.CoreBaseActivity
import com.box.common.core.app.fragment.CoreBaseFragment
import com.box.common.core.util.GooglePlayUtils
import com.box.common.core.util.ResUtils
import com.box.common.extension.share.ContentLink
import com.box.common.extension.share.ShareManager
import com.crashlytics.android.Crashlytics
import me.yokeyword.fragmentation.SupportFragment

object OpenUrlManager {

    fun checkOpenUrl(openUrl: String) {
        try {
            val mainActivity = CoreApp.coreBaseActivities.find {
                it::class.java == MainActivity::class.java
            } ?: return
            checkOpenUrl(mainActivity, openUrl)
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    fun checkOpenUrl(activity: CoreBaseActivity, openUrl: String) {
        try {
            if (openUrl.isBlank()) {
                return
            }

            AppLogManager.logEvent(ExtraInfo(
                    cE = AppLogKey.CE.OPEN_URL,
                    cL = AppLogKey.CE.OPEN_URL,
                    url = openUrl
            ))

            val uri = Uri.parse(openUrl)
            val scheme = uri.scheme
            if (scheme != LOCAL && scheme != NEWSJET && scheme != NEWSBOX) {
                return
            }

            val host = uri.host
            when (host) {
                TAB -> {
                    val index = uri.getQueryParameter(INDEX)?.toIntOrNull() ?: -1
                    val forward = uri.getQueryParameter(FORWARD) ?: ""
                    val channelId = uri.getQueryParameter(CHANNEL) ?: ""
                    val mainFragment = activity.findFragment(MainFragment::class.java)
                    mainFragment.setCurrentTabAndChannel(index, channelId)
                    val currentFragments = activity.supportFragmentManager.fragments
                    for (fragment in currentFragments) {
                        if (fragment is CoreBaseFragment && fragment != mainFragment) {
                            fragment.pop()
                        }
                    }
                }
                SEARCH -> {

                }
                LOGIN -> {
                    val fragment = CoreBaseFragment.instantiate(LoginDialogFragment::class.java)
                    activity.callRootFragmentStart(fragment, SupportFragment.SINGLETOP, hideSelf = false)
                }
                FEEDBACK -> {
                    val fragment = CoreBaseFragment.instantiate(FeedbackFragment::class.java)
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                WEB -> {
                    val url = uri.getQueryParameter(URL) ?: return
                    val title = uri.getQueryParameter(TITLE) ?: ""
                    val refer = uri.getQueryParameter(REFER) ?: AppLogKey.CRN.UNKNOWN
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(BrowserFragment::class.java,
                            BrowserPresenterAutoBundle.builder(url, title, refer, referId).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                TRACK_EVENT -> {
                    val event = uri.getQueryParameter(EVENT) ?: return
                    val label = uri.getQueryParameter(LABEL) ?: return
                    AnalyticsManager.logEvent(event, label)
                }
                SAFARI -> {
                    val intent = Intent()
                    val url = uri.getQueryParameter(URL) ?: return
                    intent.action = "android.intent.action.VIEW"
                    intent.data = Uri.parse(url)
                    activity.startActivity(intent)
                }
                ALERT -> {
                    val message = uri.getQueryParameter(MESSAGE) ?: return
                    val actionTitle = uri.getQueryParameter(ACTION_TITLE)
                            ?: ResUtils.getString(R.string.Common_OK)
                    AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(message)
                            .setNegativeButton(actionTitle, { dialog, _ ->
                                dialog.dismiss()
                            })
                            .create().show()
                }
                TOAST -> {

                }
                ADJUST_TEXT_SIZE -> {
                    val style = uri.getQueryParameter(STYLE) ?: return
                    val fontSize = DataDictionary.FontSize.valueOf(style)
                    EventManager.post(FontSizeChangeEvent(fontSize))
                }
                DETAIL -> {
                    val aid = uri.getQueryParameter(ID) ?: uri.getQueryParameter(AID) ?: return
                    val refer = uri.getQueryParameter(REFER) ?: "unknow"
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(ArticleDetailFragment::class.java,
                            ArticleDetailPresenterAutoBundle.builder(Article(aid = aid), true, AppLog.Refer.newBuilder()
                                    .setName(refer)
                                    .setItemId(referId)
                                    .build()
                            ).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                VIDEO -> {
                    val aid = uri.getQueryParameter(ID) ?: uri.getQueryParameter(AID) ?: return
                    val refer = uri.getQueryParameter(REFER) ?: "unknow"
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(VideoDetailFragment::class.java,
                            VideoDetailPresenterAutoBundle.builder(Video(aid = aid), true, AppLog.Refer.newBuilder()
                                    .setName(refer)
                                    .setItemId(referId)
                                    .build()
                            ).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                IMAGE -> {
                    val aid = uri.getQueryParameter(ID) ?: uri.getQueryParameter(AID) ?: return
                    val refer = uri.getQueryParameter(REFER) ?: "unknow"
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(ImageDetailFragment::class.java,
                            ImageDetailPresenterAutoBundle.builder(Image(aid = aid), true, AppLog.Refer.newBuilder()
                                    .setName(refer)
                                    .setItemId(referId)
                                    .build()
                            ).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                GIF -> {
                    val aid = uri.getQueryParameter(ID) ?: uri.getQueryParameter(AID) ?: return
                    val refer = uri.getQueryParameter(REFER) ?: "unknow"
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(GifDetailFragment::class.java,
                            GifDetailPresenterAutoBundle.builder(GIF(aid = aid), true, AppLog.Refer.newBuilder()
                                    .setName(refer)
                                    .setItemId(referId)
                                    .build()
                            ).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                ESSAY -> {
                    val aid = uri.getQueryParameter(ID) ?: uri.getQueryParameter(AID) ?: return
                    val refer = uri.getQueryParameter(REFER) ?: "unknow"
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val fragment = CoreBaseFragment.instantiate(EssayDetailFragment::class.java,
                            EssayDetailPresenterAutoBundle.builder(Essay(aid = aid), true, AppLog.Refer.newBuilder()
                                    .setName(refer)
                                    .setItemId(referId)
                                    .build()
                            ).bundle())
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                TEXT_SIZE -> {
                    val fragment = CoreBaseFragment.instantiate(FontFragment::class.java)
                    activity.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                REQUEST_LOCATION -> {

                }
                AUTHORIZATION -> {

                }
                MARKET -> {
                    GooglePlayUtils.goMarketDetail(activity)
                }
                MIX_LIST -> {
                    val chid = uri.getQueryParameter(CHID) ?: return
                    val type = uri.getQueryParameter(TYPE)?.toInt() ?: return
                    val name = uri.getQueryParameter(NAME) ?: ""
                    val fragment = CoreBaseFragment.instantiate(ContainerTitleFragment::class.java,
                            ContainerTitlePresenterAutoBundle
                                    .builder(NewsListFragment::class.java.name, NewsListPresenterAutoBundle
                                            .builder(Channel(chid = chid, name = name, channelType = type), "public_feed", "")
                                            .bundle())
                                    .mParentLoadWhenEnterEnd(true)
                                    .mParentTitle(name)
                                    .mStatusBarIsLight(true)
                                    .bundle())
                    CoreApp.coreBaseActivities.firstOrNull()?.callRootFragmentStart(fragment, SupportFragment.STANDARD)
                }
                SHARE -> {
                    val title = uri.getQueryParameter(TITLE) ?: ""
                    val shareUrl = uri.getQueryParameter(SHARE_URL) ?: ""
                    val imageUrl = uri.getQueryParameter(IMAGE_URL) ?: ""
                    val platform = uri.getQueryParameter(PLATFORM)?.toIntOrNull()

                    if (title.isEmpty() && shareUrl.isEmpty() && imageUrl.isEmpty()) {
                        return
                    }

                    val shareContentLink = ContentLink(
                            title = title,
                            linkUrl = shareUrl,
                            imageUrl = imageUrl,
                            text = title
                    )

                    when (platform) {
                        SharePlatform.TWITTER.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.TWITTER,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.FACEBOOK.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.FACEBOOK,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.LINE.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.LINE,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.MAIL.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.MAIL,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.SMS.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.SMS,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.CLIPBOARD.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.CLIPBOARD,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        SharePlatform.SYSTEM.intValue -> {
                            ShareManager.shareLink(activity = activity,
                                    platform = com.box.common.extension.share.SharePlatform.SYSTEM,
                                    content = shareContentLink,
                                    listener = ShareUtils.getCommonShareListener(refer = AppLogKey.Refer.OPEN_URL))
                        }
                        else -> {
                            val fragment = CoreBaseFragment.instantiate(MoreDialogFragment::class.java,
                                    MoreDialogPresenterAutoBundle
                                            .builder(Article(), Channel(), "", "")
                                            .mMoreShare(arrayListOf(
                                                    MoreItem.More(MoreItem.More.Type.SHARE_FACEBOOK, R.drawable.share_facebook, ResUtils.getString(R.string.Common_Facebook)),
                                                    MoreItem.More(MoreItem.More.Type.SHARE_TWITTER, R.drawable.share_twitter, ResUtils.getString(R.string.Common_Twitter)),
                                                    MoreItem.More(MoreItem.More.Type.SHARE_LINE, R.drawable.share_line, ResUtils.getString(R.string.Common_LINE))))
                                            .mMoreAction(arrayListOf(
                                                    MoreItem.More(MoreItem.More.Type.SHARE_COPY, R.drawable.share_copy, ResUtils.getString(R.string.Common_CopyLink)),
                                                    MoreItem.More(MoreItem.More.Type.SHARE_SYSTEM, R.drawable.share_system, ResUtils.getString(R.string.Common_SystemShare)),
                                                    MoreItem.More(MoreItem.More.Type.SHARE_MESSAGE, R.drawable.share_messages, ResUtils.getString(R.string.Common_Message)),
                                                    MoreItem.More(MoreItem.More.Type.SHARE_MAIL, R.drawable.share_mail, ResUtils.getString(R.string.Common_Mail))
                                            ))
                                            .mShareContentLink(shareContentLink)
                                            .bundle())
                            activity.callRootFragmentStart(fragment, SupportFragment.STANDARD, hideSelf = false)
                        }
                    }
                }
                TRACK_SERVER_EVENT -> {
                    val event = uri.getQueryParameter(EVENT) ?: ""
                    val label = uri.getQueryParameter(LABEL) ?: ""
                    val refer = uri.getQueryParameter(REFER) ?: AppLogKey.CRN.UNKNOWN
                    val referId = uri.getQueryParameter(REFER_ID) ?: ""
                    val info = ExtraInfo(cE = event, cL = label, cRN = refer, cRI = referId, isOpenUrlTrack = true)
                    AppLogManager.logEvent(info = info)
                }
            }
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    enum class SharePlatform(val intValue: Int) {
        TWITTER(0),
        FACEBOOK(1),
        LINE(2),
        MAIL(3),
        SMS(4),
        CLIPBOARD(5),
        SYSTEM(6)
    }
}