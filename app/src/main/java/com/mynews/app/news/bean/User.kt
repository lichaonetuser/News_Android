package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class User(
        @Expose @SerializedName("unique_device_id") var uniqueDeviceId: String = "",
        @Expose @SerializedName("e_flag") var eFlag: String? = null,
        @Expose @SerializedName("interest") var interest: Interest?,
        @Expose @SerializedName("gender") var gender: Map<String, String>? = null

) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelUser.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelUser.writeToParcel(this, dest, flags)
    }
}