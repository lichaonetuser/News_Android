package com.box.app.news.analytics

import com.box.app.news.applog.AppLogKey
import com.box.app.news.bean.Channel
import com.box.app.news.bean.WorldcupVideo
import com.box.app.news.data.DataDictionary
import com.box.app.news.item.CoverFlowBannerItem
import com.box.app.news.item.VideoPlayableItem
import com.box.app.news.item.WorldCupMatchBannerItem
import com.box.app.news.item.WorldCupTeamVideoListItem
import com.box.app.news.proto.AppLog
import com.box.common.core.analytics.AnalyticsManager
import com.box.common.core.analytics.platform.AppsFlyerAnalytics
import com.box.common.core.analytics.platform.FirebaseAnalytics
import com.box.common.core.analytics.platform.UmengAnalytics
import com.box.common.core.log.Logger
import com.box.common.extension.widget.recycler.item.BaseItem

object AnalyticsHelper {

    fun getNewsItemAnalyticsMap(analyticsKey: String,
                                refer: AppLog.Refer,
                                positionRefer: AppLog.Refer,
                                positionSourceRefer: AppLog.Refer)
            : (List<BaseItem<*, *>>) -> MutableList<BaseItem<*, *>> {
        return { t: List<BaseItem<*, *>> ->
            t.map {
                if (it is VideoPlayableItem) {
                    it.mAnalyticsKey = analyticsKey
                    it.mPositionSourceRefer = positionSourceRefer
                    it.mPositionRefer = positionRefer
                }
                if (it is WorldCupMatchBannerItem) {
                    it.mAnalyticsKey = analyticsKey
                    it.mPositionSourceRefer = positionSourceRefer
                    it.mPositionRefer = positionRefer
                }
                if (it is CoverFlowBannerItem) {
                    it.mAnalyticsKey = analyticsKey
                }
                if (it is WorldCupTeamVideoListItem) {
                    it.mAnalyticsKey = analyticsKey
                    it.mRefer = refer
                }
                it
            }.toMutableList()
        }
    }

    fun logLoginEvent(event: String, vararg parameters: String,
                      onAppsFlyer: Boolean = AnalyticsManager.defaultOnAppsFlyer,
                      onUmeng: Boolean = AnalyticsManager.defaultOnUmeng,
                      onFirebase: Boolean = AnalyticsManager.defaultOnFirebase) {
        AnalyticsManager.logEvent(onUmeng = onUmeng, onAppsFlyer = onAppsFlyer, onFirebase = onFirebase,
                event = AnalyticsKey.Event.ACCOUNT,
                parameters = *parameters)
        AnalyticsManager.logEvent(onUmeng = onUmeng, onAppsFlyer = onAppsFlyer, onFirebase = onFirebase,
                event = event,
                parameters = *parameters)
    }

}