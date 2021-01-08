package com.mynews.app.news.event.change

import com.mynews.app.news.event.base.BaseEvent
import com.mynews.app.news.widget.NewsVideoView

class ClarityChangeEvent(val newClarity: NewsVideoView.Clarity) : BaseEvent()