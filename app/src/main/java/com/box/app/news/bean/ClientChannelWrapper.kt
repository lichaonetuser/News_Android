package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

/**
 *
 */
@PaperParcel
data class ClientChannelWrapper(
        @Expose @SerializedName("channels") var channels:MutableList<String> = mutableListOf(),
        @Expose @SerializedName("mtime") var mtime:Long = 0L
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelClientChannelWrapper.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelClientChannelWrapper.writeToParcel(this, dest, flags)
    }
}