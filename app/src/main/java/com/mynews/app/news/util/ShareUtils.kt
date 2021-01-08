package com.mynews.app.news.util

import android.text.TextUtils
import com.mynews.app.news.R
import com.mynews.app.news.analytics.AnalyticsKey
import com.mynews.app.news.applog.AppLogKey
import com.mynews.app.news.applog.AppLogManager
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.core.util.ResUtils
import com.mynews.common.extension.share.ContentLink
import com.mynews.common.extension.share.IShareListener
import com.mynews.common.extension.share.SharePlatform
import com.crashlytics.android.Crashlytics

object ShareUtils {
    const val PLATFORM_COPY = "copylink"
    const val PLATFORM_TW = "twitter"
    const val PLATFORM_FB = "facebook"
    const val PLATFORM_LINE = "line"
    const val PLATFORM_MAIL = "mail"
    const val PLATFORM_MESSAGE = "message"
    const val PLATFORM_SYSTEM = "system"
    const val PLATFORM_REPORT = "report"
    const val PLATFORM_FAVORITE = "favorite"
    const val PLATFORM_UNFAVORITE = "unfavorite"
    const val PLATFORM_NOT_INSTERESTED = "notInterested"
    private const val MAX_SHARE_LENGTH = 140

    fun getCommonContentLink(linkUrl: String, title: String?, imageUrl: String?, text: String? = "$title  $linkUrl"): ContentLink {
        return ContentLink(linkUrl = linkUrl, title = title, imageUrl = imageUrl, text = text)
    }

    fun getCommonContentLink(news: BaseNewsBean, platform: SharePlatform): ContentLink {
        return getCommonContentLink(
                linkUrl = news.shareUrl,
                title = news.title,
                imageUrl = when (news) {
                    is Article -> {
                        news.listImageUrls.firstOrNull()
                    }
                    is Video -> {
                        news.coverImage
                    }
                    else -> {
                        null
                    }
                },
                text = if (platform == SharePlatform.TWITTER) {
                    if (news is Essay) {
                        val content = news.text
                        if (!TextUtils.isEmpty(content)) {
                            var shareStr = content + news.shareUrl
                            if (shareStr.length > MAX_SHARE_LENGTH) {
                                val appender = "..."
                                val end = content.length - (shareStr.length - MAX_SHARE_LENGTH - appender.length)
                                shareStr = when {
                                    end <= 0 -> ""
                                    end >= content.length -> content
                                    else -> content.substring(0, end) + appender
                                }
                            }
                            shareStr
                        } else {
                            ""
                        }
                    } else {
                        news.title
                    }
                } else {
                    "${news.title}  ${news.shareUrl}"
                })
    }

    fun getAppConfigShareContentLink(share: Share, platform: SharePlatform): ContentLink {
        return getCommonContentLink(
                linkUrl = share.url ?: "",
                title = share.desc,
                imageUrl = share.image,
                text = if (platform == SharePlatform.TWITTER) {
                    "${share.desc} #NewsBox"
                } else {
                    "${share.desc} - NewsBox ${share.url}"
                })
    }

