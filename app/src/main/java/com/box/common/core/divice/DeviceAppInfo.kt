package com.box.common.core.divice

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class DeviceAppInfo(
        @Expose @SerializedName("app_name") var appName: String = "",
        @Expose @SerializedName("application_flags") var applicationFlags: Int = -1,
        @Expose @SerializedName("package_name") var packageName: String = "",
        @Expose @SerializedName("process_name") var processName: String = "",
        @Expose @SerializedName("first_install_time") var firstInstallTime: Long = -1L,
        @Expose @SerializedName("last_update_time") var lastUpdateTime: Long = -1L,
        @Expose @SerializedName("version_code") var versionCode: Int = -1,
        @Expose @SerializedName("version_name") var versionName: String = ""
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelDeviceAppInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelDeviceAppInfo.writeToParcel(this, dest, flags)
    }
}