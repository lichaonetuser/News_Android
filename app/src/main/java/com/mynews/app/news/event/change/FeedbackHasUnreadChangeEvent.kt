package com.mynews.app.news.event.change

import com.mynews.app.news.event.base.BaseEvent

class FeedbackHasUnreadChangeEvent(val hasUnread: Boolean) : BaseEvent()