    fun getCommonShareListener(analyticsEventKey: String = "", analyticsParameterKeyPrefix: String = "",
                               itemId: String = "", refer: String = "", referId: String = "",
                               finish: (() -> Unit)? = null): IShareListener {
        return object : IShareListener {
            override fun onSuccess(sharePlatform: SharePlatform, confirm: Boolean, extra: Any?) {
                var platformStr = ""
                when (sharePlatform) {
                    SharePlatform.FACEBOOK -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_FACEBOOK_SUCCESS)
                        platformStr = PLATFORM_FB
                    }
                    SharePlatform.TWITTER -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_TWITTER_SUCCESS)
                        platformStr = PLATFORM_TW
                    }
                    SharePlatform.LINE -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_LINE_SUCCESS)
                        platformStr = PLATFORM_LINE
                    }
                    SharePlatform.MAIL -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_MAIL_SUCCESS)
                        platformStr = PLATFORM_MAIL
                    }
                    SharePlatform.SMS -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_MESSAGE_SUCCESS)
                        platformStr = PLATFORM_MESSAGE
                    }
                    SharePlatform.CLIPBOARD -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_COPYLINK_SUCCESS)
                        platformStr = PLATFORM_COPY
                    }
                    SharePlatform.SYSTEM -> platformStr = PLATFORM_SYSTEM
                }
                when (sharePlatform) {
                    SharePlatform.CLIPBOARD -> {
                        ToastUtils.showToast(ResUtils.getString(R.string.Tip_CopyDone))
                    }
                    else -> {
                        if (confirm) {
                            ToastUtils.showToast(ResUtils.getString(R.string.Tip_NewsShareSuccess))
                        }
                    }
                }
                AppLogManager.logEvent(
                        name = AppLog.EventName.EVENT_SHARE,
                        label = AppLogKey.Label.SHARE_DONE,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(itemId)
                                .setPlatform(platformStr)
                                .setRefer(AppLog.Refer.newBuilder()
                                        .setName(refer)
                                        .setItemId(referId)
                                        .build())
                                .build())
                finish?.invoke()
            }

            override fun onCancel(sharePlatform: SharePlatform) {
                var platformStr = ""
                when (sharePlatform) {
                    SharePlatform.FACEBOOK -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_FACEBOOK_CANCEL)
                        platformStr = PLATFORM_FB
                    }
                    SharePlatform.TWITTER -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_TWITTER_CANCEL)
                        platformStr = PLATFORM_TW
                    }
                    SharePlatform.LINE -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_LINE_CANCEL)
                        platformStr = PLATFORM_LINE
                    }
                    SharePlatform.MAIL -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_MAIL_CANCEL)
                        platformStr = PLATFORM_MAIL
                    }
                    SharePlatform.SMS -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_MESSAGE_CANCEL)
                        platformStr = PLATFORM_MESSAGE
                    }
                    SharePlatform.CLIPBOARD -> platformStr = PLATFORM_COPY
                    SharePlatform.SYSTEM -> platformStr = PLATFORM_SYSTEM
                }
                AppLogManager.logEvent(
                        name = AppLog.EventName.EVENT_SHARE,
                        label = AppLogKey.Label.SHARE_CANCEL,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(itemId)
                                .setPlatform(platformStr)
                                .setRefer(AppLog.Refer.newBuilder()
                                        .setName(refer)
                                        .setItemId(itemId)
                                        .build())
                                .build())
                ToastUtils.showToast(ResUtils.getString(R.string.Tip_NewsShareCancel))
                finish?.invoke()
            }

            override fun onError(sharePlatform: SharePlatform, e: Exception) {
                var platformStr = ""
                when (sharePlatform) {
                    SharePlatform.FACEBOOK -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_FACEBOOK_FAIL)
                        platformStr = PLATFORM_FB
                    }
                    SharePlatform.TWITTER -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_TWITTER_FAIL)
                        platformStr = PLATFORM_TW
                    }
                    SharePlatform.LINE -> platformStr = PLATFORM_LINE
                    SharePlatform.MAIL -> {
                        AnalyticsManager.logEvent(analyticsEventKey, analyticsParameterKeyPrefix + AnalyticsKey.Parameter.SHARE_MAIL_NOT_SUPPORT)
                        platformStr = PLATFORM_MAIL
                    }
                    SharePlatform.SMS -> platformStr = PLATFORM_MESSAGE
                    SharePlatform.CLIPBOARD -> platformStr = PLATFORM_COPY
                    SharePlatform.SYSTEM -> platformStr = PLATFORM_SYSTEM
                }

                when (sharePlatform) {
                    SharePlatform.LINE -> {
                        Crashlytics.logException(e)
                        ToastUtils.showToast(ResUtils.getString(R.string.Common_ShareToLineFailedNotice))
                    }
                    SharePlatform.SMS, SharePlatform.MAIL -> {
                        ToastUtils.showToast(ResUtils.getString(R.string.Tip_Unsupported))
                    }
                    else -> {
                        ToastUtils.showToast(ResUtils.getString(R.string.Tip_NewsShareFail))
                    }
                }
                var label = AppLogKey.Label.SHARE_FAIL
                if (sharePlatform == SharePlatform.SMS) {
                    label = AppLogKey.Label.SHARE_NOT_SUPPORT
                }
                AppLogManager.logEvent(
                        name = AppLog.EventName.EVENT_SHARE,
                        label = label,
                        body = AppLog.EventBody.newBuilder()
                                .setItemId(itemId)
                                .setPlatform(platformStr)
                                .setRefer(AppLog.Refer.newBuilder()
                                        .setName(refer)
                                        .setItemId(itemId)
                                        .build())
                                .build())
                finish?.invoke()
            }
        }
    }

}