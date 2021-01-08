package com.mynews.app.news.bean

import com.mynews.app.news.util.UDIDUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

data class PushArrivalLog(
        @Expose @SerializedName("unique_device_id") var uniqueDeviceId: String = UDIDUtils.getUniqueDeviceIdPair().second,
        @Expose @SerializedName("timestamp") var timestamp: Long = System.currentTimeMillis(),
        @Expose @SerializedName("log_id") var logId: String = UUID.randomUUID().toString(),
        @Expose @SerializedName("open_url") var openUrl: String = "",
        @Expose @SerializedName("push_id") var pushId: String = ""
)