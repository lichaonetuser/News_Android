package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Share(
        @Expose @SerializedName("url") var url: String? = null,
        @Expose @SerializedName("image") var image: String? = null,
        @Expose @SerializedName("desc") var desc: String? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelShare.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelShare.writeToParcel(this, dest, flags)
    }
}