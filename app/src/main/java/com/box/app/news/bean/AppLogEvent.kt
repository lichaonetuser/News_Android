package com.box.app.news.bean

import com.box.app.news.bean.base.BaseBean
import com.box.app.news.proto.AppLog
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

@Table
data class AppLogEvent(
        @Setter("id") @PrimaryKey var id: String = "",
        @Setter("event") @Column var event: AppLog.Event = AppLog.Event.getDefaultInstance(),
        @Setter("fail_count") @Column var failCount: Int = 0
) : BaseBean()