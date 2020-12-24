package com.box.app.news.data

import com.box.app.news.R
import com.box.common.core.util.ResUtils
import com.box.common.extension.login.LoginPlatform


object DataDictionary {

    val CHANNEL_ID_RECOMMEND_ARTICLE = "__for_you__" // 	文章推荐频道的ID固定为__for_you__
    val CHANNEL_ID_RECOMMEND_VIDEO = "__for_you_video__" // 视频tab推荐频道的ID固定为__for_you_video__
    val CHANNEL_ID_RECOMMEND_ARTICLE_VIDEO = "__for_you_article_video__" // 文章频道视频频道的ID固定为__for_you_article_video__
    val CHANNEL_ID_RECOMMEND_WORLDCUP = "__for_you_worldcup__" // 文章频道视频频道的ID固定为__for_you_article_video__
    val CHANNEL_ID_MESSAGE_BOARD = "__message_board__" // 世界杯留言版
    val CHANNEL_ID_HEADLINE = "__headline__" // 要闻

    enum class ChannelType(val value: Int) {
        ARTICLE(0),
        VIDEO(1),
        IMAGE(2),
        GIF(3),
        TWITTER_VIDEO(4),
        WEB(5),
        PUBLIC_FEED(6),
        BOARD(7);
    }

    enum class AccountPlatform(val value: Int, val loginPlatform: LoginPlatform) {
        FACEBOOK(0, LoginPlatform.FACEBOOK),
        TWITTER(1, LoginPlatform.TWITTER),
        GOOGLE(2, LoginPlatform.GOOGLE);

        companion object {
            fun intValueOf(value: Int): AccountPlatform? {
                return when (value) {
                    FACEBOOK.value -> AccountPlatform.FACEBOOK
                    TWITTER.value -> AccountPlatform.TWITTER
                    GOOGLE.value -> AccountPlatform.GOOGLE
                    else -> null
                }
            }
        }
    }

    enum class ProfileStatus(val value: Int) {
        OK(0),
        FACEBOOK_EXPIRE(1),
        TWITTER_EXPIRE(2),
        FORCE_EXIT(255)
    }

    enum class LoginCode(val value: Int) {
        SUCCESS(0)
    }

    enum class LogoutCode(val value: Int) {
        SUCCESS(0)
    }

    enum class LoginStatus(val value: Int) {
        SUCCESS(1),
        FAIL(0)
    }

    enum class CommentType(val value: Int) {
        ENABLE(1),
        DISABLE(0)
    }

    enum class Gender(val value: Int) {
        UNKNOWN(0),
        MALE(1),
        FEMALE(2)
    }

    enum class NewsType(val value: Int) {
        ARTICLE(0),
        VIDEO(1),
        IMAGE(2),
        GIF(3),
        MULTIPLEIMAGE(5),  //4保留给twitter video，目前twitter video和video 共用1
        ESSAY(6),
        CARD_TWITTER(98),
        CARD(99),
        GUIDE(100),
        WORLDCUPVIDEO(101),
        WORLDCUPBANNER(102),
        WORLDCUPMATCH(103),
        HEADLINE(106),
        NATIVE_AD(1001);

        companion object {
            fun intValueOf(value: Int): NewsType {
                return when (value) {
                    ARTICLE.value -> ARTICLE
                    VIDEO.value -> VIDEO
                    IMAGE.value -> IMAGE
                    CARD.value -> CARD
                    GUIDE.value -> GUIDE
                    WORLDCUPVIDEO.value -> WORLDCUPVIDEO
                    WORLDCUPBANNER.value -> WORLDCUPBANNER
                    WORLDCUPMATCH.value -> WORLDCUPMATCH
                    HEADLINE.value -> HEADLINE
                    NATIVE_AD.value -> NATIVE_AD
                    else -> ARTICLE
                }
            }
        }
    }

    enum class AdType(val value: Int) {
        INTERSTITIAL(0),
        FEED_RIGHT_IMAGE(10),
        FEED_LARGE_IMAGE(11),
        FEED_VIDEO(12)
    }

    enum class NewsActionType(val value: Int) {
        DIG(1),                 // 赞
        BURY(2),                // 不感兴趣
        NOT_INTEREST(3),        // 踩
        DELETE(4),              // 详情页删除
        FAVORITE(5),            // 收藏
        UNFAVORITE(6),          // 取消收藏
        CANCEL_DIG(7),          // 取消赞
        CANCEL_BURY(8),         // 取消踩
        CANCEL_DIG_AND_BURY(9), // 取消赞并且踩
        CANCEL_BURY_AND_DIG(10); // 取消踩并且赞
    }

