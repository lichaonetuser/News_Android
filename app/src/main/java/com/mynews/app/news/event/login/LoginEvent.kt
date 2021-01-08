package com.mynews.app.news.event.login

import com.mynews.app.news.bean.Account
import com.mynews.app.news.event.base.BaseEvent

class LoginEvent(val success: Boolean, val account: Account?) : BaseEvent()