package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class SubmitPushToken(
        @Expose @SerializedName("token") var token: String = "",
        @Expose @SerializedName("enabled") var enabled: Int = 1
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelUser.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelSubmitPushToken.writeToParcel(this, dest, flags)
    }
}