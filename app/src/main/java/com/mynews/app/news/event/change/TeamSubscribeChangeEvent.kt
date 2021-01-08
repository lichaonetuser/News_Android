package com.mynews.app.news.event.change

import com.mynews.app.news.bean.WorldcupTeam
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.event.base.BaseEvent

class TeamSubscribeChangeEvent (val action: DataAction, val team: WorldcupTeam, val extra: EXTRA = EXTRA.NORMAL)
    : BaseEvent() {

    enum class EXTRA {
        NORMAL, UPDATE_INFORMATION
    }

}