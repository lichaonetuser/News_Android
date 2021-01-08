package com.mynews.app.news.event.change

import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.event.base.BaseEvent

/**
 * 用于通知收藏列表发生变化的event
 */
class CollectionListChangeEvent(val action: DataAction, val news: BaseNewsBean) : BaseEvent()