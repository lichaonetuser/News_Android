package com.box.app.news.item.factory

import com.box.app.news.R
import com.box.app.news.ad.AdManager
import com.box.app.news.ad.AdSourceEna
import com.box.app.news.bean.*
import com.box.app.news.bean.GIF.Companion.GIF_TYPE_IMAGE
import com.box.app.news.bean.GIF.Companion.GIF_TYPE_MP4
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataDictionary.FeedbackType
import com.box.app.news.item.*
import com.box.app.news.item.collect.*
import com.box.app.news.item.inbox.InboxCommentItem
import com.box.app.news.item.inbox.InboxMultiItem
import com.box.app.news.item.inbox.InboxNoImgItem
import com.box.app.news.item.inbox.InboxRightImgItem
import com.box.app.news.item.world.*
import com.box.common.core.util.ResUtils
import com.box.common.extension.widget.recycler.item.BaseItem
import com.box.common.extension.widget.recycler.item.EmptyItem


object ItemFactory {

    val NEWS by lazy {
        { news: BaseNewsBean ->
            val item: BaseItem<*, *> = when (news) {
                is Headline -> {
                    HeadlineTimeItem(news)
                }
                is ArticleHeadline -> {
                    ArticleHeadlineItem(news)
                }
                is Article -> {
                    val imageCount = news.listImageUrls.size
                    val style = news.style
                    when {
                        imageCount == 0 -> ArticleNoImgItem(news)
                        style == DataDictionary.ArticleStyle.LARGE_IMAGE.value -> ArticleLargeImgItem(news)
                        style == DataDictionary.ArticleStyle.LARGE_IMAGE_HEADLINE.value -> ArticleLargeImgItem(news)
                        imageCount >= 3 -> ArticleRightImgItem(news)
                        style == DataDictionary.ArticleStyle.LEFT_IMAGE.value -> ArticleLeftImgItem(news)
                        else -> ArticleRightImgItem(news)
                    }
                }
                is Video -> {
                    val style = news.style
                    when (style) {
                        DataDictionary.VideoStyle.PLAYABLE.value -> VideoPlayableItem(news)
                        DataDictionary.VideoStyle.LARGE.value -> VideoLargeImgItem(news)
                        DataDictionary.VideoStyle.LEFT.value -> VideoLeftImgItem(news)
                        DataDictionary.VideoStyle.RIGHT.value -> VideoFavoriteLargeImgItem(news)
                        else -> VideoPlayableItem(news)
                    }
                }
                is Image -> {
                    when (news.images.size) {
                        0, 1 -> SinglePictureItem(news)
                        2 -> MutiplePicture2Item(news)
                        3 -> MutiplePicture3Item(news)
                        4 -> MutiplePicture4Item(news)
                        else -> EmptyItem(news)
                    }
                }
                is GIF -> {
                    val type = news.gifType
                    when (type) {
                        GIF_TYPE_IMAGE -> GifItem(news)
                        GIF_TYPE_MP4 -> GifVideoItem(news)
                        else -> GifItem(news)
                    }
                }
                is WorldcupBanner -> {
                    val style = news.style
                    when (style) {
                        DataDictionary.BannerStyle.BANNER_STYLE_0.value -> CoverFlowBannerItem(news)
                        DataDictionary.BannerStyle.BANNER_STYLE_1.value -> infinitelyBannerItem(news)
                        else -> CoverFlowBannerItem(news)
                    }
                }
                is Essay -> {
                    EssayItem(news)
                }
                is ArticleNativeAd -> {
                    if (AdManager.checkAdNativeAdConfigValid(news)) {
                        when (news.adType) {
                            DataDictionary.AdType.FEED_VIDEO.value ->
                                when (AdManager.getNativeAdSourceEna(news)) {
                                    AdSourceEna.ADMOB -> {
                                        if (AdManager.checkAdSourceConfigValid(AdManager.getConfig().source.admob)) {
                                            AdMobNativeAdVideoItem(news)
                                        } else {
                                            EmptyItem(news)
                                        }
                                    }
                                    AdSourceEna.MOPUB -> {
                                        MoPubNativeAdVideoItem(news)
                                    }
                                    AdSourceEna.JUMPRAW -> {
                                        JumpRawNativeAdVideoItem(news)
                                    }
                                    else -> {
                                        EmptyItem(news)
                                    }
                                }
                            DataDictionary.AdType.FEED_RIGHT_IMAGE.value ->
                                when (AdManager.getNativeAdSourceEna(news)) {
                                    AdSourceEna.ADMOB -> {
                                        if (AdManager.checkAdSourceConfigValid(AdManager.getConfig().source.admob)) {
                                            AdMobNativeAdRightImgItem(news)
                                        } else {
                                            EmptyItem(news)
                                        }
                                    }
                                    AdSourceEna.MOPUB -> {
                                        MoPubNativeAdRightImgItem(news)
                                    }
                                    AdSourceEna.JUMPRAW -> {
                                        JumpRawNativeAdRightImgItem(news)
                                    }
                                    else -> {
                                        EmptyItem(news)
                                    }
                                }
                            DataDictionary.AdType.FEED_LARGE_IMAGE.value ->
                                when (AdManager.getNativeAdSourceEna(news)) {
                                    AdSourceEna.ADMOB -> {
                                        if (AdManager.checkAdSourceConfigValid(AdManager.getConfig().source.admob)) {
                                            AdMobNativeAdLargeImgItem(news)
                                        } else {
                                            EmptyItem(news)
                                        }
                                    }
                                    AdSourceEna.MOPUB -> {
                                        MoPubNativeAdLargeImgItem(news)
                                    }
                                    AdSourceEna.JUMPRAW -> {
                                        JumpRawNativeAdLargeImgItem(news)
                                    }
                                    else -> {
                                        EmptyItem(news)
                                    }
                                }
                            else -> EmptyItem(news)
                        }
                    } else {
                        EmptyItem(news)
                    }
                }
                else -> EmptyItem(news)
            }
            item
        }
    }

