package com.box.app.news.event.change

import com.box.app.news.data.DataDictionary
import com.box.app.news.event.base.BaseEvent

class FontSizeChangeEvent(val fontSize: DataDictionary.FontSize) : BaseEvent()