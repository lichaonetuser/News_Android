package com.box.app.news.event.change

import com.box.app.news.event.base.BaseEvent

class FeedbackHasUnreadChangeEvent(val hasUnread: Boolean) : BaseEvent()