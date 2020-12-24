package com.box.app.news.event.change

import com.box.app.news.bean.WorldcupTeam
import com.box.app.news.data.DataAction
import com.box.app.news.event.base.BaseEvent

class TeamSubscribeChangeEvent (val action: DataAction, val team: WorldcupTeam, val extra: EXTRA = EXTRA.NORMAL)
    : BaseEvent() {

    enum class EXTRA {
        NORMAL, UPDATE_INFORMATION
    }

}