package com.mynews.app.news.event.change

import com.mynews.app.news.bean.WorldcupMatch
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.event.base.BaseEvent

class MatchSubscribeChangeEvent (val action: DataAction, val match: WorldcupMatch, val extra: EXTRA = EXTRA.NORMAL)
    : BaseEvent() {

    enum class EXTRA {
        NORMAL, UPDATE_SUBSCRIBE_INFORMATION, UPDATE_DIG_HOME_TEAM, UPDATE_DIG_AWAY_TEAM
    }

}