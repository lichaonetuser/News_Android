package com.box.app.news.bean

import com.box.app.news.bean.base.BaseBean
import com.box.app.news.proto.AppLog

data class AppLogImpressionCellTrack(
        var id: String = "",
        var type: AppLog.ImpressionType = AppLog.ImpressionType.IMPRESSION_ARTICLE_LIST
) : BaseBean()