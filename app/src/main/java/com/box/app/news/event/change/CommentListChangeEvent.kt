package com.box.app.news.event.change

import android.os.Parcelable
import com.box.app.news.bean.Comment
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataAction
import com.box.app.news.event.base.BaseEvent

class CommentListChangeEvent(val action: DataAction,
                             val targetBean: Parcelable,
                             val comment: Comment,
                             val extra: EXTRA = EXTRA.NORMAL)
    : BaseEvent() {

    enum class EXTRA {
        NORMAL, UPDATE_INFORMATION
    }

}