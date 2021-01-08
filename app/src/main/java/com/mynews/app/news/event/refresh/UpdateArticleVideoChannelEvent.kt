package com.mynews.app.news.event.refresh

import com.mynews.app.news.bean.RecommendChannel
import com.mynews.app.news.event.base.BaseEvent

/**
 *
 */
class UpdateArticleVideoChannelEvent(val recommendChannel: RecommendChannel) : BaseEvent()