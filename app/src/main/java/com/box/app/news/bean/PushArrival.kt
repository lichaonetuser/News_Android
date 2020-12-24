package com.box.app.news.bean

import com.box.common.core.environment.EnvIOSApp
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class PushArrival(
        @Expose @SerializedName("logs") var logs: ArrayList<PushArrivalLog> = arrayListOf(),
        @Expose @SerializedName("phonetype") var phonetype: String = EnvIOSApp.phonetype,
        @Expose @SerializedName("osv") var osv: String = EnvIOSApp.osv,
        @Expose @SerializedName("v") var v: String = EnvIOSApp.v
)