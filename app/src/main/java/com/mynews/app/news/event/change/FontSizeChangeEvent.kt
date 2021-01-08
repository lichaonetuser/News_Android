package com.mynews.app.news.event.change

import com.mynews.app.news.data.DataDictionary
import com.mynews.app.news.event.base.BaseEvent

class FontSizeChangeEvent(val fontSize: DataDictionary.FontSize) : BaseEvent()