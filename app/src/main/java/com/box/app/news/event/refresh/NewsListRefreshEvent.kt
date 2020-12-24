package com.box.app.news.event.refresh

import com.box.app.news.bean.Channel
import com.box.app.news.event.base.BaseEvent

class NewsListRefreshEvent(val channel: Channel) : BaseEvent()