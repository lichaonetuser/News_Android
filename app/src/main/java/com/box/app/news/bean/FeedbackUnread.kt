package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel

@PaperParcel
data class FeedbackUnread(
        @Expose @SerializedName("has_unread") var hasUnread: Boolean = false
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelFeedbackUnread.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelFeedbackUnread.writeToParcel(this, dest, flags)
    }
}

