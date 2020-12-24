package com.box.common.core.net.http.bean

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class APIResponse<T>(
        @Expose @SerializedName("status") var status: Int = 0,
        @Expose @SerializedName("message") var message: String = "",
        @Expose @SerializedName("error") var error: String = "",
        @Expose @SerializedName("data") var data: T? = null
) {
    fun isOk(): Boolean {
        return status == 1
    }
}