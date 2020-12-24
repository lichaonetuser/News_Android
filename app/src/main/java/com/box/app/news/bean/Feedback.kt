package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class Feedback(
        @Expose @SerializedName("ctime") var ctime: Long = 0,
        @Expose @SerializedName("image") var image: ImageInfo = ImageInfo(),
        @Expose @SerializedName("content") var content: String = "",
        @Expose @SerializedName("avatar_url") var avatarUrl: String = "",
        @Expose @SerializedName("type") var type: Int = 0,
        @Expose @SerializedName("id") var id: Int = 0
) : BaseBean(),Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelFeedback.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelFeedback.writeToParcel(this, dest, flags)
    }
}

