package com.box.app.news.event.change

import com.box.app.news.bean.Channel
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.DataAction
import com.box.app.news.event.base.BaseEvent

class NewsListChangeEvent(val action: DataAction,
                          val channel: Channel,
                          val news: BaseNewsBean,
                          val extra: EXTRA = EXTRA.NORMAL,
                          val checkChannel : Boolean=true)
    : BaseEvent() {
    enum class EXTRA {
        NORMAL, UPDATE_INFORMATION
    }
}