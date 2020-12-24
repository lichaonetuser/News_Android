package com.box.app.news.event.change

import com.box.app.news.bean.Account
import com.box.app.news.event.base.BaseEvent

//fromProfile指示是否来自user/profile接口导致的账户更新，默认为false
class AccountChangeEvent(val account: Account, val fromProfile: Boolean = false) : BaseEvent()