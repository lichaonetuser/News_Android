package com.mynews.app.news.event.change

import com.mynews.app.news.bean.InboxCountResponse
import com.mynews.app.news.event.base.BaseEvent

class InboxUnreadCountChangeEvent(val countResponse: InboxCountResponse) : BaseEvent()