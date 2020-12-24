package com.box.app.news.event.change

import com.box.app.news.bean.Region
import com.box.app.news.event.base.BaseEvent

class WeatherRegionChangeEvent(val region : Region) : BaseEvent()