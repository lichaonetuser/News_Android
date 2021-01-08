package com.mynews.app.news.analytics

import com.mynews.app.news.item.CoverFlowBannerItem
import com.mynews.app.news.item.VideoPlayableItem
import com.mynews.app.news.item.WorldCupMatchBannerItem
import com.mynews.app.news.item.WorldCupTeamVideoListItem
import com.mynews.app.news.proto.AppLog
import com.mynews.common.core.analytics.AnalyticsManager
import com.mynews.common.extension.widget.recycler.item.BaseItem

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