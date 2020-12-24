package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel


@PaperParcel
data class WorldcupBannerNews(
        @Expose @SerializedName("open_url") var open_url: String = "",
        @Expose @SerializedName("title") var title: String = "",
        @Expose @SerializedName("cover_image") var cover_image: String = "",
        @Expose @SerializedName("tags") var tags: ArrayList<Tag> = arrayListOf(),
        @Expose @SerializedName("is_video") var is_video: Boolean = true,
        @Expose @SerializedName("is_gif") var is_gif: Boolean = false
        ) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelWorldcupBannerNews.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelWorldcupBannerNews.writeToParcel(this, dest, flags)
    }
}