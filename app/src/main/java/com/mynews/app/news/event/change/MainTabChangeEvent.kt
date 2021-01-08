package com.mynews.app.news.event.change

import com.mynews.app.news.event.base.BaseEvent
import com.mynews.app.news.page.mvp.layer.main.IMainTab

class MainTabChangeEvent(val tab: IMainTab) : BaseEvent()