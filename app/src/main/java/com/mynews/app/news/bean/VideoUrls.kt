package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
class VideoUrls(@Expose @SerializedName("hd_url") var hdUrl: String = "",
                @Expose @SerializedName("sd_url") var sdUrl: String = "",
                @Expose @SerializedName("normal_url") var normalUrl: String = "")
    : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelVideoUrls.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelVideoUrls.writeToParcel(this, dest, flags)
    }
}