    enum class ArticleStyle(val value: Int) {
        UN_KNOW(0),
        LARGE_IMAGE(1),
        RIGHT_IMAGE(2),
        LEFT_IMAGE(3),
        LARGE_IMAGE_HEADLINE(5),
        RIGHT_IMAGE_HEADLINE(6),
        NO_OR_MULTI_IMAGE_HEADLINE(7);
    }

    enum class ArticleCopyrightType(val value: Int) {
        LITE(0), //默认加载转码
        LITE_AND_LOAD_WEB(1),  //默认加载转码, 同时加载原网页
        WEB(2),  //默认加载原网页, 同时显示转码按钮
        WEB_ONLY(3), //默认加载原网页, 不再显示转码按钮
        LITE_ONLY(4); //默认加载转码页, 不再显示转码按钮

        companion object {
            fun intValueOf(value: Int): ArticleCopyrightType {
                return when (value) {
                    LITE.value -> LITE
                    LITE_AND_LOAD_WEB.value -> LITE_AND_LOAD_WEB
                    WEB.value -> WEB
                    WEB_ONLY.value -> WEB_ONLY
                    LITE_ONLY.value -> LITE_ONLY
                    else -> LITE
                }
            }
        }
    }

    enum class VideoStyle(val value: Int) {
        PLAYABLE(101),
        LARGE(102),
        LEFT(103),
        RIGHT(104);
    }

    enum class DeleteContentType(val value: Int) {
        NOT_DELETE(0),
        NO_COPYRIGHT(1),
        EXPIRED(2);
    }

    enum class JSArticleDetailAction(val value: String) {
        DETAIL_ACTION("detail_action"),
        OPEN_URL("open_url")
    }

    enum class JSArticleDetailActionType(val value: String) {
        SHOW_IMAGE("show_image"), //JS展示图片
        LOAD_IMAGE("load_image"), //JS加载图片
        OPEN_ORIGINAL_URL("open_original_url"), //JS应用内打开原网页
        OPEN_ORIGINAL_URL_ON_SAFARI("open_original_url_on_safari"); //JS调用系统浏览器打开原网页
    }

    enum class FeedbackType(val value: Int) {
        TYPE_SERVICE(1),
        TYPE_USER(0);
    }

    enum class FontSize(val res: Int) {
        L(R.string.Setting_FontSize_L),
        M(R.string.Setting_FontSize_M),
        S(R.string.Setting_FontSize_S);

        fun toShowString(): String {
            return ResUtils.getString(res)
        }

        fun toCommonParams(): String {
            return this.name
        }
    }

    enum class SourceType(val value: Int) {
        YOUTUBE(0),
        MP4(1),
    }

    enum class TargetType(val value: String) {
        ARTICLE("article"),
        VIDEO("video"),
        GIF("gif"),
        ESSAY("essay"),
        IMAGE("image"),
        WORLD_CUP("worldcup"),
        TEAM("team"),
        PLAYER("player"),
        MATCH("match"),
    }

    enum class ReportType(val value: Int) {
        COMMENT(255),
        ARTICLE(0),
        VIDEO(1),
        IMAGE(2),
        GIF(3),
        ESSAY(4)
    }

    enum class SportActionType(val value: Int) {
        FOLLOW(1),                        // 关注球队
        CANCEL_FOLLOW(2),                // 取消关注球队
        RESERVATION(3),        // 预约比赛
        CANCEL_RESERVATION(4),              // 取消预约比赛
        DIG_HOME(5),            // 支持主队
        CANCEL_DIG_HOME(6),          // 取消支持主队
        DIG_AWAY(7),          // 支持客队
        CANCEL_DIG_AWAY(8),         // 取消支持客队
    }

    enum class BannerStyle(val value: Int) {
        BANNER_STYLE_0(0),
        BANNER_STYLE_1(1);
    }

    enum class WorldcupMatchStatus(val value: Int) {
        NOT_START(0),
        ONGOING(1),
        END(2)
    }

    enum class DeepLinkReportType(val value: Int) {
        LAUNCH_FETCH(0),
        REPORT_FB_URL(1),
        REPORT_AF_JSON(2)
    }

    enum class InboxType(val intValue: Int) {
        MESSAGE(0),
        PUSH_HISTORY(1),
    }

    enum class InboxPushType(val intValue: Int) {
        SINGLE(0),
        MULTI(1),
    }