    val COLLECT by lazy {
        { news: BaseNewsBean ->
            var item: BaseItem<*, *> = EmptyItem(news)
            when (news) {
                is Article -> {
                    val imageCount = news.listImageUrls.size
                    val style = news.style
                    item = when {
                        imageCount == 0 -> CollectArticleNoImgItem(news)
                        style == DataDictionary.ArticleStyle.LARGE_IMAGE.value -> CollectArticleLargeImgItem(news)
                        style == DataDictionary.ArticleStyle.LARGE_IMAGE_HEADLINE.value -> CollectArticleLargeImgItem(news)
                        else -> CollectArticleRightImgItem(news)
                    }
                }
                is Video -> item = CollectVideoItem(news)
                is Image -> {
                    when (news.images.size) {
                        0, 1 -> item = Collect1PictureItem(news)
                        2 -> item = Collect2PictureItem(news)
                        3 -> item = Collect3PictureItem(news)
                        4 -> item = Collect4PictureItem(news)
                    }
                }
                is GIF -> item = CollectGIFItem(news)
                is Essay -> item = CollectEssayItem(news)
            }
            item
        }
    }

    val FEEDBACK by lazy {
        { feedback: Feedback ->
            val type = feedback.type
            when (type) {
                FeedbackType.TYPE_SERVICE.value -> FeedbackServiceItem(feedback)
                FeedbackType.TYPE_USER.value -> FeedbackUserItem(feedback)
                else -> EmptyItem(feedback)
            }
        }
    }

    val MORE by lazy {
        { more: MoreItem.More -> MoreItem(more) }
    }

    val COMMENT_MORE by lazy {
        { more: CommentMoreItem.More -> CommentMoreItem(more) }
    }

    val REPORT_REASON by lazy {
        { reportReason: ReportItem.ReportReason -> ReportItem(reportReason) }
    }

    val CLARITY by lazy {
        { clarity: String -> ClarityItem(clarity) }
    }

    val RELATED by lazy {
        { news: BaseNewsBean ->
            when {
                news is Article && news.listImageUrls.isNotEmpty() -> {
                    RelatedRightImgItem(news)
                }
                news is Video && news.coverImage.isNotBlank() -> {
                    if (news.from == BaseNewsBean.FROM_VIDEO_DEATIL && news.title.isNotBlank()) {
                        RelatedVideoLeftItem(news)
                    } else {
                        RelatedVideoItem(news)
                    }
                }
                news is Image && (news.info.urls.isNotEmpty() || news.images.isNotEmpty()) -> {
                    if (news.title.isBlank()) {
                        RelatedLargeImgItem(news)
                    } else {
                        RelatedRightImgItem(news)
                    }
                }
                news is GIF -> RelatedGifItem(news)
                news is Essay -> RelatedEssayItem(news)
                else -> RelatedNoImgItem(news)
            }
        }
    }

    val COMMENT by lazy {
        { comment: Comment -> CommentItem(comment) }
    }

