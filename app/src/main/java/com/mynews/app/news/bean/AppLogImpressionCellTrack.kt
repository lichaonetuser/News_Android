package com.mynews.app.news.bean

import com.mynews.app.news.bean.base.BaseBean
import com.mynews.app.news.proto.AppLog

data class AppLogImpressionCellTrack(
        var id: String = "",
        var type: AppLog.ImpressionType = AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST
) : BaseBean()