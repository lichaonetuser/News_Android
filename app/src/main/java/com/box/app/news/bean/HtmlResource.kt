package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class HtmlResource(
        @Expose @SerializedName("url") var url: String = "",
        @Expose @SerializedName("md5") var md5: String = ""
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelHtmlResource.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelHtmlResource.writeToParcel(this, dest, flags)
    }
}