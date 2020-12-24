package com.box.app.news.event.login

import com.box.app.news.bean.Account
import com.box.app.news.event.base.BaseEvent

class LoginEvent(val success: Boolean, val account: Account?) : BaseEvent()