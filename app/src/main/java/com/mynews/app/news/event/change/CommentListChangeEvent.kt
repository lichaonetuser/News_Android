package com.mynews.app.news.event.change

import android.os.Parcelable
import com.mynews.app.news.bean.Comment
import com.mynews.app.news.data.DataAction
import com.mynews.app.news.event.base.BaseEvent

class CommentListChangeEvent(val action: DataAction,
                             val targetBean: Parcelable,
                             val comment: Comment,
                             val extra: EXTRA = EXTRA.NORMAL)
    : BaseEvent() {

    enum class EXTRA {
        NORMAL, UPDATE_INFORMATION
    }

}