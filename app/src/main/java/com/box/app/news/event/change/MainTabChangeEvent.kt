package com.box.app.news.event.change

import com.box.app.news.event.base.BaseEvent
import com.box.app.news.page.mvp.layer.main.IMainTab
import com.box.app.news.page.mvp.layer.main.MainTabEnum

class MainTabChangeEvent(val tab: IMainTab) : BaseEvent()