    enum class InboxMessageType(val intValue: Int) {
        CUSTOM(9999),
        ARTICLE(0),
        YOUTUBE_VIDEO(1),
        IMAGE(2),
        GIF(3),
        TWITTER_VIDEO(4),
        COMMENT(104);

        companion object {
            fun intValueOf(intValue: Int): InboxMessageType? {
                return when (intValue) {
                    CUSTOM.intValue -> CUSTOM
                    ARTICLE.intValue -> ARTICLE
                    YOUTUBE_VIDEO.intValue -> YOUTUBE_VIDEO
                    IMAGE.intValue -> IMAGE
                    GIF.intValue -> GIF
                    TWITTER_VIDEO.intValue -> TWITTER_VIDEO
                    COMMENT.intValue -> COMMENT
                    else -> null
                }
            }
        }
    }

    object ReportReasonKey {
        const val OTHER = 0
        const val HATE_SPEECH = 1
        const val POLITICS = 2
        const val PORN = 3
        const val PRIVACY = 4
        const val COPYRIGHT = 9999
    }

    enum class SearchType(val value: Int) {
        ARTICLE(0),
        VIDEO(1)
    }

    enum class SearchResultQuery(val value: String) {
        KEYWORD("keyword"),
        TYPE("type")
    }

    enum class DeviceAppRequestInfoType(val value: Int) {
        ALL(0),
        FAST(1)
    }

    enum class PushMessageDialogStyle(val value: Int) {
        UNKNOWN(-999),
        TEXT(0),
        LARGE_IMAGE(1),
        RIGHT_IMAGE(2),
        MULTI_ITEM(3);

        companion object {
            fun intValueOf(value: Int): PushMessageDialogStyle {
                return when (value) {
                    PushMessageDialogStyle.TEXT.value -> PushMessageDialogStyle.TEXT
                    PushMessageDialogStyle.LARGE_IMAGE.value -> PushMessageDialogStyle.LARGE_IMAGE
                    PushMessageDialogStyle.RIGHT_IMAGE.value -> PushMessageDialogStyle.RIGHT_IMAGE
                    PushMessageDialogStyle.MULTI_ITEM.value -> PushMessageDialogStyle.MULTI_ITEM
                    else -> PushMessageDialogStyle.UNKNOWN
                }
            }
        }
    }

    enum class PushMessageWakeType(val value: Int) {
        NONE(0),
        WAKE_ONCE(1);
        //WAKE_KEEP(2);

        companion object {
            fun intValueOf(value: Int): PushMessageWakeType {
                return when (value) {
                    PushMessageWakeType.NONE.value -> PushMessageWakeType.NONE
                    PushMessageWakeType.WAKE_ONCE.value -> PushMessageWakeType.WAKE_ONCE
                    //PushMessageWakeType.WAKE_KEEP.value -> PushMessageWakeType.WAKE_KEEP
                    else -> PushMessageWakeType.NONE
                }
            }
        }
    }

    // 用法详见 http://192.168.50.102:10000/display/server/Article+API#ArticleAPI-GIF
    enum class GifType(val type: Int) {
        UNKNOWN(0), // 这个是根据业务逻辑自己定义的
        IMAGE(1),
        VIDEO(2)
    }

    @Suppress("ObjectPropertyName")
    object NetworkAccess {
        val WiFi = "WiFi"
        val _4G = "4G"
        val _3G = "3G"
        val _2G = "2G"
        val unknown = "unknown"
    }

    /**
     * 7.1 年龄 性别相关定义
     */
    object SelectInfoObject {
        val UNSET = -1

        val MALE = 0
        val FEMALE = 1

        val FEMALE_STRING = "女性"
        val MALE_STRING = "男性"

        val AGE_STRING_LIST = arrayListOf("18歲以下", "18～24歲", "25～29歲", "30～35歲", "36～45歲", "45歲以上")
        val AGE_STAGE_LIST = arrayListOf(11, 12, 13, 14, 15, 16)
        val AGE_STAGE_MAP = mapOf(
                "18歲以下" to 11,
                "18～24歲" to 12,
                "25～29歲" to 13,
                "30～35歲" to 14,
                "36～45歲" to 15,
                "45歲以上" to 16)
    }

    object HasSelectedGenderAgeState {
        val NOT_ACCEPT = "" //没有数据
        val ACCEPT = "accept" //udid接口gender字段有值，表示进入性别年龄段选择界面
        val SHOWED = "showed" //已经展示过性别年龄段选择界面
    }
}

