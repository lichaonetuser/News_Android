package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class AppLogConfig(
        @Expose @SerializedName("report_fail_base_interval") var reportFailBaseInterval: Int = 10,
        @Expose @SerializedName("once_max_count") var onceMaxCount: Int = 100, //一次最大发送的数据
        @Expose @SerializedName("max_retry_count") var maxRetryCount: Int = 4, //最大重试次数
        @Expose @SerializedName("polling_interval") var pollingInterval: Int = 30, //轮询间隔，秒
        @Expose @SerializedName("fetch_setting_interval") var fetchSettingInterval: Int = 60,//获得配置的间隔
        @Expose @SerializedName("disable_report_error") var disableReportError: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelAppLogConfig.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelAppLogConfig.writeToParcel(this, dest, flags)
    }
}