package com.mynews.app.news.event.change

import com.mynews.app.news.bean.Region
import com.mynews.app.news.event.base.BaseEvent

class WeatherRegionChangeEvent(val region : Region) : BaseEvent()