package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class PLinkResponse(@Expose @SerializedName("open_url") var openUrl: String? = null,
                         @Expose @SerializedName("delay") var delaySeconds: Int = 0,
                         @Expose @SerializedName("image") var image: String? = null,
                         @Expose @SerializedName("title") var title: String? = null)
    : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelPLinkResponse.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelPLinkResponse.writeToParcel(this, dest, flags)
    }
}

