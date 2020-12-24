package com.box.app.news.event.change

import com.box.app.news.event.base.BaseEvent
import com.box.app.news.widget.NewsVideoView

class ClarityChangeEvent(val newClarity: NewsVideoView.Clarity) : BaseEvent()