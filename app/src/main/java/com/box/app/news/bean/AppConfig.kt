package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class AppConfig(
        @Expose @SerializedName("disable_detail_preload") var disableDetailPreload: Boolean = false,
        @Expose @SerializedName("shms") var shms: Int = 0,
        @Expose @SerializedName("ad") var ad: Ad? = null,
        @Expose @SerializedName("auto_clean_cache_min_interval") var autoCleanCacheMinInterval: Long = 259200001,
        @Expose @SerializedName("ab_info") var abInfo: AbInfo = AbInfo(),
        @Expose @SerializedName("enable_not_interested") var enableNotInterested: Boolean = false,
        @Expose @SerializedName("detail_min_refresh_interval") var detailMinRefreshInterval: Long = 3600L,
        @Expose @SerializedName("matches_rool_interval") var matches_rool_interval: Double = 2.9,
        @Expose @SerializedName("cover_flow_rool_interval") var cover_flow_rool_interval: Double = 4.1,
        @Expose @SerializedName("domain_map") var domainMap: ArrayList<AppConfigDomainMap> = arrayListOf(),
        @Expose @SerializedName("worldcup_disable") var worldcup_disable: Boolean = true,
        @Expose @SerializedName("worldcup_calendar_enterance_disable") var worldcup_calendar_enterance_disable: Boolean = false,
        @Expose @SerializedName("worldcup_calendar_enterance_url") var worldcup_calendar_enterance_url: String = "",
        @Expose @SerializedName("show_headline") var showHeadline: Boolean = true //TODO 调试默认写成true
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAppConfig.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAppConfig.writeToParcel(this, dest, flags)
    }
}

