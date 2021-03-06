package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Tag(
        @Expose @SerializedName("color") var color: String = "",
        @Expose @SerializedName("name") var name: String = "",
        @Expose @SerializedName("id") var id: Int = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelTag.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelTag.writeToParcel(this, dest, flags)
    }

}