package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class PLinkRequest(@Expose @SerializedName("type") var type: Int = DataDictionary.DeepLinkReportType.LAUNCH_FETCH.value,
                        @Expose @SerializedName("json") var jsonMap: Map<String, String?>? = null,
                        @Expose @SerializedName("url") var url: String? = null)
    : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPLinkRequest.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPLinkRequest.writeToParcel(this, dest, flags)
    }
}


