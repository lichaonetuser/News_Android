package com.mynews.app.news.event.change

import com.mynews.app.news.event.base.BaseEvent

/**
 * 是否显示文章首页频道编辑右上角的新频道提醒
 */
class HideOrShowNewChannelTipEvent(val show: Boolean): BaseEvent()