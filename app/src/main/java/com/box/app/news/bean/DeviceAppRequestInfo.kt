package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.box.common.core.divice.DeviceAppInfo
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class DeviceAppRequestInfo(
        @Expose @SerializedName("installed_app") var installedApp: List<DeviceAppInfo> = mutableListOf(),
        @Expose @SerializedName("running_processes") var runningProcesses: List<DeviceAppInfo> = mutableListOf(),
        @Expose @SerializedName("type") var type: Int = DataDictionary.DeviceAppRequestInfoType.ALL.value
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelDeviceAppRequestInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelDeviceAppRequestInfo.writeToParcel(this, dest, flags)
    }
}