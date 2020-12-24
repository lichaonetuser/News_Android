package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class PushMessage(
        @Expose @SerializedName("id") var id: String = "",
        @Expose @SerializedName("title") var title: String = "",
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("image") var image: String = "",
        @Expose @SerializedName("style") var style: Int = 0,
        @Expose @SerializedName("items") var items: ArrayList<PushMessageLockScreenItem>? = null,
        @Expose @SerializedName("lock_screen") var lockScreen: PushMessageLockScreen? = null,
        @Expose @SerializedName("clear") var clear: Int? = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPushMessage.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPushMessage.writeToParcel(this, dest, flags)
    }
}