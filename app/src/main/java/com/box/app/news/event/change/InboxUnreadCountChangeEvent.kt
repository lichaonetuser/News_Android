package com.box.app.news.event.change

import com.box.app.news.bean.InboxCountResponse
import com.box.app.news.event.base.BaseEvent

class InboxUnreadCountChangeEvent(val countResponse: InboxCountResponse) : BaseEvent()