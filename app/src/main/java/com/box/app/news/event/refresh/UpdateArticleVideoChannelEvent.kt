package com.box.app.news.event.refresh

import com.box.app.news.bean.RecommendChannel
import com.box.app.news.event.base.BaseEvent

/**
 *
 */
class UpdateArticleVideoChannelEvent(val recommendChannel: RecommendChannel) : BaseEvent()