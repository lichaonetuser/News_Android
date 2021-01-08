package com.mynews.app.news.bean

import com.mynews.app.news.bean.base.BaseBean
import com.mynews.app.news.proto.AppLog

data class AppLogImpressionCell(
        var id: String = "",
        var duration: Long = 0,
        var maxDuration: Long = 0,
        var type: AppLog.ImpressionCellType,
        var isTracking: Boolean = false,
        var enterTime: Long = 0,
        var trackingStartTime: Long = 0
) : BaseBean()
