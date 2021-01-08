package com.mynews.app.news.event.refresh

import com.mynews.app.news.bean.Channel
import com.mynews.app.news.event.base.BaseEvent

/**
 *
 */
class ChannelListChangeEvent(val channels:List<Channel>) : BaseEvent()