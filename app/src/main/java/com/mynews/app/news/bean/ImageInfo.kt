package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class ImageInfo(
        @Expose @SerializedName("width") var width: Int = 0,
        @Expose @SerializedName("type") var type: Int = 0,
        @Expose @SerializedName("uri") var uri: String = "",
        @Expose @SerializedName("urls") var urls: MutableList<String> = mutableListOf<String>(),
        @Expose @SerializedName("height") var height: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelImageInfo.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelImageInfo.writeToParcel(this, dest, flags)
    }
}