    val INBOX by lazy {
        { message: InboxMessage ->
            if (message.item is BaseNewsBean) {
                val news = message.item
                if ((news is Article && news.listImageUrls.isEmpty())
                        || (news is Image && news.info.urls.isEmpty())
                        || (news is Video && news.coverImage.isBlank())
                        || (news is GIF && news.coverImage.isBlank())
                        || news is Essay) {
                    InboxNoImgItem(message)
                } else {
                    InboxRightImgItem(message)
                }
            } else if (message.items != null && message.items!!.isNotEmpty()) {
                InboxMultiItem(message)
            } else {
                InboxCommentItem(message)
            }
        }
    }

    val MYCOMMENT by lazy {
        { comment: Comment ->
            val item = CommentItem(comment)
            item.isMyCommentStyle = true
            item
        }
    }

    val WORLD_CUP_BOARD by lazy {
        { worldCupBoard: WorldCupBoard ->
            val hotItems = worldCupBoard.hot.map { WorldCupBoardCommentItem(it) }
            val recentItems = worldCupBoard.recent.map { WorldCupBoardCommentItem(it) }
            val aggregatedItems = worldCupBoard.aggregated.map(WORLD_CUP_BOARD_CONTENT)
            createWorldCupBoardItems(worldCupBoard, hotItems, recentItems, aggregatedItems)
        }
    }

    val WORLD_CUP_BOARD_CONTENT_PRIORITY by lazy {
        { worldCupBoard: WorldCupBoard ->
            val hotItems = worldCupBoard.hot.map(WORLD_CUP_BOARD_CONTENT)
            val recentItems = worldCupBoard.recent.map(WORLD_CUP_BOARD_CONTENT)
            val aggregatedItems = worldCupBoard.aggregated.map(WORLD_CUP_BOARD_CONTENT)
            createWorldCupBoardItems(worldCupBoard, hotItems, recentItems, aggregatedItems)
        }
    }

    private fun createWorldCupBoardItems(worldCupBoard: WorldCupBoard,
                                         hotItems: List<BaseWorldCupBoardItem<*>>,
                                         recentItems: List<BaseWorldCupBoardItem<*>>,
                                         aggregatedItems: List<BaseWorldCupBoardItem<*>>): List<BaseItem<*, *>> {
        val items = arrayListOf<BaseItem<*, *>>()
        if (hotItems.isNotEmpty()) {
            val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Best)
            val count = worldCupBoard.hotCount ?: hotItems.size
            val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
            hotItems.forEach { it.header = headerItem }
            items.addAll(hotItems)
        }
        if (recentItems.isNotEmpty()) {
            val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Newest)
            val count = worldCupBoard.recentCount ?: recentItems.size
            val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
            recentItems.forEach { it.header = headerItem }
            items.addAll(recentItems)
        }
        if (aggregatedItems.isNotEmpty()) {
            val headerTitle = ResUtils.getString(R.string.WorldCup2018_Comment_Other)
            val count = worldCupBoard.aggregatedCount ?: aggregatedItems.size
            val headerItem = WorldCupBoardHeaderItem(headerTitle, count)
            aggregatedItems.forEach { it.header = headerItem }
            items.addAll(aggregatedItems)
        }
        return items
    }

    val WORLD_CUP_BOARD_CONTENT by lazy {
        { comment: Comment ->
            when {
                comment.article != null -> WorldCupBoardArticleItem(comment)
                comment.video != null -> WorldCupBoardVideoItem(comment)
                comment.image != null -> WorldCupBoardImageItem(comment)
                comment.player != null -> WorldCupBoardPlayerItem(comment)
                comment.team != null -> WorldCupBoardTeamItem(comment)
                comment.match != null -> WorldCupBoardMatchItem(comment)
                else -> WorldCupBoardCommentItem(comment)
            }
        }
    }

    val SEARCH_ITEM by lazy {
        { searchData: SearchData ->
            val type = searchData.mType
            when (type) {
                SearchData.TYPE_SEARCH_HOTWORD -> SearchHotwordItem(searchData)
                SearchData.TYPE_SEARCH_HISTORY -> SearchHotwordHistoryItem(searchData)
                SearchData.TYPE_SEARCH_CLEAR_HISTORY -> SearchHotwordClearHistoryItem(searchData)
                SearchData.TYPE_SEARCH_HOTWORD_TITLE -> SearchHotwordTitleItem(searchData)
                else -> SearchHotwordDividerItem(searchData)
            }
        }
    }
}

