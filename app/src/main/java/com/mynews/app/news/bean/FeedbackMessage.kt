package com.mynews.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.mynews.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class FeedbackMessage(
        @Expose @SerializedName("content") var content: String? = null,
        @Expose @SerializedName("contact") var contact: String? = null,
        @Expose @SerializedName("image_uri") var imageUri: String? = null,
        @Expose @SerializedName("image_width") var imageWidth: Int? = null,
        @Expose @SerializedName("image_height") var imageHeight: Int? = null
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelFeedbackMessage.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelFeedbackMessage.writeToParcel(this, dest, flags)
    }
}

